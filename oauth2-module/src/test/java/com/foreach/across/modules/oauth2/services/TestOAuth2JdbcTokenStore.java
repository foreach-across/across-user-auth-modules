package com.foreach.across.modules.oauth2.services;

import com.foreach.across.modules.oauth2.business.OAuth2Client;
import com.foreach.across.modules.user.business.Permission;
import com.foreach.across.modules.user.business.Role;
import com.foreach.across.modules.user.business.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.SerializationUtils;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestOAuth2JdbcTokenStore.Config.class)
@DirtiesContext
public class TestOAuth2JdbcTokenStore
{
	@Autowired
	private DataSource dataSource;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private OAuth2StatelessJdbcTokenStore oAuth2StatelessJdbcTokenStore;
	@Autowired
	private List<OAuth2AuthenticationSerializer> serializers;
	@Autowired
	private DummyOAuth2AuthenticationSerializer dummyOAuth2AuthenticationSerializer;

	@Before
	public void resetMocks() {
		for( OAuth2AuthenticationSerializer serializer : serializers ) {
			reset( serializer );
		}
		reset( dataSource, userDetailsService, clientDetailsService, oAuth2StatelessJdbcTokenStore );
	}

	@Test
	public void testClientSerialization() {
		OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                           "testClientId", Collections.<GrantedAuthority>emptyList(), true,
		                                           Collections.singleton( "fullScope" ),
		                                           Collections.singleton( "resourceId" ), "",
		                                           Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap()
		);

		OAuth2Client clientDetails = new OAuth2Client();

		Set<Role> roles = new HashSet<>();
		Role thirdRole = new Role( "role three" );
		thirdRole.addPermission( new Permission( "authority two" ) );

		roles.add( new Role( "role one" ) );
		roles.add( new Role( "role two" ) );
		roles.add( thirdRole );
		clientDetails.setRoles( roles );

		clientDetails.setId( 516 );
		Set<String> authorityTypes = new HashSet<>();
		authorityTypes.add( "authority one" );
		authorityTypes.add( "authority two" );
		clientDetails.setAuthorizedGrantTypes( authorityTypes );

		when( clientDetailsService.loadClientByClientId( "testClientId" ) ).thenReturn( clientDetails );

		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication( request, null );
		byte[] bytes = oAuth2StatelessJdbcTokenStore.serializeAuthentication( oAuth2Authentication );

		assertNotNull( bytes );
		assertTrue( "should find some bytes in this object", bytes.length > 10 );

		Object o = SerializationUtils.deserialize( bytes );
		assertNotNull( o );
		assertTrue( "Serializer should be of type ClientOAuth2AuthenticationSerializer", o instanceof AuthenticationSerializerObject );

		OAuth2Authentication storedAuthentication = oAuth2StatelessJdbcTokenStore.deserializeAuthentication( bytes );

		assertNotNull( storedAuthentication );
		assertEquals( 4, storedAuthentication.getAuthorities().size() );

