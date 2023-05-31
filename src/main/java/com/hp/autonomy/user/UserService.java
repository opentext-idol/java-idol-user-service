/*
 * Copyright 2013-2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.user;

import com.hp.autonomy.types.idol.responses.*;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public interface UserService {

    /**
     * Get a list of fields used across all users.
     */
    UserFieldNamesResponse getAllFieldNames();

    /**
     * Get all the users with all of their respective roles.
     *
     * @return A list of users, uids and all of their respective roles
     */
    List<UserRoles> getUsersRoles();

    /**
     * Get all the users belonging to a specific role. Each UserRoles' roles will only contain this role.
     *
     * @param role Only return users with this role
     * @return A list of users, uids and their respective roles
     */
    List<UserRoles> getUsersRoles(String role);

    /**
     * Get all the users belonging to one or more of a list of roles. Each UserRoles' roles will only contain one or
     * more of the roles specified in the roles list.
     *
     * @param roles Only return users belonging to one or more of these roles
     * @return A list of users, uids and their respective roles
     */
    List<UserRoles> getUsersRoles(List<String> roles);

    /**
     * Get all the users who have at least one role other than the roles listed in rolesExcept. No UserRoles' roles will
     * contain a role contained in rolesExcept, even if they have those roles in community.
     *
     * @param rolesExcept Roles to discard
     * @return A list of users, uids and their respective roles
     */
    List<UserRoles> getUsersRolesExcept(List<String> rolesExcept);

    /**
     * Get all of the users and uids in community as a list of UserRoles. The UserRoles' roles will only contain zero or
     * more of the roles listed in roles.
     *
     * @param roles The roles to include in the UserRoles' roles list
     * @return A list of users, uids and their respective roles
     */
    List<UserRoles> getAllUsersWithRoles(List<String> roles);

    /**
     * Get a paged portion of the users and uids in community as a list of UserRoles. The UserRoles' roles will only contain zero or
     * more of the roles listed in roles.
     *
     * @param roles The roles to include in the UserRoles' roles list
     * @param query the filter string to filter by
     * @param pageSize how many users to fetch
     * @param pageStart what page we're on, starts from 1b
     * @param rolename a single role to restrict to.
     * @return An object containing a count and a list of users, uids and their respective roles
     */
    PagedUserRoles getAllUsersWithRoles(List<String> roles, String query, int pageSize, int pageStart, String rolename);

    /**
     * Get a user, its uid and all of its roles.
     *
     * @param username The user to fetch
     * @return The user and its roles
     */
    UserRoles getUser(String username);

    /**
     * Get a user, its uid and all of its roles.
     *
     * @param username The user to fetch
     * @param deferLogin True if the user should be created if it is not found
     * @return The user and its roles
     */
    default UserRoles getUser(String username, boolean deferLogin) {
        throw new UnsupportedOperationException("Default implementation of method");
    }

    /**
     * Get a user, its uid, all of its roles, and security info.
     *
     * @param username The user to fetch
     * @param deferLogin True if the user should be created if it is not found
     * @param password password used for authentication.  If null, then CheckEntitlement must be
     *                 configured to false
     * @return The user and its roles
     */
    default UserRoles getUser(String username, boolean deferLogin, String password) {
        throw new UnsupportedOperationException("Default implementation of method");
    }

    /**
     * Get the user details for a given username.
     *
     * @param username The user to fetch
     * @return The user details, or null if the user is not found
     */
    User getUserDetails(String username);

    /**
     * Get the user details for a given user uid.
     *
     * @param uid The identifier for the user to fetch
     * @return The user details, or null if the user is not found
     */
    User getUserDetails(Long uid);

    /**
     * Get the user details for a given username.
     * @param username The user to fetch
     * @param deferLogin True if the user should be created if it is not found
     * @return The user details, or null if the user is not found
     */
    default User getUserDetails(final String username, final boolean deferLogin) {
        throw new UnsupportedOperationException("Default implementation of method");
    }

    /**
     * Get the user details for a given username, including security info.
     * @param username The user to fetch
     * @param deferLogin True if the user should be created if it is not found
     * @param password user password.  If null, then CheckEntitlement must be configured to false
     * @return The user details, or null if the user is not found
     */
    default User getUserDetails(String username, boolean deferLogin, String password) {
        throw new UnsupportedOperationException("Default implementation of method");
    }

    /**
     * Get details for multiple users.  This is the same as calling {@link #getUserDetails(String)}
     * for each user in turn, but faster.
     */
    List<User> getUsersDetails(List<String> usernames);

    /**
     * Delete a specific user.
     *
     * @param uid The uid for the the user
     */
    void deleteUser(long uid);

    /**
     * Adds a new user.
     *
     * @param username User name
     * @param password Password
     * @return user id
     */
    long addUser(String username, String password);

    /**
     * Adds a new user and it is assigned to the specified role
     *
     * @param username user name
     * @param password user password
     * @param role     user role
     * @return user id
     */
    long addUser(String username, String password, String role);

    /**
     * Resets a user password
     *
     * @param uid      to reset
     * @param password new password
     */
    void resetPassword(long uid, String password);

    /**
     * Get all of the roles to which a user belongs
     *
     * @param uid Specify the user by uid
     * @return A list of roles to which the user belongs
     */
    List<String> getUserRole(long uid);

    /**
     * Get all of the roles to which a user belongs
     * @param username Specify the user by username
     * @return A list of roles to which the user belongs
     */
    List<String> getUserRole(String username);

    /**
     * Get all the roles in community
     *
     * @return The list of roles
     */
    List<String> getRoles();

    /**
     * Adds a new role
     *
     * @param role To add
     */
    void addRole(String role);

    /**
     * Adds a user to a role
     *
     * @param uid  To associate to a role
     * @param role Name of the role that will be associated to the user
     */
    void addUserToRole(long uid, String role);

    /**
     * Removes a user from a role
     *
     * @param uid  User to remove from a role
     * @param role Role name that will be disassociated from the user
     */
    void removeUserFromRole(long uid, String role);

    /**
     * Remove a role from community
     *
     * @param role to remove
     */
    void removeRole(String role);

    /**
     * Authenticate the user against community
     *
     * @param username user name
     * @param password user password
     * @param method   login method
     * @return true if the authentication went well, false otherwise
     */
    boolean authenticateUser(String username, String password, String method);

    /**
     * Searches the users list in community.
     * @param searchText the string to search for
     * @param startUser the list number of the first user
     * @param maxUsers the maximum number of users to return
     * @return a list of users
     */
    UserDetails searchUsers(String searchText, int startUser, int maxUsers);

    /**
     * Get the user profiles of a user
     * @param user the username
     * @return a list of profiles
     */
    Profiles profileRead(String user);

    /**
     * Profile the user
     * @param user the username
     * @param reference the document reference
     * @return the edited profile
     */
    ProfileUser profileUser(String user, String reference);

    /**
     * Find users related to a search via profiles.  Uses the Community server's AgentStore.
     *
     * @param agentStoreProfilesDatabase database in AgentStore containing user profiles
     * @param namedArea named area within the profiles database to use
     * @param searchText find profiles similar to this text
     * @param start number of the first result to return, starting from 1
     * @param maxProfiles maximum number of results to return (before limiting using `start`
     * @return Search results, as documents representing user profiles.  Documents will generally
     *         have a USERNAME or NAME field.  The same user may occur multiple times, via different
     *         profiles.
     */
    QueryResponseData getRelatedToSearch(
        String agentStoreProfilesDatabase,
        String namedArea,
        String searchText,
        int start,
        int maxProfiles
    );

}
