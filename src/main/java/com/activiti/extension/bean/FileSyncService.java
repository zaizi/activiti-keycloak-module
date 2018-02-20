package com.activiti.extension.bean;

import com.activiti.api.idm.AbstractExternalIdmSourceSyncService;
import com.activiti.domain.sync.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


@Component
public class FileSyncService extends AbstractExternalIdmSourceSyncService {



    @Autowired
    private KeyCloakUserGroupDetails keyCloakUserGropuDetails;

    @Autowired
    private CustomBean customBean;

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
        return customBean.getPropertyValue("keycloak.synchronization.full.cronExpression");
    }

    @Override
    protected String getScheduledDifferentialSyncCronExpression() {
        return customBean.getPropertyValue("keycloak.synchronization.differential.cronExpression");
    }

    @Override
    protected void additionalPostConstruct() {

    }
}
