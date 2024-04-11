package com.hp.autonomy.user;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.util.ActionParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.opentext.idol.types.marshalling.ProcessorFactory;
import com.opentext.idol.types.responses.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    private ConfigService<UserServiceConfig> configService;

    @Mock
    private ProcessorFactory processorFactory;

    @Mock
    private AciService aciService;

    @Mock private AciServerDetails communityAciServerDetails;
    @Mock private AciServerDetails agentStoreAciServerDetails;

    @Mock
    private UserServiceConfig config;

    private UserService userService;

    @Before
    public void setUp() {
        when(configService.getConfig()).thenReturn(config);
        when(config.getCommunityDetails()).thenReturn(communityAciServerDetails);
        when(config.getCommunityAgentStoreDetails()).thenReturn(agentStoreAciServerDetails);

        userService = new UserServiceImpl(configService, aciService, processorFactory);
    }

    @Test
    public void addUser() {
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(createUid(1L));
        assertEquals(1, userService.addUser("user1", "password"));
    }

    @Test
    public void addUserWithRole() {
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(createUid(1L));
        assertEquals(1, userService.addUser("user1", "password", "role"));
    }

    @Test
    public void deleteUser() {
        userService.deleteUser(1L);
        verify(aciService).executeAction(any(AciServerDetails.class), any(ActionParameters.class), any());
    }

    @Test
    public void getUserDetails() {
        final String username = "user1";
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(createUser(username, 1L));
        assertNotNull(userService.getUserDetails(username));
    }

    @Test
    public void getUserDetailsFromUid() {
        final Long uid = 1L;
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(createUser("user1", uid));
        assertNotNull(userService.getUserDetails(uid));
    }

    @Test
    public void getUsersDetails_allSafe() {
        final List<User> mockUsers = Arrays.asList(
            createUser("u1", 3), createUser("u2", 6), createUser("u3", 7)
        );
        when(aciService.executeAction(any(), any(), any()))
            .thenReturn(mockUserDetailsResponse(mockUsers));

        // note: u4 doesn't exist
        final List<User> results = userService.getUsersDetails(
            Arrays.asList("u1", "u2", "u3", "u4"));
        assertEquals(mockUsers, results);

        final ArgumentCaptor<ActionParameters> paramsCaptor = ArgumentCaptor.forClass(ActionParameters.class);
        verify(aciService).executeAction(
            any(AciServerDetails.class), paramsCaptor.capture(), any());
        final ActionParameters params = paramsCaptor.getValue();
        assertEquals("UserReadUserListDetails", params.get("action"));
        assertEquals("u1,u2,u3,u4", params.get("match"));
        assertEquals("4", params.get("maxusers"));
    }

    @Test
    public void getUsersDetails_someUnsafe() {
        when(aciService.executeAction(any(), any(), any())).thenAnswer(inv -> {
            final ActionParameters params = (ActionParameters) inv.getArguments()[1];
            final Object action = params.get("action");
            if (action.equals("UserReadUserListDetails")) {
                return mockUserDetailsResponse(
                    Arrays.asList(createUser("u1", 3), createUser("u2", 6)));
            } else {
                return createUser((String) params.get("username"), 99);
            }
        });

        final List<User> results = userService.getUsersDetails(
            Arrays.asList("u1", ",unsafe", "u2", "uns?afe", "unsafe*"));
        // User doesn't implement `equals`
        assertEquals(5, results.size());
        assertEquals("u1", results.get(0).getUsername());
        assertEquals("u2", results.get(1).getUsername());
        assertEquals(",unsafe", results.get(2).getUsername());
        assertEquals("uns?afe", results.get(3).getUsername());
        assertEquals("unsafe*", results.get(4).getUsername());

        final ArgumentCaptor<ActionParameters> paramsCaptor = ArgumentCaptor.forClass(ActionParameters.class);
        verify(aciService, Mockito.times(4)).executeAction(
            any(AciServerDetails.class), paramsCaptor.capture(), any());
        final List<ActionParameters> allParams = paramsCaptor.getAllValues();

        final ActionParameters listParams = allParams.get(0);
        assertEquals("UserReadUserListDetails", listParams.get("action"));
        assertEquals("u1,u2", listParams.get("match"));
        assertEquals("2", listParams.get("maxusers"));

        final ActionParameters getParams1 = allParams.get(1);
        assertEquals("UserRead", getParams1.get("action"));
        assertEquals(",unsafe", getParams1.get("username"));
        final ActionParameters getParams2 = allParams.get(2);
        assertEquals("UserRead", getParams2.get("action"));
        assertEquals("uns?afe", getParams2.get("username"));
        final ActionParameters getParams3 = allParams.get(3);
        assertEquals("UserRead", getParams3.get("action"));
        assertEquals("unsafe*", getParams3.get("username"));
    }

    @Test
    public void resetPassword() {
        userService.resetPassword(1L, "password");
        verify(aciService).executeAction(any(AciServerDetails.class), any(ActionParameters.class), any());
    }

    @Test
    public void getUserRole() {
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(mockRolesResponse(Collections.singletonList("SomeRole")));
        assertThat(userService.getUserRole(1L), hasSize(1));
    }

    @Test
    public void getRoles() {
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(mockRolesResponse(Collections.singletonList("SomeRole")));
        assertThat(userService.getRoles(), hasSize(1));
    }

    @Test
    public void addRole() {
        userService.addRole("SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(ActionParameters.class), any());
    }

    @Test
    public void addUserToRole() {
        userService.addUserToRole(1L, "SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(ActionParameters.class), any());
    }

    @Test
    public void removeUserFromRole() {
        userService.removeUserFromRole(1L, "SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(ActionParameters.class), any());
    }

    @Test
    public void removeRole() {
        userService.removeRole("SomeRole");
        verify(aciService).executeAction(any(AciServerDetails.class), any(ActionParameters.class), any());
    }

    @Test
    public void authenticateUser() {
        final Security security = new Security();
        security.setAuthenticate(true);
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any())).thenReturn(security);
        assertTrue(userService.authenticateUser("user1", "password", "repository"));
    }

    @Test
    public void getUser() {
        final String username = "user1";
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any()))
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

    @Test
    public void getRelatedToSearch() {
        final QueryResponseData mockResults = new QueryResponseData();
        when(aciService.executeAction(any(), any(), any())).thenReturn(mockResults);
        final QueryResponseData results =
            userService.getRelatedToSearch("p db", "experts", "farming", 3, 40);

        assertEquals(mockResults, results);

        final ArgumentCaptor<ActionParameters> paramsCaptor = ArgumentCaptor.forClass(ActionParameters.class);
        verify(aciService).executeAction(any(AciServerDetails.class), paramsCaptor.capture(), any());
        final ActionParameters params = paramsCaptor.getValue();
        assertEquals("Query", params.get("action"));
        assertEquals("p db", params.get("databasematch"));
        assertEquals("MATCH{experts}:NAMEDAREA", params.get("fieldtext"));
        assertEquals("farming", params.get("text"));
        assertEquals("false", params.get("weighfieldtext"));
        assertEquals("fields", params.get("print"));
        assertEquals("username,name", params.get("printfields"));
        assertEquals("3", params.get("start"));
        assertEquals("40", params.get("maxresults"));
    }

    private void mockUserRoles(final Collection<String> roles) {
        final RolesResponseData rolesResponseData = mockRolesResponse(roles);
        final UserDetails userDetails = mockUserDetailsResponse(Arrays.asList(createUser("user1", 1L), createUser("user2", 2L), createUser("user3", 3L)));
        final Users usersForFirstRole = mockUsersResponse(Collections.singletonList("user1"));
        final Users usersForSecondRole = mockUsersResponse(Arrays.asList("user1", "user2"));
        final Users usersForThirdRole = mockUsersResponse(Collections.emptyList());
        when(aciService.executeAction(any(AciServerDetails.class), any(ActionParameters.class), any()))
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
        user.getRoles().add("SomeRole");

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
