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

package com.foreach.across.modules.test.ldap;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.ldap.repositories.LdapUserDirectoryRepository;
import com.foreach.across.modules.ldap.services.LdapSynchronizationService;
import com.foreach.across.modules.ldap.tasks.LdapSynchronizationTask;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.common.concurrent.locks.distributed.DistributedLockManager;
import com.foreach.common.concurrent.locks.distributed.DistributedLockRepository;
import com.foreach.common.test.MockedLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(loader = MockedLoader.class, classes = TestLdapConnectorSynchronizationTaskLocking.Config.class)
@Slf4j
public class TestLdapConnectorSynchronizationTaskLocking
{
	@Autowired
	private AcrossContextBeanRegistry acrossContextBeanRegistry;

	private LdapUserDirectoryRepository ldapUserDirectoryRepository;
	private DistributedLockManager distributedLockManager;
	private DistributedLockRepository distributedLockRepository;

	@Before
	public void setup() {
		distributedLockManager = acrossContextBeanRegistry.getBeanOfType( DistributedLockManager.class );
		distributedLockRepository = acrossContextBeanRegistry.getBeanOfType( DistributedLockRepository.class );
		ldapUserDirectoryRepository = mock( LdapUserDirectoryRepository.class );
	}

	@Test
	public void checkThatLockGetsReleasedWhenExceptionOccurs() throws Exception {
		LdapSynchronizationTask ldapSynchronizationTask = ldapSynchronizationTask();
		when( ldapUserDirectoryRepository.findAllByActiveTrue() ).thenThrow( new RuntimeException( "Some exception" ) );
		try {
			ldapSynchronizationTask.run();
		}
		catch ( Exception ignore ) {

		}
		Assert.assertFalse( distributedLockManager.isLocked( LdapSynchronizationTask.LOCK_NAME ) );
		verify( ldapUserDirectoryRepository ).findAllByActiveTrue();
	}

	@Test
	public void oneServerCanGetALockAndExecuteSynchronizationServer() throws Exception {
		LdapSynchronizationTask ldapSynchronizationTask = ldapSynchronizationTask();
		ldapSynchronizationTask.run();
		Assert.assertFalse( distributedLockManager.isLocked( LdapSynchronizationTask.LOCK_NAME ) );
		verify( ldapUserDirectoryRepository ).findAllByActiveTrue();
	}

	@Test
	public void testThatOnlyOneServerCanHaveALock() throws Exception {
		int numberOfServers = 100;
		ExecutorService executorService = Executors.newFixedThreadPool( numberOfServers );
		ArrayList<Callable<Object>> serverCalls = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch( numberOfServers );

		AtomicInteger numberOfLocks = new AtomicInteger( 0 );

		final LdapUserDirectoryRepository repository = mock( LdapUserDirectoryRepository.class );
		doAnswer( invocationOnMock -> {
			LOG.info( "Thread {} has the lock", Thread.currentThread() );
			numberOfLocks.incrementAndGet();
			// sleep long enough to make sure that others will all have been processed
			Thread.sleep( numberOfServers * 20 );
			return Collections.emptyList();
		} ).when( repository ).findAllByActiveTrue();

		IntStream.range( 0, numberOfServers ).forEach( ( i ) -> serverCalls.add( () -> {
			try {
				LdapSynchronizationTask ldapSynchronizationTask = ldapSynchronizationTask( repository );
				ldapSynchronizationTask.run();
				latch.countDown();
			}
			catch ( Throwable t ) {
				// Log the error, don't countDown the latch, it will fail the test
				LOG.error( "Assertion Error", t );
			}

			return null;
		} ) );
		serverCalls.forEach( executorService::submit );
		boolean hasErrors = !latch.await( numberOfServers * 50, TimeUnit.SECONDS );
		executorService.shutdownNow();
		if ( hasErrors ) {
			Assert.fail( "Some threads didn't acquire/release the lock properly" );
		}

		Assert.assertEquals( "Only a single thread should have acquired the lock and been able to fetch the user directories", numberOfLocks.get(), 1 );
		verify( repository, times( 1 ) ).findAllByActiveTrue();
	}

	public LdapSynchronizationTask ldapSynchronizationTask() {
		LdapSynchronizationService ldapSynchronizationService = mock( LdapSynchronizationService.class );
		return new LdapSynchronizationTask( ldapSynchronizationService, ldapUserDirectoryRepository,
		                                    distributedLockRepository, UUID.randomUUID().toString() );
	}

	public LdapSynchronizationTask ldapSynchronizationTask( LdapUserDirectoryRepository repository ) {
		LdapSynchronizationService ldapSynchronizationService = mock( LdapSynchronizationService.class );
		return new LdapSynchronizationTask( ldapSynchronizationService, repository,
		                                    distributedLockRepository, UUID.randomUUID().toString() );
	}

	@AcrossTestConfiguration(modules = { UserModule.NAME })
	@Configuration
	public static class Config
	{
	}
}
