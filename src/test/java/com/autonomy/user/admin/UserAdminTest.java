/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.autonomy.user.admin;

import com.autonomy.aci.client.annotations.IdolAnnotationsProcessorFactory;
import com.autonomy.aci.client.services.AciErrorException;
import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.services.StAXProcessor;
import com.autonomy.aci.client.transport.AciParameter;
import com.autonomy.aci.client.transport.AciServerDetails;
import com.autonomy.aci.client.util.AciParameters;
import com.autonomy.user.admin.dto.RoleList;
import com.autonomy.user.admin.dto.User;
import com.autonomy.user.admin.dto.UserList;
import com.autonomy.user.admin.dto.UserReadUserListDetailsUser;
import com.autonomy.user.admin.dto.UserRoles;
import com.hp.autonomy.frontend.configuration.ConfigService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.autonomy.frontend.testing.matchers.EqualsAciParameters.equalsAciParameters;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserAdminTest {
    @Autowired
    private AciService aciService;

    @Autowired
    private ConfigService<UserAdminConfig> userAdminConfig;

    @Autowired
    private UserAdmin userAdmin;

    @Autowired
    private IdolAnnotationsProcessorFactory processorFactory;

    private List<UserReadUserListDetailsUser> users;
    private final List<String> usernames = Arrays.asList("pippo", "richie blackmore", "ian gillan", "bobby rondinelli");

    @Before
    public void setUp() {
        when(userAdminConfig.getConfig()).thenReturn(mock(UserAdminConfig.class));
        when(userAdminConfig.getConfig().getCommunityDetails()).thenReturn(mock(AciServerDetails.class));
        when(processorFactory.listProcessorForClass(any(Class.class))).thenReturn(mock(StAXProcessor.class));

        final List<UserReadUserListDetailsUser> userList = new ArrayList<>();

        for (final String username : usernames) {
            final UserReadUserListDetailsUser user = new UserReadUserListDetailsUser();
            user.setName(username);
            userList.add(user);
        }

        users = Collections.unmodifiableList(userList);
    }

    @Test
    public void getUserTest() {
        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class))
        ).thenReturn(createUser());

        final User user = userAdmin.getUserDetails("bleah");
        assertEquals("bleah", user.getName());
        assertEquals(1, user.getMaxAgents());
    }

    @Test
    public void getEmptyUserTest() {
        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class)))
                .thenReturn(Collections.emptyList());

        final User user = userAdmin.getUserDetails("bleah");
        assertNull(user);
    }

    @Test
    public void getUserRoleTest() {
        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class)))
                .thenReturn(createUserRole());

        final List<String> roleList = userAdmin.getUserRole(42);

        assertEquals(2, roleList.size());
        assertEquals("sigur ros", roleList.get(0));
        assertEquals("solo", roleList.get(1));
    }

    @Test
    public void getEmptyUserRoleTest() {
        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class)))
                .thenReturn(Collections.emptyList());

        assertNull(userAdmin.getUserRole(42));
    }

    @Test
    public void getUsersRolesTest() {
        final List<RoleList> roles = roleList();

        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class)))
                .thenReturn(
                    roles,
                    users,
                    Collections.singletonList(getUserRoles1()),
                    Collections.singletonList(getUserRoles2()),
                    Collections.singletonList(getUserRoles3()),
                    Collections.singletonList(getUserRoles4())
                );

        final List<UserRoles> userRoles = userAdmin.getUsersRoles();

        assertEquals(3, userRoles.size());

        for (final UserRoles userRole : userRoles) {
            assertThat(usernames, hasItem(userRole.getUsername()));

            if (userRole.getUsername().equals("pippo")) {
                assertEquals(0, userRole.getRoles().size());
            }
        }
    }

    @Test
    public void getUsersRolesWithRoleTest() {
        final List<RoleList> roles = roleList();

        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class)))
                .thenReturn(
                    roles,
                    getUsers3(),
                    Collections.singletonList(getUserRoles3())
                );

        final List<UserRoles> userRoles = userAdmin.getUsersRoles("black sabbath");

        assertEquals(2, userRoles.size());

        for (final UserRoles userRole : userRoles) {
            assertThat(userRole.getRoles(), hasItem("black sabbath"));
            assertThat(userRole.getRoles(), not(hasItem("rainbow")));
        }
    }

    @Test
    public void getUsersRolesWithRolesTest() {
        final List<RoleList> roles = roleList();

        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class)))
                .thenReturn(
                    roles,
                    getUsers1(),
                    Collections.singletonList(getUserRoles2())
                );

        final List<UserRoles> userRoles = userAdmin.getUsersRoles(Arrays.asList("deep purple", "rainbow"));

        assertEquals(1, userRoles.size());

        for (final UserRoles userRole : userRoles) {
            assertThat(userRole.getRoles(), not(hasItems("blue oyster cult")));
            assertThat(userRole.getUsername(), not("pippo"));
        }
    }

    @Test
    public void getUsersRolesWithExceptTest() {
        when(aciService.executeAction(
                any(AciServerDetails.class), anySetOf(AciParameter.class), any(Processor.class))
        ).thenReturn(
            roleList(),
            getUsers3(),
            Collections.singletonList(getUserRoles4())
        );

        final List<UserRoles> userRoles = userAdmin.getUsersRolesExcept(Arrays.asList("deep purple", "rainbow"));

        assertEquals(1, userRoles.size());

        for (final UserRoles userRole : userRoles) {
            assertThat(userRole.getUsername(), not("richie blackmore, pippo"));
            assertThat(userRole.getRoles(), not(hasItems("deep purple", "rainbow")));
        }
    }

    @SuppressWarnings("unchecked") /* For isA(Processor.class) calls */
    @Test
    public void getAllUsersWithRolesTest() {
        final List<String> roles = Arrays.asList("everyone", "developer", "manager", "american");
        final RoleList roleList = new RoleList();
        roleList.getRoles().addAll(roles);

        when(aciService.<List<RoleList>>executeAction(
                isA(AciServerDetails.class),
                argThat(equalsAciParameters(new AciParameters("RoleGetRoleList"))),
                isA(Processor.class))
        ).thenReturn(Collections.singletonList(roleList));

        final AciParameters developersAciParameters = new AciParameters("RoleGetUserList");
        developersAciParameters.add("RoleName", "developer");

        when(aciService.<List<UserList>>executeAction(
                isA(AciServerDetails.class),
                argThat(equalsAciParameters(developersAciParameters)),
                isA(Processor.class)
        )).thenReturn(Collections.singletonList(new UserList(Arrays.asList("brian", "alex", "matthew"))));

        final AciParameters managersAciParameters = new AciParameters("RoleGetUserList");
        managersAciParameters.add("RoleName", "manager");

        when(aciService.<List<UserList>>executeAction(
                isA(AciServerDetails.class),
                argThat(equalsAciParameters(managersAciParameters)),
                isA(Processor.class)
        )).thenReturn(Collections.singletonList(new UserList(Arrays.asList("brian", "sean"))));

        final AciParameters cooksAciParameters = new AciParameters("RoleGetUserList");
        cooksAciParameters.add("RoleName", "cook");

        when(aciService.<UserList>executeAction(
                isA(AciServerDetails.class),
                argThat(equalsAciParameters(cooksAciParameters)),
                isA(Processor.class)
        )).thenThrow(new AciErrorException("Error: Role Not Found"));

        final List<UserReadUserListDetailsUser> users = new ArrayList<>();

        final UserReadUserListDetailsUser brian = new UserReadUserListDetailsUser();
        brian.setName("brian");
        brian.setUid(1);
        users.add(brian);

        final UserReadUserListDetailsUser matthew = new UserReadUserListDetailsUser();
        matthew.setName("matthew");
        matthew.setUid(2);
        users.add(matthew);

        final UserReadUserListDetailsUser alex = new UserReadUserListDetailsUser();
        alex.setName("alex");
        alex.setUid(3);
        users.add(alex);

        final UserReadUserListDetailsUser sean = new UserReadUserListDetailsUser();
        sean.setName("sean");
        sean.setUid(4);
        users.add(sean);

        final UserReadUserListDetailsUser bush = new UserReadUserListDetailsUser();
        bush.setName("bush");
        bush.setUid(5);
        users.add(bush);

        when(aciService.<List<UserReadUserListDetailsUser>>executeAction(
                isA(AciServerDetails.class),
                argThat(equalsAciParameters(new AciParameters("UserReadUserListDetails"))),
                isA(Processor.class)
        )).thenReturn(users);

        final List<UserRoles> output = userAdmin.getAllUsersWithRoles(Arrays.asList("developer", "manager", "cook"));

        assertThat(output, hasSize(5));

        final UserRoles userRoles0 = output.get(0);
        assertThat(userRoles0.getUsername(), equalTo("brian"));
        assertThat(userRoles0.getUid(), equalTo(1L));
        assertThat(userRoles0.getRoles(), containsInAnyOrder("manager", "developer"));

        final UserRoles userRoles1 = output.get(1);
        assertThat(userRoles1.getUsername(), equalTo("matthew"));
        assertThat(userRoles1.getUid(), equalTo(2L));
        assertThat(userRoles1.getRoles(), containsInAnyOrder("developer"));

        final UserRoles userRoles2 = output.get(2);
        assertThat(userRoles2.getUsername(), equalTo("alex"));
        assertThat(userRoles2.getUid(), equalTo(3L));
        assertThat(userRoles2.getRoles(), containsInAnyOrder("developer"));

        final UserRoles userRoles3 = output.get(3);
        assertThat(userRoles3.getUsername(), equalTo("sean"));
        assertThat(userRoles3.getUid(), equalTo(4L));
        assertThat(userRoles3.getRoles(), containsInAnyOrder("manager"));

        final UserRoles userRoles4 = output.get(4);
        assertThat(userRoles4.getUsername(), equalTo("bush"));
        assertThat(userRoles4.getUid(), equalTo(5L));
        assertThat(userRoles4.getRoles(), hasSize(0));
    }

    private List<User> createUser() {
        final List<User> users = new ArrayList<>();
        final User user = new User();

        user.setName("bleah");
        user.setMaxAgents(1);
        users.add(user);

        return users;
    }

    private List<RoleList> createUserRole() {
        final List<RoleList> roles = new ArrayList<>();
        final RoleList role = new RoleList();

        role.getRoles().add("sigur ros");
        role.getRoles().add("solo");

        roles.add(role);
        return roles;
    }

    private UserList getUserRoles1() {
        return new UserList(Arrays.asList("richie blackmore", "ian gillan"));
    }

    private UserList getUserRoles2() {
        return new UserList(Collections.singletonList("richie blackmore"));
    }

    private UserList getUserRoles3() {
        return new UserList(Arrays.asList("bobby rondinelli", "ian gillan"));
    }

    private UserList getUserRoles4() {
        return new UserList(Collections.singletonList("bobby rondinelli"));
    }

    private List<UserReadUserListDetailsUser> getUsers1() {
        return Arrays.asList(users.get(1), users.get(2));
    }

    private List<UserReadUserListDetailsUser> getUsers2() {
        return Collections.singletonList(users.get(1));
    }

    private List<UserReadUserListDetailsUser> getUsers3() {
        return Arrays.asList(users.get(3), users.get(2));
    }

    private List<UserReadUserListDetailsUser> getUsers4() {
        return Collections.singletonList(users.get(3));
    }

    private List<RoleList> roleList() {
        final RoleList roleList = new RoleList();
        roleList.getRoles().addAll(Arrays.asList("deep purple", "rainbow", "black sabbath", "blue oyster cult"));
        final List<RoleList> list = new ArrayList<>();
        list.add(roleList);
        return new ArrayList<>(list);
    }
}