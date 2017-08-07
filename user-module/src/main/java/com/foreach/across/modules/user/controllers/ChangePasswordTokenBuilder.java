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

package com.foreach.across.modules.user.controllers;

import com.foreach.across.modules.user.business.User;
import com.foreach.across.modules.user.services.UserService;
import com.foreach.common.spring.code.MappedStringEncoder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
public class ChangePasswordTokenBuilder
{
	public static final ZoneId ZONE_ID_TO_USE = ZoneId.of( "Europe/Paris" );

	private final ChangePasswordControllerProperties configuration;
	private final UserService userService;

	private final Random random = new Random( System.currentTimeMillis() );

	@Setter
	private MappedStringEncoder checksumEncoder = new MappedStringEncoder( 6, false );

	@Setter
	private MappedStringEncoder longEncoder;

	public ChangePasswordTokenBuilder( ChangePasswordControllerProperties configuration, UserService userService ) {
		this.configuration = configuration;
		this.userService = userService;

		checksumEncoder = new MappedStringEncoder( 6, false );
		checksumEncoder.buildEncodingMatrix( "123456789".toCharArray(), 6, true );

		longEncoder = MappedStringEncoder.forMaximumValue( Long.MAX_VALUE, true );
	}

	/**
	 * Generate a token for a particular user, using the life time set in the configuration.
	 *
	 * @param user to build the token for
	 * @return token
	 */
	public ChangePasswordToken buildChangePasswordToken( User user ) {
		long checksum = generateRandomChecksum();
		long expireTime = ( LocalDateTime.now().atZone( ZONE_ID_TO_USE ).toInstant().toEpochMilli() +
				( configuration.getChangePasswordLinkValidityPeriodInSeconds() * 1000 ) );

		String token = longEncoder.encode( user.getId(), false ) + '-'
				+ longEncoder.encode( expireTime, false ) + '-'
				+ calculateSecurityHash( user, expireTime, checksum );

		return new ChangePasswordToken( token.toLowerCase(), checksumEncoder.encode( checksum, true ) );
	}

	private String calculateSecurityHash( User user, long expireTime, long checksum ) {
		return DigestUtils.md5DigestAsHex(
				(
						String.valueOf( user.getId() ) +
								user.getUsername() +
								user.getPassword() +
								expireTime +
								checksum +
								configuration.getHashToken()
				).getBytes()
		);
	}

	private long generateRandomChecksum() {
		return random.nextInt( Long.valueOf( checksumEncoder.getMaxValue() ).intValue() );
	}

	/**
	 * Decode a token into a matching {@link ChangePasswordRequest}.  If a token could not be decoded at all
	 * (no user found), the value might be empty.  Else a token will always be created but it is up to the
	 * client to check the corresponding properties ({@link ChangePasswordRequest#isValidToken()} and
	 * {@link ChangePasswordRequest#isExpired()} to determine if the request is in fact allowed.
	 * <p/>
	 * A token will only be valid if the checksum matched the rest of the token.
	 *
	 * @param token to decode
	 * @return request if token could be decoded
	 */
	public Optional<ChangePasswordRequest> decodeChangePasswordToken( ChangePasswordToken token ) {
		try {
			Assert.isTrue( StringUtils.isNotEmpty( token.getToken() ), "Token must not be empty." );
			Assert.isTrue( StringUtils.isNotEmpty( token.getChecksum() ), "Checksum must not be empty." );

			long checksum = checksumEncoder.decode( token.getChecksum() );

			String[] parts = token.getToken().split( "-" );
			long userId = longEncoder.decode( parts[0].toUpperCase() );
			long expireTime = longEncoder.decode( parts[1].toUpperCase() );

			User user = userService.getUserById( userId );

			boolean validToken = parts[2].equals( calculateSecurityHash( user, expireTime, checksum ) );
			ZonedDateTime expireLocalDate = ZonedDateTime.ofInstant( Instant.ofEpochMilli( expireTime ), ZONE_ID_TO_USE );
			boolean expired = expireLocalDate.isBefore( LocalDateTime.now().atZone( ZONE_ID_TO_USE ) );

			return Optional.of( new ChangePasswordRequest( user, expireLocalDate.toLocalDateTime(), validToken, expired ) );
		}
		catch ( Exception e ) {
			LOG.warn( "Attempt to decode an illegal ChangePasswordToken. ", e );
		}

		return Optional.empty();
	}

	/**
	 * Checks if the link is valid. Should return false if the checksum does not contain an identifier for a user.
	 *
	 * @param checksum
	 * @param configuration
	 * @return
	 */
	@Deprecated
	public boolean isValidLink( String checksum, ChangePasswordControllerProperties configuration ) {
		return true;
	}

	/**
	 * Retrieves the user embedded in the checksum. Should never return null. If the user does not exist, {@link #isValidLink(String, ChangePasswordControllerProperties)} should return false.
	 *
	 * @return
	 */
	@Deprecated
	public User getUser( String checksum ) {
		return null;
	}

}

