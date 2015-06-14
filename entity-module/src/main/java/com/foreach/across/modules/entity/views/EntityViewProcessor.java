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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import org.springframework.ui.ModelMap;
import org.springframework.validation.DataBinder;

/**
 * @author Arne Vandamme
 */
public interface EntityViewProcessor<V extends ViewCreationContext, T extends EntityView>
{
	void prepareModelAndCommand( String viewName,
	                             V creationContext,
	                             EntityViewCommand command,
	                             ModelMap model );

	void prepareDataBinder( String viewName, V creationContext, EntityViewCommand command, DataBinder dataBinder );

	void preProcess( V creationContext, T view );

	void postProcess( V creationContext, T view );
}
