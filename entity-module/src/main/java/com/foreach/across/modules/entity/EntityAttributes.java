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
package com.foreach.across.modules.entity;

import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;

/**
 * Contains common Entity attribute keys.
 */
public interface EntityAttributes
{
	/**
	 * If set, this attribute should contain the
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertySelector} to be used for selecting
	 * the members of a {@link FieldsetFormElement}.
	 */
	String FIELDSET_PROPERTY_SELECTOR = FieldsetFormElement.class.getName() + ".EntityPropertySelector";
}
