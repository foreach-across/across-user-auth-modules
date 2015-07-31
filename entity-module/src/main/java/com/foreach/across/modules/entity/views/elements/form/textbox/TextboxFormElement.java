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
package com.foreach.across.modules.entity.views.elements.form.textbox;

import com.foreach.across.modules.entity.views.elements.CommonViewElements;
import com.foreach.across.modules.entity.views.elements.form.FormElementSupport;

/**
 * Represents a HTML "text" input type as well as "textarea" types.
 */
@Deprecated
public class TextboxFormElement extends FormElementSupport
{
	public static final String TYPE = CommonViewElements.TEXTBOX;

	private Integer maxLength;
	private boolean url= false;
	private boolean multiLine = true;

	public TextboxFormElement() {
		super( TYPE );
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength( Integer maxLength ) {
		this.maxLength = maxLength;
	}

	public boolean isUrl() {
		return url;
	}

	public void setUrl( boolean url ) {
		this.url = url;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine( boolean multiLine ) {
		this.multiLine = multiLine;
	}
}
