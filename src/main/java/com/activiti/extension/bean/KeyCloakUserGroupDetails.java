package com.activiti.extension.bean;


import com.activiti.domain.sync.ExternalIdmGroupImpl;
import com.activiti.domain.sync.ExternalIdmUserImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KeyCloakUserGroupDetails {

    private Keycloak keyCloakClient;

    private String realmName;


    public KeyCloakUserGroupDetails(final Keycloak client, String realmName) {

        this.keyCloakClient = client;
        this.realmName = realmName;
    }

    public List<ExternalIdmGroupImpl> getGroups(List<ExternalIdmUserImpl> users) {




        RealmResource realmsResource = keyCloakClient.realm(this.realmName);
        GroupsResource groupsResource = realmsResource.groups();

        List<ExternalIdmGroupImpl> lstOfGroups = Collections.emptyList();
        List<GroupRepresentation> lstOfGroupRepresentation =  groupsResource.groups();
        if(lstOfGroupRepresentation != null && !lstOfGroupRepresentation.isEmpty()) {
            lstOfGroups = lstOfGroupRepresentation.stream().map(gr -> {

                ExternalIdmGroupImpl externalIdmGroup = new ExternalIdmGroupImpl();
                externalIdmGroup.setOriginalSrcId(gr.getName());
                externalIdmGroup.setName(gr.getName());
                GroupResource groupResource = groupsResource.group(gr.getId());
                List<UserRepresentation> members =  groupResource.members();
                if(members != null && !members.isEmpty()) {

                    Map<String, ExternalIdmUserImpl> usersMap = users.stream().collect(Collectors.toMap(ExternalIdmUserImpl::getId,
                            Function.identity()));


                    externalIdmGroup.setUsers(members.stream()
                                                    .filter(member -> usersMap.containsKey(member.getUsername()))
                                                    .map(member -> usersMap.get(member.getUsername()))
                                                    .collect(Collectors.toList()));


                }

                return externalIdmGroup;
            }).collect(Collectors.toList());

        }

        return lstOfGroups;
    }


    public List<ExternalIdmUserImpl> getUsers() {

        RealmResource realmsResource = keyCloakClient.realm(this.realmName);
        UsersResource ur = realmsResource.users();


        List<org.keycloak.representations.idm.UserRepresentation> userRepresentations = ur.list();

        List<ExternalIdmUserImpl> users = Collections.emptyList();

        if(userRepresentations != null && !userRepresentations.isEmpty()) {
            users = userRepresentations.stream().filter(user -> user.isEnabled()).map(user -> {
                ExternalIdmUserImpl externalIdmUser = new ExternalIdmUserImpl();
                externalIdmUser.setId(user.getUsername());
                externalIdmUser.setOriginalSrcId(user.getUsername());
                externalIdmUser.setFirstName(user.getFirstName());
                externalIdmUser.setLastName(user.getLastName());
                externalIdmUser.setEmail(user.getEmail());
                List<CredentialRepresentation> lstOfCredential = user.getCredentials();
                externalIdmUser.setLastModifiedTimeStamp(new Date());
                if(lstOfCredential != null && !lstOfCredential.isEmpty()) {
                    lstOfCredential.forEach(credentialRepresentation -> {
                        if(CredentialRepresentation.PASSWORD.equals(credentialRepresentation.getType())) {
                            externalIdmUser.setPassword(credentialRepresentation.getValue());
                        }
                    });
                }
                return externalIdmUser;
            }).collect(Collectors.toList());
        }

        return users;

    }



}
