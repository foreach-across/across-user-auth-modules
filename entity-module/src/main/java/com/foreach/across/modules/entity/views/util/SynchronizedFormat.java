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
package com.foreach.across.modules.entity.views.util;

import org.springframework.util.Assert;

import java.lang.reflect.Array;
import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.ParsePosition;

/**
 * Simple wrapper that adds synchronization to a {@link Format} instance.
 *
 * @author Arne Vandamme
 */
public final class SynchronizedFormat extends Format
{
	private final Format format;

	public SynchronizedFormat( Format format ) {
		Assert.notNull( format );
		this.format = format;
	}

	@Override
	public synchronized StringBuffer format( Object obj, StringBuffer toAppendTo, FieldPosition pos ) {
		if ( format instanceof MessageFormat ) {
			// MessageFormat expects an array
			if ( !Array.class.isInstance( obj ) ) {
				return format.format( new Object[] { obj }, toAppendTo, pos );
			}
		}

		return format.format( obj, toAppendTo, pos );
	}

	@Override
	public synchronized Object parseObject( String source, ParsePosition pos ) {
		return format.parseObject( source, pos );
	}
}
