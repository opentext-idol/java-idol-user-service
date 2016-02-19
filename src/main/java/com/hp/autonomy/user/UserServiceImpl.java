/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.user;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.types.idol.RolesResponseData;
import com.hp.autonomy.types.idol.Security;
import com.hp.autonomy.types.idol.Uid;
import com.hp.autonomy.types.idol.User;
import com.hp.autonomy.types.idol.UserDetails;
import com.hp.autonomy.types.idol.Users;
import com.hp.autonomy.types.requests.idol.actions.role.RoleActions;
import com.hp.autonomy.types.requests.idol.actions.role.params.RoleAddParams;
import com.hp.autonomy.types.requests.idol.actions.role.params.RoleAddUserToRoleParams;
import com.hp.autonomy.types.requests.idol.actions.role.params.RoleDeleteParams;
import com.hp.autonomy.types.requests.idol.actions.role.params.RoleGetUserListParams;
import com.hp.autonomy.types.requests.idol.actions.role.params.RoleRemoveUserFromRoleParams;
import com.hp.autonomy.types.requests.idol.actions.role.params.RoleUserGetRoleListParams;
import com.hp.autonomy.types.requests.idol.actions.user.UserActions;
import com.hp.autonomy.types.requests.idol.actions.user.params.SecurityParams;
import com.hp.autonomy.types.requests.idol.actions.user.params.UserAddParams;
import com.hp.autonomy.types.requests.idol.actions.user.params.UserDeleteParams;
import com.hp.autonomy.types.requests.idol.actions.user.params.UserEditParams;
import com.hp.autonomy.types.requests.idol.actions.user.params.UserReadParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation of {@link UserService}
 */
public class UserServiceImpl implements UserService {
    private final AciService aciService;
    private final ConfigService<? extends UserServiceConfig> userAdminConfig;
    final Processor<RolesResponseData> rolesProcessor;
    final Processor<Security> securityProcessor;
    final Processor<User> userProcessor;
    final Processor<UserDetails> userDetailsProcessor;
    final Processor<Users> usersProcessor;
    final Processor<Uid> uidProcessor;
    final Processor<Void> emptyProcessor;

    public UserServiceImpl(final ConfigService<? extends UserServiceConfig> config, final AciService aciService, final AciResponseJaxbProcessorFactory processorFactory) {
        userAdminConfig = config;
        this.aciService = aciService;
        rolesProcessor = processorFactory.createAciResponseProcessor(RolesResponseData.class);
        securityProcessor = processorFactory.createAciResponseProcessor(Security.class);
        userProcessor = processorFactory.createAciResponseProcessor(User.class);
        userDetailsProcessor = processorFactory.createAciResponseProcessor(UserDetails.class);
        usersProcessor = processorFactory.createAciResponseProcessor(Users.class);
        uidProcessor = processorFactory.createAciResponseProcessor(Uid.class);
        emptyProcessor = processorFactory.createEmptyAciResponseProcessor();
    }

    @Override
    public List<UserRoles> getUsersRoles() {
        return createUserRoles(getRoles(), false);
    }

    @Override
    public List<UserRoles> getUsersRoles(final String role) {
        return getUsersRoles(Collections.singletonList(role));
    }

    @Override
    public List<UserRoles> getUsersRoles(final List<String> roles) {
        return createUserRoles(retainExistingRoles(roles), false);
    }

    @Override
    public List<UserRoles> getUsersRolesExcept(final List<String> rolesExcept) {
        final List<String> existingRoles = getRoles();
        existingRoles.removeAll(rolesExcept);
        return createUserRoles(existingRoles, false);
    }

    @Override
    public List<UserRoles> getAllUsersWithRoles(final List<String> roles) {
        return createUserRoles(retainExistingRoles(roles), true);
    }

    @Override
    public UserRoles getUser(final String username) {
        final User user = getUserDetails(username);

        final long uid = user.getUid();
        return new UserRoles(username, uid, user.getSecurityinfo(), getUserRole(uid));
    }

    @Override
    public User getUserDetails(final String username) {
        final AciParameters parameters = new AciParameters(UserActions.UserRead.name());
        parameters.add(UserReadParams.UserName.name(), username);
        parameters.add(UserReadParams.SecurityInfo.name(), true);

        return aciService.executeAction(getCommunity(), parameters, userProcessor);
    }

    @Override
    public long addUser(final String username, final String password) {
        final AciParameters parameters = new AciParameters(UserActions.UserAdd.name());
        parameters.add(UserAddParams.UserName.name(), username);
        parameters.add(UserAddParams.Password.name(), password);

        return aciService.executeAction(getCommunity(), parameters, uidProcessor).getUid();
    }

    @Override
    public long addUser(final String username, final String password, final String role) {
        final long uid = addUser(username, password);
        addUserToRole(uid, role);
        return uid;
    }

    @Override
    public void deleteUser(final long uid) {
        final AciParameters parameters = new AciParameters(UserActions.UserDelete.name());
        parameters.add(UserDeleteParams.UID.name(), uid);
        aciService.executeAction(getCommunity(), parameters, emptyProcessor);
    }

    @Override
    public void resetPassword(final long uid, final String password) {
        final AciParameters parameters = new AciParameters(UserActions.UserEdit.name());
        parameters.add(UserEditParams.UID.name(), uid);
        parameters.add(UserEditParams.ResetPassword.name(), true);
        parameters.add(UserEditParams.NewPassword.name(), password);
        aciService.executeAction(getCommunity(), parameters, emptyProcessor);
    }

