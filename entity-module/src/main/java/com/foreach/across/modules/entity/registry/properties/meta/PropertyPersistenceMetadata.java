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
package com.foreach.across.modules.entity.registry.properties.meta;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;

/**
 * Contains persistence related metadata that is not present in {@link org.springframework.data.mapping.PersistentProperty}.
 * Usually if an {@link EntityPropertyDescriptor} has this attribute, it also has a
 * {@link org.springframework.data.mapping.PersistentProperty} attribute.
 *
 * @author Niels Doucet, Arne Vandamme
 */
public class PropertyPersistenceMetadata
{
	private boolean embedded;

	public void setEmbedded( boolean embedded ) {
		this.embedded = embedded;
	}

	public boolean isEmbedded() {
		return embedded;
	}

	/**
	 * Verify if a {@link EntityPropertyDescriptor} has a {@link PropertyPersistenceMetadata} attribute
	 * where {@link #isEmbedded()} returns {@code true}.
	 *
	 * @param descriptor to check
	 * @return true if the property is an embedded type
	 */
	public static boolean isEmbeddedProperty( EntityPropertyDescriptor descriptor ) {
		PropertyPersistenceMetadata metadata = descriptor.getAttribute( PropertyPersistenceMetadata.class );

		return metadata != null && metadata.isEmbedded();
	}
}
