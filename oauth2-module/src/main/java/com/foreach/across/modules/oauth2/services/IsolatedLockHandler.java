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

package com.foreach.across.modules.oauth2.services;

import com.foreach.common.concurrent.locks.ObjectLock;
import com.foreach.common.concurrent.locks.ObjectLockRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class IsolatedLockHandler
{
	/**
	 * Creates and takes the lock outside of running transaction.
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ObjectLock lock( ObjectLockRepository<String> objectLockRepository, String key ) {
		ObjectLock<String> lock = objectLockRepository.getLock( key );
		lock.lock();
		return lock;
	}

	/**
	 * Releases the lock outside of the running transaction.
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void unlock( ObjectLock lock ) {
		if ( lock != null ) {
			lock.unlock();
		}
	}
}
