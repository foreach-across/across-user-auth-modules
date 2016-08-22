/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.ldap.config;

import com.foreach.across.modules.ldap.LdapModuleSettings;
import com.foreach.across.modules.ldap.tasks.LdapSynchronizationTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
@ConditionalOnBean(LdapSynchronizationTask.class)
public class LdapSchedulerTaskConfiguration implements SchedulingConfigurer
{
	@Autowired
	private LdapSynchronizationTask ldapSynchronizationTask;
	@Autowired
	private LdapModuleSettings ldapModuleSettings;

	@Override
	public void configureTasks( ScheduledTaskRegistrar taskRegistrar ) {
		taskRegistrar.setScheduler( taskScheduler() );
		taskRegistrar.addFixedRateTask( ldapSynchronizationTask,
		                                ldapModuleSettings.getSynchronizationTaskIntervalInSeconds() * 1000 );
	}

	@Bean(destroyMethod = "shutdown")
	public TaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}
}