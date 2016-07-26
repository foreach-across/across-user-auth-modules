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

import com.foreach.across.modules.user.business.UserDirectory;
import com.foreach.across.modules.user.repositories.UserDirectoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Service
public class UserDirectoryServiceImpl implements UserDirectoryService
{
	@Autowired
	private UserDirectoryRepository userDirectoryRepository;

	@Override
	public UserDirectory getDefaultUserDirectory() {
		return userDirectoryRepository.findOne( UserDirectory.DEFAULT_INTERNAL_DIRECTORY_ID );
	}

	@Override
	public Collection<UserDirectory> getActiveUserDirectories() {
		return getUserDirectories().stream()
		                           .filter( UserDirectory::isActive )
		                           .sorted( ( l, r ) -> Integer.compare( l.getOrder(), r.getOrder() ) )
		                           .collect( Collectors.toList() );
	}

	@Override
	public Collection<UserDirectory> getUserDirectories() {
		return userDirectoryRepository.findAll();
	}

	@Override
	public UserDirectory save( UserDirectory userDirectory ) {
		UserDirectory entity = userDirectoryRepository.save( userDirectory );
		BeanUtils.copyProperties( entity, userDirectory );

		return entity;
	}
}
