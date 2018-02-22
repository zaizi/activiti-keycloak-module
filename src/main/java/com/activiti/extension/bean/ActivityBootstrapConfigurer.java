package com.activiti.extension.bean;

import com.activiti.api.boot.BootstrapConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Component
public class ActivityBootstrapConfigurer implements BootstrapConfigurer {

    @Autowired
    private FileSyncService fileSyncService;

    public void applicationContextInitialized(org.springframework.context.ApplicationContext applicationContext) {

        fileSyncService.asyncExecuteFullSynchronizationIfNeeded(null);
    }
}
