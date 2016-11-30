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

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.user.business.QGroup;
import com.foreach.across.modules.user.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Arne Vandamme
 */
@AdminWebController
@RequestMapping("/autosuggest")
@ResponseBody
public class AutoSuggestController
{
	@Autowired
	private GroupRepository groupRepository;

	@RequestMapping("/groups")
	public List<AutosuggestItemViewHelper> suggestGroups( @RequestParam(value = "query") String query ) {

		QGroup group = QGroup.group;

		return StreamSupport
				.stream( groupRepository.findAll( group.name.containsIgnoreCase( query ) ).spliterator(),
				         false )
				.map( a -> new AutosuggestItemViewHelper( a.getId(), a.getName() ) )
				.collect( Collectors.toList() );

		/*QCustomer q = QCustomer.customer;
		Predicate predicate = q.name.containsIgnoreCase( query );

		Iterable<Customer> customers = customerService.getCustomers( predicate );

		return StreamSupport.stream( customers.spliterator(), true )
		                    .map( a -> new AutosuggestItemViewHelper( a.getId(), a.getName() ) )
		                    .collect( Collectors.toList() );*/
	}

	public class AutosuggestItemViewHelper
	{
		private Long id;
		private String description;

		public AutosuggestItemViewHelper( Long id, String description ) {
			this.id = id;
			this.description = description;
		}

		public Long getId() {
			return id;
		}

		public void setId( Long id ) {
			this.id = id;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription( String description ) {
			this.description = description;
		}
	}
}
