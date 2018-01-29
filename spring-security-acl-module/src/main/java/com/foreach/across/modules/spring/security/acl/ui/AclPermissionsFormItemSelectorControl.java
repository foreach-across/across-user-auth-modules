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

package com.foreach.across.modules.spring.security.acl.ui;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FormControlElement;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.Assert;

/**
 * Custom {@link ViewElementBuilder} for creating the default item selector control for a {@link AclPermissionsFormSection}.
 * The default item selector consists of a simple control and a button to add the value of said control.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Setter
@Accessors(fluent = true)
public final class AclPermissionsFormItemSelectorControl extends AbstractNodeViewElementBuilder<NodeViewElement, AclPermissionsFormItemSelectorControl>
{
	private ViewElementBuilder control;

	/**
	 * Set the actual control that should be rendered inside the item selector row.
	 * This control should contain the value that should be added.
	 *
	 * @param controlElement control to render
	 * @return self
	 */
	public AclPermissionsFormItemSelectorControl control( ViewElement controlElement ) {
		return control( builderContext -> controlElement );
	}

	/**
	 * Set the actual control that should be rendered inside the item selector row.
	 * This control should contain the value that should be added.
	 *
	 * @param controlBuilder control as builder
	 * @return self
	 */
	public AclPermissionsFormItemSelectorControl control( ViewElementBuilder controlBuilder ) {
		this.control = controlBuilder;
		return this;
	}

	@Override
	protected NodeViewElement createElement( ViewElementBuilderContext builderContext ) {
		Assert.notNull( control, "An AclPermissionsFormItemSelectorControl requires a control property" );

		NodeViewElement row = new NodeViewElement( "div" );
		row.addCssClass( "acl-permissions-form-selector" );

		ViewElement actualControl = control.build( builderContext );
		Assert.notNull( actualControl, "An AclPermissionsFormItemSelectorControl requires a valid control" );

		FormControlElement formControl = BootstrapElementUtils.getFormControl( actualControl );

		if ( formControl != null ) {
			row.addChild( actualControl );
		}

		ButtonViewElement button = new ButtonViewElement();
		button.addCssClass( "acl-permissions-form-selector-button" );
		button.setIcon( new GlyphIcon( GlyphIcon.PLUS_SIGN ) );

		row.addChild( button );

		return apply( row, builderContext );
	}
}
