package com.foreach.across.modules.user.repositories;

import com.foreach.across.modules.user.TestDatabaseConfig;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserStatus;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.PermissionServiceImpl;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.RoleServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestUserRepository.Config.class)
@DirtiesContext
public class TestUserRepository
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PermissionService permissionService;

	@Before
	public void createRolesAndPermissions() {
		permissionService.definePermission( "perm one", "", "test-perms" );
		permissionService.definePermission( "perm two", "", "test-perms" );
		permissionService.definePermission( "perm three", "", "test-perms" );

		roleService.defineRole( "role one", "", Arrays.asList( "perm one", "perm two" ) );
		roleService.defineRole( "role two", "", Arrays.asList( "perm two", "perm three" ) );
	}

	@Test
	public void userNotFound() {
		User user = userRepository.getUserById( -123 );

		assertNull( user );
	}

	@Test
	public void userWithoutPermissions() {
		User user = new User();
		user.setUsername( "fred" );
		user.setEmail( "fred@gmail.com" );
		user.setPassword( "temp1234" );
        user.setFirstName( "Freddy" );
        user.setLastName( "Alvis" );
        user.setDisplayName( "Fréddy & Màc " );
        user.setEmailConfirmed( false );
        user.setDeleted( false );

		userRepository.create( user );

		assertTrue( user.getId() > 0 );

		User existing = userRepository.getUserById( user.getId() );

		assertEquals( user.getId(), existing.getId() );
		assertEquals( user.getUsername(), existing.getUsername() );
        assertEquals( user.getFirstName(), existing.getFirstName() );
        assertEquals( user.getLastName(), existing.getLastName() );
        assertEquals( user.getDisplayName(), existing.getDisplayName() );
		assertEquals( user.getEmail(), existing.getEmail() );
		assertEquals( user.getPassword(), existing.getPassword() );
        assertEquals( user.getDeleted(), existing.getDeleted() );
        assertEquals( user.getEmailConfirmed(), existing.getEmailConfirmed()  );
        assertEquals( user.getStatus(), existing.getStatus()  );

        assertEquals( false, user.isCredentialsNonExpired() );
        assertEquals( false, user.isAccountNonLocked() );
        assertEquals( false, user.isAccountNonExpired() );
        assertEquals( false, user.isEnabled() );
	}

    @Test
    public void userDelete() {
        User user = new User();
        user.setUsername( "deleteme" );
        user.getRoles().add( roleService.getRole( "role one" ) );
        user.setEmailConfirmed( false );
        user.setDeleted( false );
        user.setStatus( UserStatus.DEFAULT_USER_STATUS );

        userRepository.create( user );
        assertTrue( user.getId() > 0 );

        userRepository.delete( user );
        User deleted = userRepository.getUserById( user.getId() );
        assertNotNull( "user should still exist in database (soft deleted)", deleted );
        assertEquals( true, deleted.getDeleted() );
    }

	@Test
	public void userWithRoles() {
		User user = new User();
		user.setUsername( "paul" );
		user.getRoles().add( roleService.getRole( "role one" ) );
		user.getRoles().add( roleService.getRole( "role two" ) );

		userRepository.create( user );

		User existing = userRepository.getUserById( user.getId() );

		assertEquals( user.getRoles(), existing.getRoles() );
	}

	@Configuration
	@Import(TestDatabaseConfig.class)
	static class Config
	{
		@Bean
		public RoleService roleService() {
			return new RoleServiceImpl();
		}

		@Bean
		public PermissionService permissionService() {
			return new PermissionServiceImpl();
		}

		@Bean
		public RoleRepository roleRepository() {
			return new RoleRepositoryImpl();
		}

		@Bean
		public PermissionRepository permissionRepository() {
			return new PermissionRepositoryImpl();
		}

		@Bean
		public UserRepository userRepository() {
			return new UserRepositoryImpl();
		}
	}
}
