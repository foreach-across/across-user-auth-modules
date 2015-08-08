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
package com.foreach.across.modules.entity.newviews;

import java.util.Objects;

/**
 * Represents the mode for which a {@link com.foreach.across.modules.web.ui.ViewElementBuilder}
 * is being requested.  Two default modes exist: {@link #FORM_READ} and {@link #FORM_WRITE}.
 * A mode is essentially represented by a string, so it is easy to add custom modes.
 *
 * @author Arne Vandamme
 */
public class ViewElementMode
{
	/**
	 * Only the label text of the descriptor.
	 */
	public static final ViewElementMode LABEL = new ViewElementMode( "LABEL" );

	/**
	 * Only the (readonly) value of the descriptor.
	 */
	public static final ViewElementMode VALUE = new ViewElementMode( "VALUE" );

	/**
	 * Form control for modifying the descriptor.
	 */
	public static final ViewElementMode CONTROL = new ViewElementMode( "CONTROL" );

	/**
	 * Only the label text of the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_LABEL = new ViewElementMode( "LIST_LABEL" );

	/**
	 * Only the (readonly) value of the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_VALUE = new ViewElementMode( "LIST_VALUE" );

	/**
	 * Form control for modifying the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_CONTROL = new ViewElementMode( "LIST_CONTROL" );

	/**
	 * Control for detail (form) view.
	 */
	public static final ViewElementMode FORM_READ = new ViewElementMode( "FORM_READ" );

	/**
	 * Control for modifying form view.
	 */
	public static final ViewElementMode FORM_WRITE = new ViewElementMode( "FORM_WRITE" );

	private final String type;

	public ViewElementMode( String type ) {
		this.type = type;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ViewElementMode that = (ViewElementMode) o;
		return Objects.equals( type, that.type );
	}

	@Override
	public int hashCode() {
		return Objects.hash( type );
	}

	public static boolean isList( ViewElementMode mode ) {
		return LIST_LABEL.equals( mode ) || LIST_VALUE.equals( mode );
	}

	public static boolean isLabel( ViewElementMode mode ) {
		return LABEL.equals( mode ) || LIST_LABEL.equals( mode );
	}

	public static boolean isValue( ViewElementMode mode ) {
		return VALUE.equals( mode ) || LIST_VALUE.equals( mode );
	}

	public static boolean isControl( ViewElementMode mode ) {
		return CONTROL.equals( mode ) || LIST_CONTROL.equals( mode );
	}
}
