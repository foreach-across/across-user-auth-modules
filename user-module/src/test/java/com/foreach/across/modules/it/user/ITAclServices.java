package com.foreach.across.modules.it.user;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.EmptyAcrossModule;
import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.core.annotations.Refreshable;
import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.spring.security.business.AclPermission;
import com.foreach.across.modules.spring.security.business.SecurityPrincipal;
import com.foreach.across.modules.user.business.Group;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.dto.GroupDto;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.services.GroupService;
import com.foreach.across.modules.user.services.PermissionService;
import com.foreach.across.modules.user.services.RoleService;
import com.foreach.across.modules.user.services.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = { ITUserModule.Config.class, ITAclServices.SecurityConfig.class })
public class ITAclServices
{
	private final ObjectIdentity repository = new ObjectIdentityImpl( "repository", 1L );
	private final TestFolder folderOne = new TestFolder( 123 );
	private final TestFile fileInFolderOne = new TestFile( 888 );
	private final TestFolder folderTwo = new TestFolder( 456 );
	private final TestFile fileInFolderTwo = new TestFile( 999 );

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserService userService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private SecuredBean securedBean;

	@Autowired
	private AclConfigurer acl;

	private Group group;
	private User userOne, userTwo, userThree, userFour;

	@Before
	public void createUsers() {
		permissionService.definePermission( "manage files", "Manage all files and folders", "unit-test" );
		roleService.defineRole( "ROLE_FILE_MANAGER", "", Arrays.asList( "manage files" ) );

		group = createGroup();

		userOne = createRandomUser( Collections.<Group>emptyList(), Collections.<String>emptyList() );
		userTwo = createRandomUser( Collections.<Group>emptyList(), Collections.singleton( "ROLE_ADMIN" ) );
		userThree = createRandomUser( Collections.<Group>emptyList(), Collections.singleton( "ROLE_FILE_MANAGER" ) );
		userFour = createRandomUser( Collections.singleton( group ), Collections.<String>emptyList() );
	}

	private Group createGroup( String... roles ) {
		Group group = groupService.getGroupById( -999 );

		if ( group == null ) {
			GroupDto dto = new GroupDto();
			dto.setName( RandomStringUtils.randomAscii( 20 ) );
			dto.setId( -999 );
			dto.setNewEntity( true );

			for ( String role : roles ) {
				dto.addRole( roleService.getRole( role ) );
			}

			group = groupService.save( dto );
		}

		return group;
	}

	@After
	public void clearAcls() {
		acl.delete( repository, folderOne, fileInFolderOne, folderTwo, fileInFolderTwo );
	}

	private User createRandomUser( Collection<Group> groups, Collection<String> roles ) {
		UserDto user = new UserDto();
		user.setUsername( UUID.randomUUID().toString() );
		user.setEmail( UUID.randomUUID().toString() + "@test.com" );
		user.setPassword( "test" );
		user.setFirstName( RandomStringUtils.randomAscii( 25 ) );
		user.setLastName( RandomStringUtils.randomAscii( 25 ) );
		user.setDisplayName( RandomStringUtils.randomAscii( 50 ) );

		for ( Group group : groups ) {
			user.addGroup( group );
		}

		for ( String role : roles ) {
			user.addRole( roleService.getRole( role ) );
		}

		return userService.save( user );
	}

	@Test
	public void userWithDirectApprovalOnFolder() {
		logon( userOne );

		acl.create( repository, null );
		acl.create( folderOne, repository );
		acl.create( fileInFolderOne, folderOne );
		acl.create( folderTwo, repository );
		acl.create( fileInFolderTwo, folderTwo );

		assertFalse( canRead( folderOne ) );
		assertFalse( canRead( fileInFolderOne ) );

		acl.allow( userOne, folderOne, AclPermission.READ );

		assertTrue( canRead( folderOne ) );
		assertTrue( canRead( fileInFolderOne ) );
		assertFalse( canWrite( folderOne ) );
		assertFalse( canWrite( fileInFolderOne ) );

		logon( userTwo );
		assertFalse( canRead( folderOne ) );
		assertFalse( canRead( fileInFolderOne ) );
	}

	@Test
	public void permissionsThroughAuthorityAndGroup() {
		logon( userOne );

		acl.create( repository, null );
		acl.create( folderOne, repository );
		acl.create( fileInFolderOne, folderOne );
		acl.create( folderTwo, repository );
		acl.create( fileInFolderTwo, folderTwo );

		acl.allow( "manage files", repository, AclPermission.WRITE );
		acl.allow( "manage files", repository, AclPermission.READ );

		acl.allow( userTwo, folderTwo, AclPermission.READ );
		acl.allow( userOne, fileInFolderOne, AclPermission.WRITE );
		acl.allow( userOne, fileInFolderTwo, AclPermission.WRITE );

		acl.allow( group, repository, AclPermission.WRITE );

		logon( userOne );
		assertFalse( canRead( folderOne ) || canWrite( folderOne ) );
		assertFalse( canRead( folderTwo ) || canWrite( folderTwo ) );
		assertTrue( canWrite( fileInFolderOne ) && !canRead( fileInFolderOne ) );
		assertTrue( canWrite( fileInFolderTwo ) && !canRead( fileInFolderTwo ) );

		logon( userTwo );
		assertFalse( canRead( folderOne ) || canWrite( folderOne ) );
		assertTrue( canRead( folderTwo ) && !canWrite( folderTwo ) );
		assertFalse( canRead( fileInFolderOne ) || canWrite( fileInFolderOne ) );
		assertTrue( canRead( fileInFolderTwo ) && !canWrite( fileInFolderTwo ) );

		logon( userThree );
		assertTrue( canRead( folderOne ) && canWrite( folderOne ) );
		assertTrue( canRead( folderTwo ) && canWrite( folderTwo ) );
		assertTrue( canRead( fileInFolderOne ) && canWrite( fileInFolderOne ) );
		assertTrue( canRead( fileInFolderTwo ) && canWrite( fileInFolderTwo ) );

		logon( userFour );
		assertTrue( !canRead( folderOne ) && canWrite( folderOne ) );
		assertTrue( !canRead( folderTwo ) && canWrite( folderTwo ) );
		assertTrue( !canRead( fileInFolderOne ) && canWrite( fileInFolderOne ) );
		assertTrue( !canRead( fileInFolderTwo ) && canWrite( fileInFolderTwo ) );
	}

