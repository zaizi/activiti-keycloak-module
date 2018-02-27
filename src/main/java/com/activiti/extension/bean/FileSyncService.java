package com.activiti.extension.bean;

import com.activiti.api.idm.AbstractExternalIdmSourceSyncService;
import com.activiti.domain.sync.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;


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
        return true;
    }

    @Override
    protected ExternalIdmQueryResult getAllUsersAndGroupsWithResolvedMembers(Long aLong) {

        if(!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

            return new ExternalIdmQueryResultImpl(Collections.emptyList(), Collections.emptyList());
        }


        List<ExternalIdmUserImpl>  lstOfUsers = keyCloakUserGropuDetails.getUsers();


        return new ExternalIdmQueryResultImpl(lstOfUsers, keyCloakUserGropuDetails.getGroups(lstOfUsers));


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

        if(!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

            return null;
        }

        return environment.getProperty("keycloak.synchronization.full.cronExpression");
    }

    @Override
    protected String getScheduledDifferentialSyncCronExpression() {

        if(!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {

            return null;
        }

        return environment.getProperty("keycloak.synchronization.differential.cronExpression");
    }

    @Override
    protected void additionalPostConstruct() {

    }
}
