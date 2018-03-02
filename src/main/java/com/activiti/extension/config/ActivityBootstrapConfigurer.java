package com.activiti.extension.config;

import com.activiti.api.boot.BootstrapConfigurer;
import com.activiti.extension.bean.FileSyncService;
import com.activiti.extension.bean.KeyCloakEnabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActivityBootstrapConfigurer implements BootstrapConfigurer {

	@Autowired
	private FileSyncService fileSyncService;

	@Autowired
	private KeyCloakEnabled keyCloakEnabled;

	public void applicationContextInitialized(org.springframework.context.ApplicationContext applicationContext) {

		if (!this.keyCloakEnabled.isKeyCloakSynchronizeEnabled()) {
			fileSyncService.asyncExecuteFullSynchronizationIfNeeded(null);
		}
	}
}
