package com.activiti.extension.bean;

import com.activiti.api.idm.AbstractExternalIdmSourceSyncService;
import com.activiti.domain.sync.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FileSyncService extends AbstractExternalIdmSourceSyncService {

	@Autowired
	private KeyCloakUserGroupDetails keyCloakUserGropuDetails;

	@Autowired
	private Environment environment;

	@Autowired
	private KeyCloakEnabled keyCloakEnabled;

	@Override
	protected String getIdmType() {
		return "keycloak";
	}

	@Override
	protected boolean isFullSyncEnabled(Long aLong) {
		return true;
	}

	@Override
	protected boolean isDifferentialSyncEnabled(Long aLong) {
		return false;
	}

	@Override
	protected ExternalIdmQueryResult getAllUsersAndGroupsWithResolvedMembers(Long aLong) {

		if (!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

			return new ExternalIdmQueryResultImpl(Collections.emptyList(), Collections.emptyList());
		}

		List<ExternalIdmUserImpl> lstOfUsers = new ArrayList<>();

		List<ExternalIdmGroupImpl> lstOfGroups = new ArrayList<>();

		String[] allRealms = environment.getProperty("keycloak.sync.realms").split(",");
		for (String realm : allRealms) {

			keyCloakUserGropuDetails.setRealmName(realm);

			List<ExternalIdmUserImpl> lstOfRealmUsers = keyCloakUserGropuDetails.getUsers();

			List<ExternalIdmGroupImpl> lstOfRealmGroups = keyCloakUserGropuDetails.getGroups(lstOfRealmUsers);

			lstOfUsers.addAll(lstOfRealmUsers);
			lstOfGroups.addAll(lstOfRealmGroups);
		}

		return new ExternalIdmQueryResultImpl(lstOfUsers, lstOfGroups);

	}

	@Override
	protected List<? extends ExternalIdmUser> getUsersModifiedSince(Date date, Long aLong) {
		return null;
	}

	@Override
	protected List<? extends ExternalIdmGroup> getGroupsModifiedSince(Date date, Long aLong) {
		return null;
	}

	@Override
	protected String[] getTenantManagerIdentifiers(Long aLong) {
		return new String[0];
	}

	@Override
	protected String[] getTenantAdminIdentifiers(Long aLong) {
		return new String[0];
	}

	@Override
	protected String getScheduledFullSyncCronExpression() {

		if (!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

			return null;
		}

		return environment.getProperty("keycloak.synchronization.full.cronExpression");
	}

	@Override
	protected String getScheduledDifferentialSyncCronExpression() {

		if (!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

			return null;
		}

		return "";
	}

	@Override
	protected void additionalPostConstruct() {

	}
}
