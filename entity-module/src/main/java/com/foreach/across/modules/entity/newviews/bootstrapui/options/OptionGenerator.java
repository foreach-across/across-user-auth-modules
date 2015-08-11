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
package com.foreach.across.modules.entity.newviews.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Wrapper that generates the children of an {@link OptionsFormElementBuilder}.
 * Requires an {@link OptionIterableBuilder} that is responsible for creating
 * the initial collection of {@link OptionFormElementBuilder}s.</p>
 * <p>When the {@link #setSorted(boolean)}</p> property is true, the options will be sorted by label and text
 * in the resulting set.</p>
 * <p>In case of a resulting {@link com.foreach.across.modules.bootstrapui.elements.SelectFormElement} an
 * empty option will be added if none is selected or if the element is not required.  The empty
 * option can be overridden (and set to null) using {@link #setEmptyOption(OptionFormElementBuilder)}.</p>
 *
 * @author Arne Vandamme
 * @see OptionsFormElementBuilder
 * @see OptionIterableBuilder
 */
public class OptionGenerator implements ViewElementBuilder<ContainerViewElement>
{
	private OptionIterableBuilder options;
	private boolean sorted;
	private OptionFormElementBuilder emptyOption;

	public OptionGenerator() {
		emptyOption = new OptionFormElementBuilder().label( "" ).value( "" );
	}

	/**
	 * @param options iterable builder generating the list of options
	 */
	public void setOptions( OptionIterableBuilder options ) {
		this.options = options;
	}

	/**
	 * @param sorted true if the options should be sorted by name
	 */
	public void setSorted( boolean sorted ) {
		this.sorted = sorted;
	}

	/**
	 * @param emptyOption to include if none is selected or a value is not required
	 */
	public void setEmptyOption( OptionFormElementBuilder emptyOption ) {
		this.emptyOption = emptyOption;
	}

	@Override
	public ContainerViewElement build( ViewElementBuilderContext builderContext ) {
		ContainerViewElement container = new ContainerViewElement();

		OptionsFormElementBuilder optionsBuilder = builderContext.getAttribute( OptionsFormElementBuilder.class );
		Assert.notNull( optionsBuilder );

		boolean hasSelected = false;
		List<OptionFormElementBuilder> actual = new ArrayList<>();

		if ( options != null ) {
			for ( OptionFormElementBuilder option : options.buildOptions( builderContext ) ) {
				actual.add( option );
				hasSelected |= option.isSelected();
			}

			if ( sorted ) {
				Collections.sort( actual );
			}
		}

		boolean shouldAddEmptyOption = emptyOption != null
				&& optionsBuilder.getType() == OptionsFormElementBuilder.Type.SELECT
				&& ( !hasSelected || !optionsBuilder.isRequired() );

		if ( shouldAddEmptyOption ) {
			container.add( emptyOption.build( builderContext ) );
		}

		for ( OptionFormElementBuilder option : actual ) {
			container.add( option.build( builderContext ) );
		}

		return container;
	}

}
