package com.activiti.extension.bean.service;

import com.activiti.domain.sync.ExternalIdmGroupImpl;
import com.activiti.domain.sync.ExternalIdmUserImpl;
import com.activiti.extension.bean.CustomBean;
import com.activiti.extension.bean.KeyCloakUserGroupDetails;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyCloakUserGroupDetailsTest {


    @InjectMocks
    private KeyCloakUserGroupDetails keyCloakUserGroupDetails;

    @Mock
    private Keycloak keycloak;

    @Mock
    private CustomBean customBean;


    @Mock
    private RealmResource realmsResource;

    @Mock
    private GroupsResource groupsResource;

    @Mock
    private UsersResource usersResource;


    @Mock
    private GroupResource groupResource;


    /**
     * Initiate the mock objects as required by test case
     */
    private void mock() {
        when(keycloak.realm(Mockito.anyString())).thenReturn(realmsResource);
        when(realmsResource.groups()).thenReturn(groupsResource);
        keyCloakUserGroupDetails = new KeyCloakUserGroupDetails(keycloak, customBean);
    }


    /**
     * Stubbed Object for the User Rpresentation from KeyCloak
     * @param lstOfUsers - List of Users
     * @param name - User Name
     */
    private void addUserRepresentation(List<UserRepresentation> lstOfUsers, String name) {

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(name);
        userRepresentation.setUsername(name);
        lstOfUsers.add(userRepresentation);
    }


    /**
     * Stub Creation for the Groups from Key Cloak
     * @param lstOfGroups - List of Group
     * @param name - Group Name
     */

    private void addGroupRepresentation(List<GroupRepresentation> lstOfGroups, String name) {

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(name);
        lstOfGroups.add(groupRepresentation);
    }


    /**
     * Creation of List of Activity Users
     * @param lstOfUsers - List of users
     * @param name -: User Name
     */
    private void addExternalUserIdm(List<ExternalIdmUserImpl> lstOfUsers, String name) {

        ExternalIdmUserImpl userRepresentation = new ExternalIdmUserImpl();
        userRepresentation.setId(name);
        userRepresentation.setFirstName(name);
        lstOfUsers.add(userRepresentation);
    }




    @Before
    public void setup() {
        mock();
    }


    /**
     * Test GetUsers from KeyCloak which gets some users
     *
     * {@link KeyCloakUserGroupDetails#getUsers()}
     */

    @Test
    public void getKeyCloakUsers() {

        List<UserRepresentation> lstOfUsers = new ArrayList<>();
        addUserRepresentation(lstOfUsers, "SuperUser");
        addUserRepresentation(lstOfUsers, "tempUser");

        when(realmsResource.users()).thenReturn(usersResource);

        when(usersResource.list()).thenReturn(lstOfUsers);

        List<ExternalIdmUserImpl> users = keyCloakUserGroupDetails.getUsers();

        assertEquals(2, users.size());

    }


    /**
     * Test GetUsers from KeyCloak which has null users
     *
     * {@link KeyCloakUserGroupDetails#getUsers()}
     */

    @Test
    public void getKeyCloakUsersNoUsersRegisterInKeyCloak() {


        when(realmsResource.users()).thenReturn(usersResource);

        when(usersResource.list()).thenReturn(null);

        List<ExternalIdmUserImpl> users = keyCloakUserGroupDetails.getUsers();

        assertTrue(users.isEmpty());

    }


    /**
     * Test GetUsers from KeyCloak which has empty users
     *
     * {@link KeyCloakUserGroupDetails#getUsers()}
     */

    @Test
    public void getKeyCloakUsersEmptyUsersRegisterInKeyCloak() {


        when(realmsResource.users()).thenReturn(usersResource);

        when(usersResource.list()).thenReturn(Collections.emptyList());

        List<ExternalIdmUserImpl> users = keyCloakUserGroupDetails.getUsers();

        assertTrue(users.isEmpty());

    }



    /**
     * Test get Groups from KeyCloak which has no users
     *
     * {@link KeyCloakUserGroupDetails#getGroups(java.util.List)}
     */

    @Test
    public void getKeyCloakGroupsWithNoMembersAssignedToGroups() {

        List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();
        addExternalUserIdm(lstOfUsers, "SuperUser");
        addExternalUserIdm(lstOfUsers, "tempUser");

        List<GroupRepresentation> lstOfGroups = new ArrayList<>();
        addGroupRepresentation(lstOfGroups, "SuperUser");
        addGroupRepresentation(lstOfGroups, "tempUser");

        when(realmsResource.groups()).thenReturn(groupsResource);

        when(groupsResource.groups()).thenReturn(lstOfGroups);

        when(groupsResource.group(Mockito.anyString())).thenReturn(groupResource);
        when(groupResource.members()).thenReturn(null);

        List<ExternalIdmGroupImpl> results = keyCloakUserGroupDetails.getGroups(lstOfUsers);

        assertEquals(2, results.size());

    }


    /**
     * Test get Groups from KeyCloak which has Empty users
     *
     * {@link KeyCloakUserGroupDetails#getGroups(java.util.List)}
     */
    @Test
    public void getKeyCloakGroupsWithEmptyMembersAssignedToGroups() {

        List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();
        addExternalUserIdm(lstOfUsers, "SuperUser");
        addExternalUserIdm(lstOfUsers, "tempUser");

        List<GroupRepresentation> lstOfGroups = new ArrayList<>();
        addGroupRepresentation(lstOfGroups, "SuperUser");
        addGroupRepresentation(lstOfGroups, "tempUser");

        when(realmsResource.groups()).thenReturn(groupsResource);

        when(groupsResource.groups()).thenReturn(lstOfGroups);

        when(groupsResource.group(Mockito.anyString())).thenReturn(groupResource);
        when(groupResource.members()).thenReturn(Collections.emptyList());

        List<ExternalIdmGroupImpl> results = keyCloakUserGroupDetails.getGroups(lstOfUsers);

        assertEquals(2, results.size());

    }





    /**
     * Test get Groups from KeyCloak which has  users
     *
     * {@link KeyCloakUserGroupDetails#getGroups(java.util.List)}
     */
    @Test
    public void getKeyCloakGroupsWithMembersAssignedToGroups() {

        List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();
        addExternalUserIdm(lstOfUsers, "SuperUser");
        addExternalUserIdm(lstOfUsers, "tempUser");

        List<UserRepresentation> lstOfUsersRepresentation = new ArrayList<>();
        addUserRepresentation(lstOfUsersRepresentation, "SuperUser");
        addUserRepresentation(lstOfUsersRepresentation, "tempUser");


        List<GroupRepresentation> lstOfGroups = new ArrayList<>();
        addGroupRepresentation(lstOfGroups, "SuperUser");
        addGroupRepresentation(lstOfGroups, "tempUser");

        when(realmsResource.groups()).thenReturn(groupsResource);

        when(groupsResource.groups()).thenReturn(lstOfGroups);

        when(groupsResource.group(Mockito.anyString())).thenReturn(groupResource);
        when(groupResource.members()).thenReturn(lstOfUsersRepresentation);

        List<ExternalIdmGroupImpl> results = keyCloakUserGroupDetails.getGroups(lstOfUsers);

        assertEquals(2, results.size());

        assertEquals(2, results.get(0).getUsers().size());
        assertEquals(2, results.get(1).getUsers().size());

    }





    /**
     * Test get Groups from KeyCloak which has  users
     *
     * {@link KeyCloakUserGroupDetails#getGroups(java.util.List)}
     */
    @Test
    public void getKeyCloakGroupsWithMembersAssignedToGroupsEmptyUsersFromKeyCloak() {

        List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();
        addExternalUserIdm(lstOfUsers, "SuperUser");
        addExternalUserIdm(lstOfUsers, "tempUser");

        List<UserRepresentation> lstOfUsersRepresentation = new ArrayList<>();
        addUserRepresentation(lstOfUsersRepresentation, "SuperUser");
        addUserRepresentation(lstOfUsersRepresentation, "tempUser");


        List<GroupRepresentation> lstOfGroups = new ArrayList<>();
        addGroupRepresentation(lstOfGroups, "SuperUser");
        addGroupRepresentation(lstOfGroups, "tempUser");

        when(realmsResource.groups()).thenReturn(groupsResource);

        when(groupsResource.groups()).thenReturn(lstOfGroups);

        when(groupsResource.group(Mockito.anyString())).thenReturn(groupResource);
        when(groupResource.members()).thenReturn(lstOfUsersRepresentation);

        List<ExternalIdmGroupImpl> results = keyCloakUserGroupDetails.getGroups(Collections.emptyList());

        assertEquals(2, results.size());

        assertEquals(0, results.get(0).getUsers().size());
        assertEquals(0, results.get(1).getUsers().size());

    }


    /**
     * Test get Groups from KeyCloak which has no groups
     *
     * {@link KeyCloakUserGroupDetails#getGroups(java.util.List)}
     */
    @Test
    public void getKeyCloakNoGroups() {

        List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();
        addExternalUserIdm(lstOfUsers, "SuperUser");
        addExternalUserIdm(lstOfUsers, "tempUser");


        when(realmsResource.groups()).thenReturn(groupsResource);

        when(groupsResource.groups()).thenReturn(null);

        List<ExternalIdmGroupImpl> results = keyCloakUserGroupDetails.getGroups(lstOfUsers);

        assertTrue(results.isEmpty());



    }


    /**
     * Test get Groups from KeyCloak which has no groups
     *
     * {@link KeyCloakUserGroupDetails#getGroups(java.util.List)}
     */
    @Test
    public void getKeyCloakEmptyGroups() {

        List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();
        addExternalUserIdm(lstOfUsers, "SuperUser");
        addExternalUserIdm(lstOfUsers, "tempUser");


        when(realmsResource.groups()).thenReturn(groupsResource);

        when(groupsResource.groups()).thenReturn(Collections.emptyList());

        List<ExternalIdmGroupImpl> results = keyCloakUserGroupDetails.getGroups(lstOfUsers);

        assertTrue(results.isEmpty());



    }



}