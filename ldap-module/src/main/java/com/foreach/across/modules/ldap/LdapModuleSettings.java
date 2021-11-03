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

package com.foreach.across.modules.ldap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@ConfigurationProperties("ldap-module")
@Data
@SuppressWarnings("unused")
public class LdapModuleSettings
{
	public static final String DISABLE_SYNCHRONIZATION_TASK = "ldapModule.disableSynchronizationTask";
	@Deprecated
	public static final String SYNCHRONIZATION_TASK_INTERVAL_IN_SECONDS =
			"ldapModule.synchronizationTaskIntervalInSeconds";
	public static final String SYNCHRONIZATION_TASK_INTERVAL =
			"ldapModule.synchronizationTaskInterval";
	public static final String SYNCHRONIZATION_TASK_INITIAL_DELAY =
			"ldapModule.synchronizationTaskInitialDelay";
	public static final String DELETE_USERS_AND_GROUPS_WHEN_DELETED_FROM_LDAPSOURCE =
			"ldapModule.deleteUsersAndGroupsWhenDeletedFromLdapSource";
	public static final String BREAK_ON_USER_SYNC_FAILURE =
			"ldapModule.breakOnUserSyncFailure";

	private boolean disableSynchronizationTask;
	private Duration synchronizationTaskInterval = Duration.ofSeconds( 300 );
	private Duration synchronizationTaskInitialDelay = Duration.ofSeconds( 90 );
	private boolean deleteUsersAndGroupsWhenDeletedFromLdapSource = false;

	@Deprecated
	/*
	  Deprecated in favour of {@link #getSynchronizationTaskInterval}
	 */
	public long getSynchronizationTaskIntervalInSeconds() {
		return synchronizationTaskInterval.getSeconds();
	}

	@Deprecated
	/*
	  Deprecated in favour of {@link #setSynchronizationTaskInterval}
	 */
	public void setSynchronizationTaskIntervalInSeconds( long synchronizationTaskIntervalInSeconds ) {
		this.synchronizationTaskInterval = Duration.ofSeconds( synchronizationTaskIntervalInSeconds );
	}
	private boolean breakOnUserSyncFailure = true;
}
