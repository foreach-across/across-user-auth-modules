package com.foreach.across.modules.user.services;

import com.foreach.across.modules.user.UserModuleSettings;
import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.business.UserProperties;
import com.foreach.across.modules.user.dto.UserDto;
import com.foreach.across.modules.user.repositories.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private UserPropertiesService userPropertiesService;

	private final PasswordEncoder passwordEncoder;
	private final boolean useEmailAsUsername;
	private final boolean requireEmailUnique;

	public UserServiceImpl( PasswordEncoder passwordEncoder, boolean useEmailAsUsername, boolean requireEmailUnique ) {
		Assert.notNull( passwordEncoder, "A UserService must be configured with a valid PasswordEncoder" );
		this.passwordEncoder = passwordEncoder;
		this.useEmailAsUsername = useEmailAsUsername;
		this.requireEmailUnique = requireEmailUnique;
	}

	@PostConstruct
	protected void validateServiceConfiguration() {
		if ( useEmailAsUsername && !requireEmailUnique ) {
			throw new RuntimeException(
					UserModuleSettings.REQUIRE_EMAIL_UNIQUE + " must be TRUE if " + UserModuleSettings.USE_EMAIL_AS_USERNAME + " is TRUE" );
		}
	}

	public boolean isUseEmailAsUsername() {
		return useEmailAsUsername;
	}

	public boolean isRequireEmailUnique() {
		return requireEmailUnique;
	}

	@Override
	public Collection<User> getUsers() {
		return userRepository.getAll();
	}

	@Override
	public User getUserById( long id ) {
		return userRepository.getById( id );
	}

	@Override
	public User getUserByEmail( String email ) {
		return userRepository.getByEmail( email );
	}

	@Override
	public User getUserByUsername( String username ) {
		return userRepository.getByUsername( username );
	}

	@Override
	public UserDto createUserDto( User user ) {
		return new UserDto( user );
	}

	@Override
	@Transactional
	public User save( UserDto userDto ) {
		User user;

		if ( userDto.isNewEntity() ) {
			user = new User();

			if ( StringUtils.isBlank( userDto.getPassword() ) ) {
				throw new UserModuleException( "A new user always requires a non-blank password to be set." );
			}
		}
		else {
			long existingUserId = userDto.getId();

			if ( existingUserId == 0 ) {
				throw new UserModuleException(
						"Impossible to update a user with id 0, 0 is a special id that should never be used for persisted entities." );
			}

			user = getUserById( existingUserId );

			if ( user == null ) {
				throw new UserModuleException(
						"Attempt to update user with id " + existingUserId + " but that user does not exist" );
			}
		}

		if ( useEmailAsUsername ) {
			userDto.setUsername( userDto.getEmail() );
		}

		BeanUtils.copyProperties( userDto, user, "password" );

		Errors errors = new BeanPropertyBindingResult( userDto, "user" );
		userValidator.validate( userDto, errors );
		if ( errors.hasErrors() ) {
			throw new UserValidationException(
					"Failed to validate User, [" + errors.getErrorCount() + "] validation errors: " + StringUtils.join(
							errors.getAllErrors(), System.lineSeparator() ),
					errors.getAllErrors() );
		}

		// Only modify password if password on the dto is not blank
		if ( !StringUtils.isBlank( userDto.getPassword() ) ) {
			user.setPassword( passwordEncoder.encode( userDto.getPassword() ) );
		}

		if ( StringUtils.isBlank( user.getDisplayName() ) ) {
			user.setDisplayName( String.format( "%s %s", user.getFirstName(), user.getLastName() ).trim() );
		}

		if ( userDto.isNewEntity() ) {
			userRepository.create( user );
		}
		else {
			userRepository.update( user );
		}

		userDto.copyFrom( user );

		return user;
	}

	@Override
	@Transactional
	public void delete( long userId ) {
		User user = userRepository.getById( userId );
		deleteProperties( userId );
		userRepository.delete( user );
	}

	@Override
	public void deleteProperties( Long userId ) {
		userPropertiesService.deleteProperties( userId );
	}

	public UserProperties getProperties( User user ) {
		return getProperties( user.getId() );
	}

	public UserProperties getProperties( UserDto userDto ) {
		return getProperties( userDto.getId() );
	}

	@Override
	public UserProperties getProperties( Long userId ) {
		return userPropertiesService.getProperties( userId );
	}

	@Override
	public void saveProperties( UserProperties userProperties ) {
		userPropertiesService.saveProperties( userProperties );
	}
}
