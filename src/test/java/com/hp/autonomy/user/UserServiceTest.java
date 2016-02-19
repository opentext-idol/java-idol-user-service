package com.hp.autonomy.user;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private ConfigService<UserServiceConfig> configService;

    @Mock
    private AciResponseJaxbProcessorFactory processorFactory;

    @Mock
    private AciService aciService;

    @Mock
    private AciServerDetails aciServerDetails;

    @Mock
    private UserServiceConfig config;

    private UserService userService;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(config);
        when(config.getCommunityDetails()).thenReturn(aciServerDetails);

        userService = new UserServiceImpl(configService, aciService, processorFactory);
    }

    @Test
    public void addUser() {
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class))).thenReturn(createUid(1L));
        assertEquals(1, userService.addUser("user1", "password"));
    }

    @Test
    public void addUserWithRole() {
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class))).thenReturn(createUid(1L));
        assertEquals(1, userService.addUser("user1", "password", "role"));
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1L);
        verify(aciService).executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class));
    }

    @Test
    public void getUserDetails() {
        final String username = "user1";
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class))).thenReturn(createUser(username, 1L));
        assertNotNull(userService.getUserDetails(username));
    }

    @Test
    public void resetPassword() {
        userService.resetPassword(1L, "password");
        verify(aciService).executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class));
    }

    @Test
    public void getUserRole() {
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class))).thenReturn(mockRolesResponse(Collections.singletonList("SomeRole")));
        assertThat(userService.getUserRole(1L), hasSize(1));
    }

    @Test
    public void getRoles() {
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class))).thenReturn(mockRolesResponse(Collections.singletonList("SomeRole")));
        assertThat(userService.getRoles(), hasSize(1));
    }

    @Test
    public void addRole() {
        userService.addRole("SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class));
    }

    @Test
    public void addUserToRole() {
        userService.addUserToRole(1L, "SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class));
    }

    @Test
    public void removeUserFromRole() {
        userService.removeUserFromRole(1L, "SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class));
    }

    @Test
    public void removeRole() {
        userService.removeRole("SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class));
    }

    @Test
    public void authenticateUser() {
        final Security security = new Security();
        security.setAuthenticate(true);
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class))).thenReturn(security);
        assertTrue(userService.authenticateUser("user1", "password", "repository"));
    }

    @Test
    public void getUser() {
        final String username = "user1";
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class)))
                .thenReturn(createUser(username, 1L), mockRolesResponse(Collections.singletonList("SomeRole")));
        final UserRoles userRoles = userService.getUser(username);
        assertNotNull(userRoles);
        assertThat(userRoles.getRoles(), hasSize(1));
    }

    @Test
    public void getUsersRoles() {
        mockUserRoles(Collections.singletonList("Role1"));
        final List<UserRoles> usersRoles = userService.getUsersRoles();
        assertThat(usersRoles, hasSize(1));
    }

    @Test
    public void getUsersRolesByRole() {
        mockUserRoles(Collections.singletonList("Role1"));
        final List<UserRoles> usersRoles = userService.getUsersRoles("Role1");
        assertThat(usersRoles, hasSize(1));
    }

    @Test
    public void getUsersRolesExcept() {
        mockUserRoles(Arrays.asList("Role1", "Role2"));
        final List<UserRoles> usersRoles = userService.getUsersRolesExcept(Collections.singletonList("Role1"));
        assertThat(usersRoles, hasSize(1));
    }

    @Test
    public void getAllUsersWithRoles() {
        mockUserRoles(Arrays.asList("Role1", "Role2", "Role3", "Role4"));
        final List<UserRoles> usersRoles = userService.getAllUsersWithRoles(Arrays.asList("Role1", "Role2", "Role3"));
        assertThat(usersRoles, hasSize(3));
    }

    private void mockUserRoles(final Collection<String> roles) {
        final RolesResponseData rolesResponseData = mockRolesResponse(roles);
        final UserDetails userDetails = mockUserDetailsResponse(Arrays.asList(createUser("user1", 1L), createUser("user2", 2L), createUser("user3", 3L)));
        final Users usersForFirstRole = mockUsersResponse(Collections.singletonList("user1"));
        final Users usersForSecondRole = mockUsersResponse(Arrays.asList("user1", "user2"));
        final Users usersForThirdRole = mockUsersResponse(Collections.<String>emptyList());
        when(aciService.executeAction(any(AciServerDetails.class), any(AciParameters.class), any(Processor.class)))
                .thenReturn(rolesResponseData, userDetails, usersForFirstRole, usersForSecondRole, usersForThirdRole);
    }

    private Uid createUid(final long value) {
        final Uid uid = new Uid();
        uid.setUid(value);
        return uid;
    }

    private User createUser(final String username, final long uuid) {
        final User user = new User();
        user.setUid(uuid);
        user.setUsername(username);
        user.setMaxagents(1);

        return user;
    }

    private Users mockUsersResponse(final Collection<String> userList) {
        final Users users = new Users();
        users.getUser().addAll(userList);
        return users;
    }

    private UserDetails mockUserDetailsResponse(final Collection<User> userList) {
        final UserDetails users = new UserDetails();
        users.getUser().addAll(userList);
        return users;
    }

    private RolesResponseData mockRolesResponse(final Collection<String> roles) {
        final RolesResponseData rolesResponseData = new RolesResponseData();
        rolesResponseData.getRole().addAll(roles);
        return rolesResponseData;
    }
}