    @Override
    public List<String> getUserRole(final long uid) {
        final AciParameters parameters = new AciParameters(RoleActions.RoleUserGetRoleList.name());
        parameters.add(RoleUserGetRoleListParams.UID.name(), uid);

        return aciService.executeAction(getCommunity(), parameters, rolesProcessor).getRole();
    }

    @Override
    public List<String> getRoles() {
        final Set<AciParameter> parameters = new AciParameters(RoleActions.RoleGetRoleList.name());
        return aciService.executeAction(getCommunity(), parameters, rolesProcessor).getRole();
    }

    @Override
    public void addRole(final String role) {
        final AciParameters parameters = new AciParameters(RoleActions.RoleAdd.name());
        parameters.add(RoleAddParams.RoleName.name(), role);
        aciService.executeAction(getCommunity(), parameters, emptyProcessor);
    }

    @Override
    public void addUserToRole(final long uid, final String role) {
        final AciParameters parameters = new AciParameters(RoleActions.RoleAddUserToRole.name());
        parameters.add(RoleAddUserToRoleParams.RoleName.name(), role);
        parameters.add(RoleAddUserToRoleParams.UID.name(), uid);

        aciService.executeAction(getCommunity(), parameters, emptyProcessor);
    }

    @Override
    public void removeUserFromRole(final long uid, final String role) {
        final AciParameters parameters = new AciParameters(RoleActions.RoleRemoveUserFromRole.name());
        parameters.add(RoleRemoveUserFromRoleParams.RoleName.name(), role);
        parameters.add(RoleRemoveUserFromRoleParams.UID.name(), uid);

        aciService.executeAction(getCommunity(), parameters, emptyProcessor);
    }

    @Override
    public void removeRole(final String role) {
        final AciParameters parameters = new AciParameters(RoleActions.RoleDelete.name());
        parameters.add(RoleDeleteParams.RoleName.name(), role);
        aciService.executeAction(getCommunity(), parameters, emptyProcessor);
    }

    @Override
    public boolean authenticateUser(final String username, final String password, final String method) {
        final AciParameters parameters = new AciParameters(UserActions.Security.name());
        parameters.put(SecurityParams.UserName.name(), username);
        parameters.put(SecurityParams.Password.name(), password);
        parameters.put(SecurityParams.Repository.name(), method);

        return aciService.executeAction(getCommunity(), parameters, securityProcessor).isAuthenticate();
    }

    /**
     * <p>If includeEmpty is false, returns a list of UserRoles containing only users with one or more of the roles
     * listed in roleList. If it is true, the list also includes users without any of the given roles. In either case,
     * the UserRoles' roles lists only contain roles contained in roleList.</p>
     * <p>Given a role list, it gets all the users belonging to each role then extracts uids from UserReadUserListDetails.
     * This should minimize the number of calls to community since it's most likely that num(users) >> num(roles).</p>
     *
     * @param roleList     List of roles
     * @param includeEmpty Whether to include users who have none of the roles in roleList.
     * @return List of users and uids, with roles taken from roleList.
     */
    private List<UserRoles> createUserRoles(final Iterable<String> roleList, final boolean includeEmpty) {
        final List<User> userDetails = getUsers();
        final Map<String, List<String>> userNamesRolesMap = createUsernameRolesMap(roleList);
        final List<UserRoles> userRoles = new ArrayList<>();

        for (final User user : userDetails) {
            final String username = user.getUsername();
            final long uid = user.getUid();
            final String securityInfo = user.getSecurityinfo();
            final List<String> roles = userNamesRolesMap.get(username);

            if (roles != null) {
                userRoles.add(new UserRoles(username, uid, securityInfo, roles));
            } else if (includeEmpty) {
                userRoles.add(new UserRoles(username, uid, securityInfo, new ArrayList<String>()));
            }
        }

        return userRoles;
    }

    // Returns a map of username to list of role. Only users with at least one of the roles in the roleList are returned,
    // and only the roles contained in the roleList are included in the role lists.
    private Map<String, List<String>> createUsernameRolesMap(final Iterable<String> roleList) {
        final Map<String, List<String>> usersRolesMap = new HashMap<>();

        for (final String role : roleList) {
            for (final String user : getUsersWithRole(role)) {
                if (usersRolesMap.containsKey(user)) {
                    if (!usersRolesMap.get(user).contains(role)) {
                        usersRolesMap.get(user).add(role);
                    }
                } else {
                    final List<String> list = new ArrayList<>(Collections.singletonList(role));
                    usersRolesMap.put(user, list);
                }
            }
        }

        return usersRolesMap;
    }

    // Returns a list of roles present in both the roles parameter and in community
    private Iterable<String> retainExistingRoles(final Collection<String> roles) {
        final List<String> existingRoles = getRoles();
        existingRoles.retainAll(roles);
        return existingRoles;
    }

    private List<User> getUsers() {
        final Set<AciParameter> parameters = new AciParameters(UserActions.UserReadUserListDetails.name());
        return aciService.executeAction(getCommunity(), parameters, userDetailsProcessor).getUser();
    }

    private Iterable<String> getUsersWithRole(final String role) {
        final AciParameters parameters = new AciParameters(RoleActions.RoleGetUserList.name());
        parameters.add(RoleGetUserListParams.RoleName.name(), role);
        return aciService.executeAction(getCommunity(), parameters, usersProcessor).getUser();
    }

    private AciServerDetails getCommunity() {
        return userAdminConfig.getConfig().getCommunityDetails();
    }
}