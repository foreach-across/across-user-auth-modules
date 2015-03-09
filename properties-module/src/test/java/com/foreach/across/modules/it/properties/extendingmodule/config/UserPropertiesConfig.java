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
package com.foreach.across.modules.it.properties.extendingmodule.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.it.properties.definingmodule.registry.UserPropertyRegistry;
import com.foreach.common.spring.properties.support.SingletonPropertyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Registers the custom properties.
 *
 * @author Arne Vandamme
 */
@Configuration
public class UserPropertiesConfig
{
	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossModule currentModule;

	public static final String BOOLEAN = "extending.booleanProperty";
	public static final String DATE = "extending.dateProperty";
	public static final String DECIMAL = "extending.decimalProperty";

	@Autowired
	private UserPropertyRegistry userPropertyRegistry;

	@PostConstruct
	private void registerProperties() {
		userPropertyRegistry.register( currentModule, BOOLEAN, Boolean.class,
		                               SingletonPropertyFactory.<String, Boolean>forValue( false ) );
		userPropertyRegistry.register( currentModule, DATE, Date.class );
		userPropertyRegistry.register( currentModule, DECIMAL, BigDecimal.class,
		                               SingletonPropertyFactory.<String, BigDecimal>forValue( new BigDecimal( 0 ) ) );
	}
}
