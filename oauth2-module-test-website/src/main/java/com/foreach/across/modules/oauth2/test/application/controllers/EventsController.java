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

package com.foreach.across.modules.oauth2.test.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Controller
public class EventsController
{
	private final ObjectMapper objectMapper = new ObjectMapper();

	List<Object> events = new ArrayList<>();

	@EventListener
	public void receiveEvent( Object event ) {
		events.add( event );
	}

	@GetMapping("/events")
	@ResponseBody
	public String listEvents() {
		return events.stream()
		             .map( Object::getClass )
		             .map( Class::getName )
		             .collect( Collectors.joining( "<br/>" ) );
	}
}
