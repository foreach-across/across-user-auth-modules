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

package com.foreach.across.samples.user;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.user.UserModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AcrossApplication(modules = {
		UserModule.NAME, AdminWebModule.NAME, EntityModule.NAME
})
@Import({ DataSourceAutoConfiguration.class, H2ConsoleAutoConfiguration.class })
public class UserModuleApplication
{
	@Bean
	public PasswordEncoder userPasswordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost( "localhost" );
		mailSender.setPort( 25 );
		mailSender.setProtocol( "smtp" );
		Properties properties = new Properties();
		properties.put( "mail.smtp.connectiontimeout", 3000 );
		properties.put( "mail.smtp.timeout", 3000 );
		properties.put( "mail.smtps.auth", true );
		properties.put( "mail.smtp.ssl.enable", true );
		properties.put( "mail.transport.protocol", "smtp" );
		mailSender.setJavaMailProperties( properties );
		return mailSender;
	}

	public static void main( String[] args ) {
		SpringApplication.run( UserModuleApplication.class, args );
	}
}
