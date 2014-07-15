package com.foreach.across.modules.oauth2.it;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.hibernate.AcrossHibernateModule;
import com.foreach.across.modules.oauth2.OAuth2Module;
import com.foreach.across.modules.oauth2.services.OAuth2Service;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.test.AcrossTestContextConfiguration;
import com.foreach.across.test.AcrossTestContextConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(loader = AnnotationConfigWebContextLoader.class, classes = ITOAuth2Module.Config.class)
public class ITOAuth2Module {

    @Autowired
    private OAuth2Service oauth2Service;

    @Test
    public void verifyBootstrapped() {
        assertNotNull( oauth2Service );
    }

    @Configuration
    @Import(AcrossTestContextConfiguration.class)
    static class Config implements AcrossTestContextConfigurer {

        @Override
        public void configure( AcrossContext context ) {
            context.addModule( acrossHibernateModule() );
            context.addModule( userModule() );
            context.addModule( springSecurityModule() );
            context.addModule( oauth2Module() );
            context.addModule( acrossWebModule() );
        }

        private AcrossWebModule acrossWebModule() {
            return new AcrossWebModule();
        }

        private AcrossHibernateModule acrossHibernateModule() {
            return new AcrossHibernateModule();
        }

        private UserModule userModule() {
            return new UserModule();
        }

        private OAuth2Module oauth2Module() {
            return new OAuth2Module();
        }

        private SpringSecurityModule springSecurityModule() {
            return new SpringSecurityModule();
        }
    }
}