		verify( oAuth2StatelessJdbcTokenStore ).serializeAuthentication( eq( oAuth2Authentication ) );
		verify( userDetailsService, never() ).loadUserByUsername( anyString() );
		verify( clientDetailsService ).loadClientByClientId( "testClientId" );

	}

	@Test
	public void testUserSerialization() {
		OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                           "testClientId", Collections.<GrantedAuthority>emptyList(), true,
		                                           Collections.singleton( "fullScope" ),
		                                           Collections.singleton( "resourceId" ), "",
		                                           Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap()
		);

		User user = new User();
		user.setUsername( "testusername" );

		Set<Role> roles = new HashSet<>();
		Role userRole = new Role( "role three" );

		Set<Permission> permissions = new HashSet<>(  );
		permissions.add( new Permission( "permission 1" ) );
		userRole.setPermissions( permissions );

		roles.add(userRole);
		user.setRoles( roles );

		user.setId( 777 );

		when( userDetailsService.loadUserByUsername( "testusername" ) ).thenReturn( user );

		Authentication userAuthentication = mock(Authentication.class );
		when( userAuthentication.getPrincipal() ).thenReturn( user );
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication( request, userAuthentication );
		byte[] bytes = oAuth2StatelessJdbcTokenStore.serializeAuthentication( oAuth2Authentication );

		assertNotNull( bytes );
		assertTrue( "should find some bytes in this object", bytes.length > 10 );

		Object o = SerializationUtils.deserialize( bytes );
		assertNotNull( o );
		assertTrue( "Serializer should be of type UserOAuth2AuthenticationSerializer", o instanceof AuthenticationSerializerObject );

		OAuth2Authentication storedAuthentication = oAuth2StatelessJdbcTokenStore.deserializeAuthentication( bytes );

		assertNotNull( storedAuthentication );
		assertEquals( 2, storedAuthentication.getAuthorities().size() );

		verify( oAuth2StatelessJdbcTokenStore ).serializeAuthentication( eq( oAuth2Authentication ) );
		verify( userDetailsService ).loadUserByUsername( "testusername" );
		verify( clientDetailsService, never() ).loadClientByClientId( anyString() );
	}

	@Test
	public void testSerializationWithNullPricipallFallsBackToNormalSerialization() {
		OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                           null, Collections.<GrantedAuthority>emptyList(), true,
		                                           Collections.singleton( "fullScope" ),
		                                           Collections.singleton( "resourceId" ), "",
		                                           Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap() );

		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication( request, null );
		byte[] bytes = oAuth2StatelessJdbcTokenStore.serializeAuthentication( oAuth2Authentication );
		assertNotNull( bytes );
		assertTrue( "should find some bytes in this object", bytes.length > 10 );

		OAuth2Authentication storedAuthentication = oAuth2StatelessJdbcTokenStore.deserializeAuthentication( bytes );
		assertNotNull( storedAuthentication );
		assertEquals( null, storedAuthentication.getPrincipal() );
		assertEquals( "fullScope", storedAuthentication.getOAuth2Request().getScope().iterator().next() );
	}

	@Test
	public void testSerializationWithAnyOtherPricipallFallsBackToNormalSerialization() {
		OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                           null, Collections.<GrantedAuthority>emptyList(), true,
		                                           Collections.singleton( "fullScope" ),
		                                           Collections.singleton( "resourceId" ), "",
		                                           Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap() );

		String[] principal = new String[] { "weird", "principal", "object"};
		Authentication userAuthentication = new PreAuthenticatedAuthenticationToken( principal, null );
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication( request, userAuthentication );
		byte[] bytes = oAuth2StatelessJdbcTokenStore.serializeAuthentication( oAuth2Authentication );
		assertNotNull( bytes );
		assertTrue( "should find some bytes in this object", bytes.length > 10 );

		OAuth2Authentication storedAuthentication = oAuth2StatelessJdbcTokenStore.deserializeAuthentication( bytes );
		assertNotNull( storedAuthentication );
		String[] storedPrincipal = (String[]) storedAuthentication.getPrincipal();
		assertEquals( 3, storedPrincipal.length );
		assertArrayEquals( principal, storedPrincipal );
		assertEquals( "fullScope", storedAuthentication.getOAuth2Request().getScope().iterator().next() );

		assertEquals( 3, serializers.size() );
		for( OAuth2AuthenticationSerializer serializer : serializers ) {
			verify( serializer, never() ).serialize( any( OAuth2Authentication.class ) );
		}
	}

	@Test( expected = OAuth2AuthenticationSerializer.SerializationException.class )
	public void testInvalidSerializedObject() {
		byte[] unknownObject = SerializationUtils.serialize( new ArrayList<>() );
		OAuth2Authentication invalidByteArray = oAuth2StatelessJdbcTokenStore.deserializeAuthentication( unknownObject );
		assertNull( invalidByteArray );
	}

	@Test( expected = OAuth2AuthenticationSerializer.SerializationException.class )
	public void testCustomSerializerThrowsErrorWhenDeserializingUnknownObject() {
		AuthenticationSerializerObject<ArrayList<Object>> authenticationSerializerObject = new AuthenticationSerializerObject<>( DummyOAuth2AuthenticationSerializer.class.getCanonicalName(), new ArrayList<>( ) );
		byte[] serializedDummyObject = SerializationUtils.serialize( authenticationSerializerObject );
		OAuth2Authentication invalidByteArray = oAuth2StatelessJdbcTokenStore.deserializeAuthentication( serializedDummyObject );
		assertNull( invalidByteArray );
	}

	@Test( expected = OAuth2AuthenticationSerializer.SerializationException.class )
	public void testCustomSerializerThrowsErrorWhenSerializingUnknownObject() {
		OAuth2Request request = new OAuth2Request( Collections.<String, String>emptyMap(),
		                                           null, Collections.<GrantedAuthority>emptyList(), true,
		                                           Collections.singleton( "fullScope" ),
		                                           Collections.singleton( "resourceId" ), "",
		                                           Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap() );

		Authentication userAuthentication = mock( Authentication.class );
		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication( request, userAuthentication );
		byte[] bytes = dummyOAuth2AuthenticationSerializer.serialize( oAuth2Authentication );

		assertNull( bytes );
	}


	@Configuration
	static class Config
	{
		@Bean
		public DataSource dataSource() {
			return mock( DataSource.class );
		}

		@Bean
		public UserDetailsService userDetailsService() {
			return mock( UserDetailsService.class );
		}

		@Bean
		public ClientDetailsService clientDetailsService() {
			return mock( ClientDetailsService.class );
		}

		@Bean
		public OAuth2StatelessJdbcTokenStore oAuth2StatelessJdbcTokenStore() {
			return spy( new OAuth2StatelessJdbcTokenStore( dataSource() ) );
		}

		@Bean
		public ClientOAuth2AuthenticationSerializer clientOAuth2AuthenticationSerializer() {
			return spy( new ClientOAuth2AuthenticationSerializer() );
		}

		@Bean
		public UserOAuth2AuthenticationSerializer userOAuth2AuthenticationSerializer() {
			return spy( new UserOAuth2AuthenticationSerializer() );
		}

		@Bean
		public DummyOAuth2AuthenticationSerializer dummyOAuth2AuthenticationSerializer() {
			return spy( new DummyOAuth2AuthenticationSerializer() );
		}
	}

	private static class DummyOAuth2AuthenticationSerializer extends OAuth2AuthenticationSerializer {

		@Override
		protected byte[] serializePrincipal( Object object ) {
			return null;
		}

		@Override
		public OAuth2Authentication deserialize( AuthenticationSerializerObject serializerObject ) {
			return null;
		}

		@Override
		public boolean canSerialize( OAuth2Authentication authentication ) {
			return false;
		}
	}
}
