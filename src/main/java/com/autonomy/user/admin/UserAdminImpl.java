/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.user.admin;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.util.AciParameters;
import com.autonomy.user.admin.dto.RoleList;
import com.autonomy.user.admin.dto.Security;
import com.autonomy.user.admin.dto.Uid;
import com.autonomy.user.admin.dto.User;
import com.autonomy.user.admin.dto.UserList;
import com.autonomy.user.admin.dto.UserReadUserListDetailsUser;
import com.autonomy.user.admin.dto.UserRoles;
import com.hp.autonomy.frontend.configuration.ConfigService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdminImpl implements UserAdmin {

    private final AciService aciService;
    private final ConfigService<? extends UserAdminConfig> userAdminConfig;
    private final IdolAnnotationsProcessorFactory processorFactory;
    private static final String ROLE_NAME = "RoleName";
    private static final String USER_NAME = "UserName";
    private static final String UID = "Uid";

    public UserAdminImpl(final ConfigService<? extends UserAdminConfig> config, final AciService aciService, final IdolAnnotationsProcessorFactory processorFactory) {
        this.userAdminConfig = config;
        this.aciService = aciService;
        this.processorFactory = processorFactory;
    }

    @Override
    public List<UserRoles> getUsersRoles() {
        return createUserRoles(getRoles(), false);
    }

    @Override
    public List<UserRoles> getUsersRoles(final String role) {
        return getUsersRoles(Arrays.asList(role));
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
        return new UserRoles(username, uid, getUserRole(uid));
    }

    @Override
    public User getUserDetails(final String username) {
        final AciParameters parameters = new AciParameters("UserRead");
        parameters.add(USER_NAME, username);

        final List<User> users = aciService.executeAction(getCommunity(), parameters,
                processorFactory.listProcessorForClass(User.class));

        return !users.isEmpty() ? users.get(0) : null;
    }

    @Override
    public long addUser(final String username, final String password) {
        final AciParameters parameters = new AciParameters("UserAdd");
        parameters.add(USER_NAME, username);
        parameters.add("Password", password);

        final Uid uid = aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(Uid.class)).get(0);

        return uid.getUid();
    }

    @Override
    public long addUser(final String username, final String password, final String role) {
        final long uid = addUser(username, password);
        addUserToRole(uid, role);
        return uid;
    }

    @Override
    public void deleteUser(final long uid) {
        final AciParameters parameters = new AciParameters("UserDelete");
        parameters.add(UID, uid);
        aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(EmptyResponse.class));
    }

    @Override
    public void resetPassword(final long uid, final String password) {
        final AciParameters parameters = new AciParameters("UserEdit");
        parameters.add(UID, uid);
        parameters.add("ResetPassword", true);
        parameters.add("NewPassword", password);
        aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(EmptyResponse.class));
    }

    @Override
    public List<String> getUserRole(final long uid) {
        final AciParameters parameters = new AciParameters("RoleUserGetRoleList");
        parameters.add(UID, uid);

        final List<RoleList> list = aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(RoleList.class));

        return !list.isEmpty() ? list.get(0).getRoles() : null;
    }

    @Override
    public List<String> getRoles() {
        final AciParameters parameters = new AciParameters("RoleGetRoleList");

        final List<RoleList> list = aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(RoleList.class));

        return !list.isEmpty() ? list.get(0).getRoles() : null;
    }

    @Override
    public void addRole(final String role) {
        final AciParameters parameters = new AciParameters("RoleAdd");
        parameters.add(ROLE_NAME, role);
        aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(EmptyResponse.class));
    }

    @Override
    public void addUserToRole(final long uid, final String role) {
        final AciParameters parameters = new AciParameters("RoleAddUserToRole");
        parameters.add(ROLE_NAME, role);
        parameters.add(UID, uid);

        aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(EmptyResponse.class));
    }

    @Override
    public void removeUserFromRole(final long uid, final String role) {
        final AciParameters parameters = new AciParameters("RoleRemoveUserFromRole");
        parameters.add(ROLE_NAME, role);
        parameters.add(UID, uid);

        aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(EmptyResponse.class));
    }

    @Override
    public void removeRole(final String role) {
        final AciParameters parameters = new AciParameters("RoleDelete");
        parameters.add(ROLE_NAME, role);
        aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(EmptyResponse.class));
    }

    @Override
    public boolean authenticateUser(final String username, final String password, final String method) {
        final AciParameters parameters = new AciParameters("Security");
        parameters.put(USER_NAME, username);
        parameters.put("password", password);
        parameters.put("repository", method);

        return aciService.executeAction(
                getCommunity(),
                parameters,
                processorFactory.listProcessorForClass(Security.class)
        ).get(0).isAuthenticated();
    }

    /**
     * <p>If includeEmpty is false, returns a list of UserRoles containing only users with one or more of the roles
     * listed in roleList. If it is true, the list also includes users without any of the given roles. In either case,
     * the UserRoles' roles lists only contain roles contained in roleList.</p>
     * <p>Given a role list, it gets all the users belonging to each role then extracts uids from UserReadUserListDetails.
     * This should minimize the number of calls to community since it's most likely that num(users) >> num(roles).</p>
     *
     * @param roleList List of roles
     * @param includeEmpty Whether to include users who have none of the roles in roleList.
     * @return List of users and uids, with roles taken from roleList.
     */
    private List<UserRoles> createUserRoles(final List<String> roleList, final boolean includeEmpty) {
        final List<UserReadUserListDetailsUser> userDetails = getUsers();
        final Map<String, List<String>> usernamesRolesMap = createUsernameRolesMap(roleList);
        final List<UserRoles> userRoles = new ArrayList<>();

        for (final UserReadUserListDetailsUser user : userDetails) {
            final String username = user.getName();
            final long uid = user.getUid();
            final List<String> roles = usernamesRolesMap.get(username);

            if (roles != null) {
                userRoles.add(new UserRoles(username, uid, roles));
            } else if (includeEmpty) {
                userRoles.add(new UserRoles(username, uid, new ArrayList<String>()));
            }
        }

        return userRoles;
    }

    // Returns a map of username to list of role. Only users with at least one of the roles in the roleList are returned,
    // and only the roles contained in the roleList are included in the role lists.
    private Map<String, List<String>> createUsernameRolesMap(final List<String> roleList) {
        final Map<String, List<String>> usersRolesMap = new HashMap<>();

        for (final String role : roleList) {
            final UserList userList = getUsersWithRole(role);

            for (final String user : userList.getUserNames()) {
                if (usersRolesMap.containsKey(user)) {
                    if (!usersRolesMap.get(user).contains(role)) {
                        usersRolesMap.get(user).add(role);
                    }
                } else {
                    final List<String> list = new ArrayList<>(Arrays.asList(role));
                    usersRolesMap.put(user, list);
                }
            }
        }

        return usersRolesMap;
    }

    // Returns a list of roles present in both the roles parameter and in community
    private List<String> retainExistingRoles(final List<String> roles) {
        final List<String> existingRoles = getRoles();
        existingRoles.retainAll(roles);
        return existingRoles;
    }

    private List<UserReadUserListDetailsUser> getUsers() {
        final AciParameters parameters = new AciParameters("UserReadUserListDetails");
        return aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(UserReadUserListDetailsUser.class));
    }

    private UserList getUsersWithRole(final String role) {
        final AciParameters parameters = new AciParameters("RoleGetUserList");
        parameters.add(ROLE_NAME, role);
        return aciService.executeAction(getCommunity(), parameters, processorFactory.listProcessorForClass(UserList.class)).get(0);
    }

    private AciServerDetails getCommunity() {
        return userAdminConfig.getConfig().getCommunityDetails();
    }
}