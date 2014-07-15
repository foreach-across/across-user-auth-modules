package com.foreach.across.modules.user.it;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserRestriction;
import com.foreach.across.modules.user.config.UserSchemaConfiguration;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossTestContextConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = ITUserModuleRenamedTables.Config.class)
public class ITUserModuleRenamedTables
{
	@Autowired
	private UserService userService;

	@Test
	public void verifyBootstrapped() {
		assertNotNull( userService );
		User admin = userService.getUserByUsername( "admin" );
		assertNotNull( admin );
		assertEquals( "admin", admin.getUsername() );
		assertEquals( EnumSet.noneOf( UserRestriction.class ), admin.getRestrictions() );
		assertEquals( false, admin.getDeleted() );
		assertEquals( true, admin.getEmailConfirmed() );

		assertEquals( true, admin.isEnabled() );
		assertEquals( true, admin.isAccountNonExpired() );
		assertEquals( true, admin.isAccountNonLocked() );
		assertEquals( true, admin.isCredentialsNonExpired() );
	}

	@Configuration
	@AcrossTestConfiguration
	static class Config implements AcrossTestContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( acrossHibernateModule() );
			context.addModule( userModule() );
		}

		private AcrossHibernateModule acrossHibernateModule() {
			return new AcrossHibernateModule();
		}

		private UserModule userModule() {
			UserModule userModule = new UserModule();

			SchemaConfiguration schema = userModule.getSchemaConfiguration();
			schema.renameTable( UserSchemaConfiguration.TABLE_PERMISSION, "permissies" );
			schema.renameTable( UserSchemaConfiguration.TABLE_USER, "gebruikers" );
			schema.renameTable( UserSchemaConfiguration.TABLE_PERMISSION_GROUP, "permissie_groepen" );
			schema.renameTable( UserSchemaConfiguration.TABLE_ROLE, "rollen" );
			schema.renameTable( UserSchemaConfiguration.TABLE_ROLE_PERMISSION, "rol_permissie" );
			schema.renameTable( UserSchemaConfiguration.TABLE_USER_ROLE, "gebruiker_rol" );

			return userModule;
		}
	}
}
