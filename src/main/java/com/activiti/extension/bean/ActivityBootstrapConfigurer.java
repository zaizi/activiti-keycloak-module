package com.activiti.extension.bean;

import com.activiti.api.boot.BootstrapConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivityBootstrapConfigurer implements BootstrapConfigurer {

    @Autowired
    private FileSyncService fileSyncService;


    @Autowired
    private KeyCloakEnabled keyCloakEnabled;

    public void applicationContextInitialized(org.springframework.context.ApplicationContext applicationContext) {

        if(!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {
            fileSyncService.asyncExecuteFullSynchronizationIfNeeded(null);
        }
    }
}
