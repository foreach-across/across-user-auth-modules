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

package com.foreach.across.modules.user.services;

import com.foreach.across.modules.hibernate.jpa.config.HibernateJpaConfiguration;
import com.foreach.across.modules.spring.security.SpringSecurityModuleCache;
import com.foreach.across.modules.user.UserModuleCache;
import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.QUser;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.repositories.UserRepository;
import com.foreach.across.modules.user.services.support.DefaultUserDirectoryStrategy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserPropertiesService userPropertiesService;

	@Autowired
	private UserModifiedNotifier userModifiedNotifier;

	@Autowired
	private DefaultUserDirectoryStrategy defaultUserDirectoryStrategy;

	@Autowired
	private UserModuleSettings settings;

	@Autowired
	@Qualifier("userPasswordEncoder")
	private PasswordEncoder userPasswordEncoder;

	@Override
	public Collection<User> getUsers() {
		return userRepository.findAll();
	}

	@Cacheable(value = SpringSecurityModuleCache.SECURITY_PRINCIPAL, unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<User> getUserById( long id ) {
		return userRepository.findById( id );
	}

	@Cacheable(value = UserModuleCache.USERS, key = "('email:' + #email).toLowerCase()", unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<User> getUserByEmail( String email ) {
		return userRepository.findByEmail( email );
	}

	@Override
	public Optional<User> getUserByEmail( String email, UserDirectory userDirectory ) {
		QUser query = QUser.user;
		return userRepository.findOne( query.email.eq( email ).and( query.userDirectory.eq( userDirectory ) ) );
	}

	@Override
	public Optional<User> getUserByUsername( String username, UserDirectory userDirectory ) {
		QUser query = QUser.user;
		return userRepository.findOne( query.username.eq( username ).and( query.userDirectory.eq( userDirectory ) ) );
	}

	@Cacheable(value = UserModuleCache.USERS, key = "('username:' + #username).toLowerCase()", unless = SpringSecurityModuleCache.UNLESS_NULLS_ONLY)
	@Override
	public Optional<User> getUserByUsername( String username ) {
		return userRepository.findByUsername( username );
	}

	@Override
	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public User save( User userDto ) {
		defaultUserDirectoryStrategy.apply( userDto );

		User user;
		User originalUser = null;

		if ( userDto.isNew() ) {
			if ( StringUtils.isBlank( userDto.getPassword() ) ) {
				throw new UserModuleException( "A new user always requires a non-blank password to be set." );
			}

			user = new User();
		}
		else {
			long existingUserId = userDto.getId();

			if ( existingUserId == 0 ) {
				throw new UserModuleException(
						"Impossible to update a user with id 0, 0 is a special id that should never be used for persisted entities." );
			}

			user = getUserById( existingUserId ).orElse( null );

			if ( user == null ) {
				throw new UserModuleException(
						"Attempt to update user with id " + existingUserId + " but that user does not exist" );
			}

			originalUser = user;

			String currentPassword = user.getPassword();
			user = user.toDto();
			user.setPassword( currentPassword );
		}

		if ( settings.isUseEmailAsUsername() ) {
			if ( StringUtils.isBlank( userDto.getUsername() ) ) {
				userDto.setUsername( userDto.getEmail() );
			}

			// Update username to new email if it is modified and username was the old email
			if ( !userDto.isNew()
					&& !StringUtils.equals( userDto.getEmail(), user.getEmail() )
					&& StringUtils.equals( userDto.getUsername(), user.getEmail() ) ) {
				userDto.setUsername( userDto.getEmail() );
			}
		}

		Errors errors = new BeanPropertyBindingResult( userDto, "user" );
		userValidator.validate( userDto, errors );

		if ( errors.hasErrors() ) {
			throw new UserValidationException(
					"Failed to validate User, [" + errors.getErrorCount() + "] validation errors: " + StringUtils.join(
							errors.getAllErrors(), System.lineSeparator() ),
					errors.getAllErrors() );
		}

		BeanUtils.copyProperties( userDto, user, "password" );

		// Only modify password if password on the dto is not blank
		if ( !StringUtils.isBlank( userDto.getPassword() ) ) {
			user.setPassword( userPasswordEncoder.encode( userDto.getPassword() ) );
		}

		if ( StringUtils.isBlank( user.getDisplayName() ) ) {
			user.setDisplayName( String.format( "%s %s", user.getFirstName(), user.getLastName() ).trim() );
		}

		User saved = userRepository.save( user );

		BeanUtils.copyProperties( saved, userDto, "password" );

		if ( originalUser != null ) {
			userModifiedNotifier.update( originalUser, userDto );
		}

		return saved;
	}

	@Override
	@Transactional(HibernateJpaConfiguration.TRANSACTION_MANAGER)
	public void delete( long userId ) {
		userRepository.findById( userId )
		              .ifPresent( user -> {
			              deleteProperties( userId );
			              userRepository.delete( user );
		              } );
	}

	@Override
	public void deleteProperties( User user ) {
		deleteProperties( user.getId() );
	}

	@Override
	public void deleteProperties( long userId ) {
		userPropertiesService.deleteProperties( userId );
	}

	@Override
	public UserProperties getProperties( User user ) {
		return userPropertiesService.getProperties( user.getId() );
	}

	@Override
	public void saveProperties( UserProperties userProperties ) {
		userPropertiesService.saveProperties( userProperties );
	}

	@Transactional(value = HibernateJpaConfiguration.TRANSACTION_MANAGER, readOnly = true)
	@Override
	public Collection<User> getUsersWithPropertyValue( String propertyName, Object propertyValue ) {
		Collection<Long> userIds = userPropertiesService.getEntityIdsForPropertyValue( propertyName, propertyValue );

		if ( userIds.isEmpty() ) {
			return Collections.emptyList();
		}

		return userRepository.findAllById( userIds );
	}

	@Override
	public Collection<User> findAll( Predicate predicate ) {
		return (Collection<User>) userRepository.findAll( predicate );
	}

	@Override
	public Iterable<User> findAll( Predicate predicate, Sort sort ) {
		return null;
	}

	@Override
	public Optional<User> findOne( Predicate predicate ) {
		return userRepository.findOne( predicate );
	}

	@Override
	public Collection<User> findAll( Predicate predicate, OrderSpecifier<?>... orderSpecifiers ) {
		return (Collection<User>) userRepository.findAll( predicate, orderSpecifiers );
	}

	@Override
	public Iterable<User> findAll( OrderSpecifier<?>... orders ) {
		return userRepository.findAll( orders );
	}

	@Override
	public Page<User> findAll( Predicate predicate, Pageable pageable ) {
		return userRepository.findAll( predicate, pageable );
	}

	@Override
	public long count( Predicate predicate ) {
		return userRepository.count( predicate );
	}

	@Override
	public boolean exists( Predicate predicate ) {
		return userRepository.exists( predicate );
	}

	@Override
	public <S extends User, R> R findBy( Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction ) {
		return userRepository.findBy( predicate, queryFunction );
	}
}