	private boolean canRead( Object object ) {
		try {
			return securedBean.canRead( object );
		}
		catch ( AccessDeniedException ade ) {
			return false;
		}
	}

	private boolean canWrite( Object object ) {
		try {
			return securedBean.canWrite( object );
		}
		catch ( AccessDeniedException ade ) {
			return false;
		}
	}

	private void logon( User user ) {
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken( user, "test",
		                                                                                           user.getAuthorities() );
		SecurityContextHolder.getContext().setAuthentication( authRequest );
	}

	@Configuration
	protected static class SecurityConfig implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new SpringSecurityModule() );
			context.addModule( testModule() );
		}

		public AcrossModule testModule() {
			EmptyAcrossModule emptyAcrossModule = new EmptyAcrossModule( "testModule" );
			emptyAcrossModule.addApplicationContextConfigurer( TestModuleConfig.class );

			return emptyAcrossModule;
		}
	}

	@Configuration
	protected static class TestModuleConfig
	{
		@Bean
		@Exposed
		public SecuredBean securedBean() {
			return new SecuredBean();
		}

		@Bean
		@Exposed
		public AclConfigurer aclConfigurer() {
			return new AclConfigurer();
		}
	}

	protected static class TestFile
	{
		private final long id;

		public TestFile( long id ) {
			this.id = id;
		}

		public long getId() {
			return id;
		}
	}

	public static class TestFolder extends TestFile
	{
		public TestFolder( long id ) {
			super( id );
		}
	}

	@Refreshable
	public static class SecuredBean
	{
		@PreAuthorize("hasPermission(#fileOrFolder, 'READ')")
		public boolean canRead( Object fileOrFolder ) {
			return true;
		}

		@PreAuthorize("hasPermission(#fileOrFolder, 'WRITE')")
		public boolean canWrite( Object fileOrFolder ) {
			return true;
		}
	}

	@Refreshable
	public static class AclConfigurer
	{
		@Autowired
		private AcrossContextBeanRegistry contextBeanRegistry;

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		public void create( Object object, Object parent ) {
			MutableAclService aclService = contextBeanRegistry.getBeanOfType( MutableAclService.class );

			ObjectIdentity oi = id( object );

			MutableAcl acl = null;
			try {
				acl = (MutableAcl) aclService.readAclById( oi );
			}
			catch ( NotFoundException nfe ) {
				acl = aclService.createAcl( oi );
			}

			if ( parent != null ) {
				Acl parentAcl = aclService.readAclById( id( parent ) );
				acl.setParent( parentAcl );
			}

			aclService.updateAcl( acl );
		}

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		public void allow( SecurityPrincipal principal, Object fileOrFolder, Permission perm ) {
			MutableAclService aclService = contextBeanRegistry.getBeanOfType( MutableAclService.class );

			Sid sid = new PrincipalSid( principal.getPrincipalId() );
			ObjectIdentity oi = id( fileOrFolder );

			MutableAcl acl = null;
			try {
				acl = (MutableAcl) aclService.readAclById( oi );
			}
			catch ( NotFoundException nfe ) {
				acl = aclService.createAcl( oi );
			}

			acl.insertAce( acl.getEntries().size(), perm, sid, true );
			aclService.updateAcl( acl );
		}

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		public void allow( String authority, Object entity, Permission perm ) {
			MutableAclService aclService = contextBeanRegistry.getBeanOfType( MutableAclService.class );

			Sid sid = new GrantedAuthoritySid( authority );
			ObjectIdentity oi = id( entity );

			MutableAcl acl = null;
			try {
				acl = (MutableAcl) aclService.readAclById( oi );
			}
			catch ( NotFoundException nfe ) {
				acl = aclService.createAcl( oi );
			}

			acl.insertAce( acl.getEntries().size(), perm, sid, true );
			aclService.updateAcl( acl );
		}

		@Transactional(propagation = Propagation.REQUIRES_NEW)
		public void delete( Object... objects ) {
			MutableAclService aclService = contextBeanRegistry.getBeanOfType( MutableAclService.class );

			for ( Object object : objects ) {
				try {
					aclService.deleteAcl( id( object ), true );
				}
				catch ( NotFoundException nfe ) {
				}
			}
		}

		private ObjectIdentity id( Object object ) {
			if ( object instanceof ObjectIdentity ) {
				return (ObjectIdentity) object;
			}

			return new ObjectIdentityImpl( object );
		}
	}
}

