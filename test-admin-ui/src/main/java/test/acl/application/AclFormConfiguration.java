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

package test.acl.application;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.spring.security.acl.business.AclPermission;
import com.foreach.across.modules.spring.security.acl.business.AclSecurityEntity;
import com.foreach.across.modules.spring.security.acl.ui.AclPermissionsForm;
import com.foreach.across.modules.spring.security.acl.ui.AclPermissionsFormRegistry;
import com.foreach.across.modules.spring.security.acl.ui.AclPermissionsFormSection;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalRetrievalStrategy;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import test.acl.application.domain.customer.Customer;
import test.acl.application.domain.group.Group;
import test.acl.application.domain.group.GroupRepository;
import test.acl.application.domain.user.User;
import test.acl.application.domain.user.UserRepository;

import static com.foreach.across.modules.entity.views.EntityViewCustomizers.basicSettings;
import static com.foreach.across.modules.spring.security.acl.ui.AclPermissionsForm.permissionGroup;

/**
 * Sample configuration building ACL form profiles and registering to corresponding entities.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
@RequiredArgsConstructor
class AclFormConfiguration implements EntityConfigurer
{
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Customer.class )
		        .attribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, "with-anonymous" )
		        .view( AclPermissionsForm.VIEW_NAME, basicSettings().adminMenu( "/advanced-options/aclPermissions" ) );

		entities.withType( Group.class )
		        .attribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, "group-user" );
		entities.withType( AclSecurityEntity.class )
		        .attribute( AclPermissionsFormRegistry.ATTR_ACL_PROFILE, "group-user" );
	}

	@Bean
	@Exposed
	SecurityPrincipalRetrievalStrategy securityPrincipalRetrievalStrategy() {
		return principalName -> {
			if ( principalName.startsWith( "group:" ) ) {
				return groupRepository.findOneByName( principalName.replaceFirst( "group:", "" ) );
			}
			else {
				return userRepository.findOneByName( principalName );
			}
		};
	}

	@Autowired
	void registerAclForms( AclPermissionsFormRegistry formRegistry ) {
		formRegistry.put(
				"group-user",
				AclPermissionsForm
						.builder()
						.name( "group-user" )
						.section(
								AclPermissionsFormSection
										.builder()
										.name( "group" )
										.entityType( Group.class )
										.permissionGroups(
												permissionGroup( "" )
														.permissionsSupplier( () -> new Permission[] { AclPermission.READ, AclPermission.WRITE,
														                                               AclPermission.DELETE } )
														.build()
										)
										/*
										.sidMatcher( ( sid, entity ) -> entity instanceof Group )
										.sidForObjectResolver( group -> new PrincipalSid( ( (Group) group ).getPrincipalName() ) )
										.objectForSidResolver(
												sid -> groupRepository.findOneByName( ( (PrincipalSid) sid ).getPrincipal().replaceFirst( "group:", "" ) )
										)
										.transportIdForObjectResolver( group -> ( (Group) group ).getId() )
										.objectForTransportIdResolver( transportId -> groupRepository.findOne( Long.parseLong( transportId ) ) )
										.objectLabelViewElementProvider( ( object, builderContext ) -> TextViewElement.text( object.toString() ) )
										*/
										.build()
						)
						.section(
								AclPermissionsFormSection
										.builder()
										.name( "user" )
										.permissionGroups(
												permissionGroup( "create" )
														.permissions( AclPermission.CREATE )
														.build(),
												permissionGroup( "read-write" )
														.permissions( AclPermission.READ, AclPermission.WRITE )
														.build(),
												permissionGroup( "" )
														.permissions( AclPermission.DELETE, AclPermission.ADMINISTRATION )
														.build()
										)
										.sidMatcher( ( sid, entity ) -> entity instanceof User )
										.sidForObjectResolver( user -> new PrincipalSid( ( (User) user ).getPrincipalName() ) )
										.objectForSidResolver(
												sid -> userRepository.findOneByName( ( (PrincipalSid) sid ).getPrincipal() )
										)
										.transportIdForObjectResolver( user -> ( (User) user ).getName() )
										.objectForTransportIdResolver( userRepository::findOneByName )
										.objectLabelViewElementProvider( ( object, builderContext ) -> TextViewElement.text( ( (User) object ).getName() ) )
										.itemSelectorBuilder( AclPermissionsForm.selectorControl().control(
												BootstrapUiBuilders.formGroup().control( BootstrapUiBuilders.textbox() ) )
										)
										.build()
						)
						.build()
		);

		formRegistry.put(
				"with-anonymous",
				AclPermissionsForm
						.builder()
						.section( formRegistry.get( "group-user" ).getSectionWithName( "user" ) )
						.section(
								AclPermissionsFormSection
										.builder()
										.name( "global" )
										.permissions( AclPermission.READ, AclPermission.WRITE )
										.sidMatcher( ( sid, entity ) -> sid instanceof GrantedAuthoritySid )
										.sidForObjectResolver( object -> (Sid) object )
										.objectForSidResolver( sid -> sid )
										.transportIdForObjectResolver( object -> ( (GrantedAuthoritySid) object ).getGrantedAuthority() )
										.objectForTransportIdResolver( GrantedAuthoritySid::new )
										.objectLabelViewElementProvider(
												( object, builderContext ) -> TextViewElement.text( ( (GrantedAuthoritySid) object ).getGrantedAuthority() )
										)
										.build()
						)
						.build()
		);

		//views[aclPermissions].aclForm[group-user].section[name].title, subTitle
		//views[aclPermissions].permission[read]=BOE
	}
}
