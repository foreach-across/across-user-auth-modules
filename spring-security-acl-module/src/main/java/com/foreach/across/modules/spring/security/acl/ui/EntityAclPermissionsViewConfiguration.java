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

package com.foreach.across.modules.spring.security.acl.ui;

import com.foreach.across.core.annotations.ConditionalOnAcrossModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@ConditionalOnAcrossModule(EntityModule.NAME)
@ConditionalOnClass(EntityConfigurer.class)
@ComponentScan
@RequiredArgsConstructor
public class EntityAclPermissionsViewConfiguration implements EntityConfigurer
{
	private final EntityAclPermissionsViewProcessor processor;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.assignableTo( IdBasedEntity.class )
		        .formView( "aclPermissions", vb ->
				        vb.showProperties()
				          .viewProcessor( processor )
		        );
	}
}

