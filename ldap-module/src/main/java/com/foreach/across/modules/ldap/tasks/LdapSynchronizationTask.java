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

package com.foreach.across.modules.ldap.tasks;

import com.foreach.across.modules.ldap.repositories.LdapUserDirectoryRepository;
import com.foreach.across.modules.ldap.services.LdapSynchronizationService;
import com.foreach.common.concurrent.locks.distributed.DistributedLock;
import com.foreach.common.concurrent.locks.distributed.DistributedLockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@Slf4j
public class LdapSynchronizationTask implements Runnable
{
	public static final String LOCK_NAME = "LdapSynchronizationTask";

	private final LdapSynchronizationService ldapSynchronizationService;
	private final LdapUserDirectoryRepository ldapUserDirectoryRepository;
	private final DistributedLock lock;

	@Autowired
	public LdapSynchronizationTask( LdapSynchronizationService ldapSynchronizationService,
	                                LdapUserDirectoryRepository ldapUserDirectoryRepository,
	                                DistributedLockRepository distributedLockRepository,
	                                String serverName ) {
		this.ldapSynchronizationService = ldapSynchronizationService;
		this.ldapUserDirectoryRepository = ldapUserDirectoryRepository;
		lock = distributedLockRepository.getLock( serverName, "LdapSynchronizationTask" );
	}

	@Override
	public void run() {
		try {
			LOG.info( "Trying to get lock for {} and owner {}", lock.getOwnerId(), lock.getKey() );
			if ( lock.tryLock() ) {
				LOG.info( "Got lock for {} and owner {}", lock.getOwnerId(), lock.getKey() );
				ldapUserDirectoryRepository.findAllByActiveTrue().forEach( userDirectory -> {
					try {
						UserDirectorySyncHolder.setUserDirectory( userDirectory );
						ldapSynchronizationService.synchronizeData( userDirectory );
					}
					finally {
						UserDirectorySyncHolder.clearUserDirectory();
					}
				} );
			}
			else {
				LOG.info( "Could not get lock for {} and owner {}", lock.getOwnerId(), lock.getKey() );
			}
		}
		catch ( Throwable t ) {
			LOG.error( "Error in LdapSynchronizationTask", t );
		}
		finally {
			LOG.info( "Finished task" );
			if ( lock.isHeldByCurrentThread() ) {
				lock.unlock();
			}
		}
	}
}
