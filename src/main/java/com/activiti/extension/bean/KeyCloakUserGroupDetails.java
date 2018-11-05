package com.activiti.extension.bean;

import com.activiti.domain.sync.ExternalIdmGroupImpl;
import com.activiti.domain.sync.ExternalIdmUserImpl;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@ComponentScan("com.activiti.extension.config")
public class KeyCloakUserGroupDetails {
	
	@Autowired
	private Keycloak keyCloakClient;

	private String realmName;

	public KeyCloakUserGroupDetails() {

	}
	
	public synchronized List<ExternalIdmGroupImpl> getGroups(List<ExternalIdmUserImpl> users) {

		RealmResource realmsResource = keyCloakClient.realm(this.realmName);
		GroupsResource groupsResource = realmsResource.groups();
		
		List<ExternalIdmGroupImpl> lstOfGroups = Collections.emptyList();
			
		List<GroupRepresentation> lstOfGroupRepresentation = groupsResource.groups();
		
		if (lstOfGroupRepresentation != null && !lstOfGroupRepresentation.isEmpty()) {
			lstOfGroups = lstOfGroupRepresentation.stream().map(gr -> getSubgroups(gr, users, groupsResource)) //initial call of recursion
					.collect(Collectors.toList());
		}

		return lstOfGroups;
	}
		
	public synchronized ExternalIdmGroupImpl getSubgroups(GroupRepresentation groupRep, List<ExternalIdmUserImpl> users, GroupsResource groupsResource) {
		
		ExternalIdmGroupImpl externalIdmGroupImpl = new ExternalIdmGroupImpl();
	    externalIdmGroupImpl.setName(groupRep.getName());
	    externalIdmGroupImpl.setOriginalSrcId(groupRep.getName());
	    List<ExternalIdmGroupImpl> subExternalIdmGroupImpl = Collections.emptyList();
	    
	    List<GroupRepresentation> subGroupRepresentation = new ArrayList<>();
	    
	    List<UserRepresentation> members = groupsResource.group(groupRep.getId()).members(); 		
		
	    //set users for each group
		List<ExternalIdmUserImpl> newUsers = new ArrayList<>();
		if (members != null && !members.isEmpty()) {
			
			Map<String, ExternalIdmUserImpl> usersMap = users.stream()
					.collect(Collectors.toMap(ExternalIdmUserImpl::getId, Function.identity()));
			
			newUsers = members.stream().filter(member -> usersMap.containsKey(member.getUsername()))
	    			.map(member -> usersMap.get(member.getUsername())).collect(Collectors.toList());
		}
		
		externalIdmGroupImpl.setUsers(newUsers);
	    		
	    if(groupRep.getSubGroups() != null){
	    	groupRep.getSubGroups().stream().distinct().forEach(gr -> {
	    		subGroupRepresentation.add(gr);
	    		
			});
		}
	   
	    subExternalIdmGroupImpl =	subGroupRepresentation.stream()
	        .map(r -> getSubgroups(r, users, groupsResource)) 
	        .collect(Collectors.toList());
	    externalIdmGroupImpl.setChildGroups(subExternalIdmGroupImpl);
	    
	    return externalIdmGroupImpl;
	}
		
	public synchronized List<ExternalIdmUserImpl> getUsers() {

		RealmResource realmsResource = keyCloakClient.realm(this.realmName);
		UsersResource ur = realmsResource.users();

		List<org.keycloak.representations.idm.UserRepresentation> userRepresentations = ur.list();

		List<ExternalIdmUserImpl> users = Collections.emptyList();

		if (userRepresentations != null && !userRepresentations.isEmpty()) {
			users = userRepresentations.stream().filter(user -> user.isEnabled()).map(user -> {
				ExternalIdmUserImpl externalIdmUser = new ExternalIdmUserImpl();
				externalIdmUser.setId(user.getUsername());
				externalIdmUser.setOriginalSrcId(user.getUsername());
				externalIdmUser.setFirstName(user.getFirstName());
				externalIdmUser.setLastName(user.getLastName());
				externalIdmUser.setEmail(user.getEmail());
				List<CredentialRepresentation> lstOfCredential = user.getCredentials();
				externalIdmUser.setLastModifiedTimeStamp(new Date());
				if (lstOfCredential != null && !lstOfCredential.isEmpty()) {
					lstOfCredential.forEach(credentialRepresentation -> {
						if (CredentialRepresentation.PASSWORD.equals(credentialRepresentation.getType())) {
							externalIdmUser.setPassword(credentialRepresentation.getValue());
						}
					});
				}
				return externalIdmUser;
			}).collect(Collectors.toList());
		}

		return users;

	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
}
