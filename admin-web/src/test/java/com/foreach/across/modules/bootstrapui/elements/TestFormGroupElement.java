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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

/**
 * @author Arne Vandamme
 */
public class TestFormGroupElement extends AbstractBootstrapViewElementTest
{
	private static final String INPUT_GROUP_ADDON = "<span class='input-group-addon'>" +
			"<span aria-hidden='true' class='glyphicon glyphicon-alert'></span>" +
			"</span>";

	private FormGroupElement group, groupWithInputGroup;

	@Before
	public void before() {
		group = new FormGroupElement();

		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "control" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "title" );

		group.setLabel( label );
		group.setControl( textbox );

		groupWithInputGroup = new FormGroupElement();

		InputGroupFormElement inputGroupFormElement = new InputGroupFormElement();
		inputGroupFormElement.setControl( textbox );
		inputGroupFormElement.setAddonBefore( new GlyphIcon( GlyphIcon.ALERT ) );

		LabelFormElement inputGroupLabel= new LabelFormElement();
		inputGroupLabel.setTarget( inputGroupFormElement );
		inputGroupLabel.setText( "title input group" );

		groupWithInputGroup.setLabel( inputGroupLabel );
		groupWithInputGroup.setControl( inputGroupFormElement );
	}

	@Test
	public void simpleGroup() {
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void formGroupWithInputGroup() {
		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void requiredGroup() {
		group.setRequired( true );

		renderAndExpect(
				group,
				"<div class='form-group required'>" +
						"<label for='control' class='control-label'>title<sup class='required'>*</sup></label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void requiredGroupWithInputGroup() {
		groupWithInputGroup.setRequired( true );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group required'>" +
						"<label for='control' class='control-label'>title input group<sup class='required'>*</sup></label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void customTagAppended() {
		NodeViewElement div = new NodeViewElement( "div" );
		div.setAttribute( "class", "some-class" );
		div.add( new TextViewElement( "appended div" ) );

		group.add( div );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"<div class='some-class'>appended div</div>" +
						"</div>"
		);

		groupWithInputGroup.add( div );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"<div class='some-class'>appended div</div>" +
						"</div>"
		);
	}

	@Test
	public void withHelpText() {
		NodeViewElement help = new NodeViewElement( "p" );
		help.setAttribute( "class", "help-block" );
		help.add( new TextViewElement( "example help text" ) );

		group.setHelpBlock( help );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"</div>"
		);

		group.setRenderHelpBlockBeforeControl( true );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"</div>"
		);

		SelectFormElement select = new SelectFormElement();
		select.setName( "list" );

		group.setControl( select );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<p class='help-block' id='list.help'>example help text</p>" +
						"<select class='form-control' name='list' id='list' aria-describedby='list.help' />" +
						"</div>"
		);

		groupWithInputGroup.setHelpBlock( help );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"</div>" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"</div>"
		);

		group.setRenderHelpBlockBeforeControl( true );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title input group</label>" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void inlineFormLayoutWithVisibleLabel() {
		group.setFormLayout( FormLayout.inline( true ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);

		groupWithInputGroup.setFormLayout( FormLayout.inline( true ) );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void inlineFormLayoutWithHiddenLabel() {
		group.setFormLayout( FormLayout.inline( false ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' placeholder='title' />" +
						"</div>"
		);

		group.getControl( TextboxFormElement.class ).setPlaceholder( "some placeholder" );
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' placeholder='some placeholder' />" +
						"</div>"
		);

		group.getControl( TextboxFormElement.class ).setPlaceholder( "" );
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' placeholder='' />" +
						"</div>"
		);

		groupWithInputGroup.setFormLayout( FormLayout.inline( false ) );
		groupWithInputGroup.getControl( InputGroupFormElement.class )
		                   .getControl( TextboxFormElement.class )
		                   .setPlaceholder( null );
		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' placeholder='title input group' />" +
						"</div>" +
						"</div>"
		);

		BootstrapElementUtils.getFormControl( groupWithInputGroup, TextboxFormElement.class )
		                     .setPlaceholder( "some placeholder" );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' placeholder='some placeholder' />" +
						"</div>" +
						"</div>"
		);

		BootstrapElementUtils.getFormControl( groupWithInputGroup, TextboxFormElement.class )
		                     .setPlaceholder( "" );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' placeholder='' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void inlineFormDoesNotVisuallyRenderHelp() {
		NodeViewElement help = new NodeViewElement( "p" );
		help.setAttribute( "class", "help-block" );
		help.add( new TextViewElement( "example help text" ) );

		group.setHelpBlock( help );
		group.setFormLayout( FormLayout.inline( true ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"<p class='help-block sr-only' id='control.help'>example help text</p>" +
						"</div>"
		);

		group.setFormLayout( FormLayout.inline( false ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' placeholder='title' />" +
						"<p class='help-block sr-only' id='control.help'>example help text</p>" +
						"</div>"
		);
	}

	@Test
	public void horizontalFormLayout() {
		NodeViewElement help = new NodeViewElement( "p" );
		help.setAttribute( "class", "help-block" );
		help.add( new TextViewElement( "example help text" ) );

		group.setFormLayout( FormLayout.horizontal( 2 ) );
		group.setHelpBlock( help );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label col-md-2'>title</label>" +
						"<div class='col-md-10'>" +
						"<input type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"</div>" +
						"</div>"
		);

		groupWithInputGroup.setFormLayout( FormLayout.horizontal( 6 ) );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='control-label col-md-6'>title input group</label>" +
						"<div class='col-md-6'>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void horizontalFormLayoutWithoutLabel() {
		CheckboxFormElement checkbox = new CheckboxFormElement();
		checkbox.setText( "checkbox value" );

		group = new FormGroupElement();
		group.setFormLayout( FormLayout.horizontal( 2 ) );
		group.setControl( checkbox );

		renderAndExpect(
				group,
				"<div class='form-group checkbox'>" +
						"<div class='col-md-10 col-md-offset-2'>" +
						"<div class='checkbox'><label>" +
						"<input type='checkbox' />checkbox value" +
						"</label>" +
						"</div>" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void errorFromBoundObject() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/formObject" );

		renderAndExpect(
				container,
				model -> {
					TestClass target = new TestClass( "test value" );
					BindingResult errors = new BeanPropertyBindingResult( target, "item" );
					errors.rejectValue( "control", "broken", "broken" );

					model.addAttribute( BindingResult.MODEL_KEY_PREFIX + "item", errors );
					model.addAttribute( "item", target );
					model.addAttribute( "formGroup", group );
				},
				"<div class='form-group has-error'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"<div class='small text-danger'>broken</div>" +
						"</div>"
		);
	}

	@Test
	public void errorFromFormCommand() {
		TestClass target = new TestClass( "test value" );

		FormViewElement form = new FormViewElement();
		form.setCommandAttribute( "item" );
		form.add( group );

		renderAndExpect(
				form,
				model -> {
					BindingResult errors = new BeanPropertyBindingResult( target, "item" );
					errors.rejectValue( "control", "broken", "broken" );

					model.addAttribute( BindingResult.MODEL_KEY_PREFIX + "item", errors );
					model.addAttribute( "item", target );
				},
				"<form role='form' method='post'>" +
						"<div class='form-group has-error'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"<div class='small text-danger'>broken</div>" +
						"</div>" +
						"</form>"
		);
	}

	@Test
	public void errorOnHorizontalForm() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/formObject" );

		group.setFormLayout( FormLayout.horizontal( 2 ) );

		renderAndExpect(
				container,
				model -> {
					TestClass target = new TestClass( "test value" );
					BindingResult errors = new BeanPropertyBindingResult( target, "item" );
					errors.rejectValue( "control", "broken", "broken" );

					model.addAttribute( BindingResult.MODEL_KEY_PREFIX + "item", errors );
					model.addAttribute( "item", target );
					model.addAttribute( "formGroup", group );
				},
				"<div class='form-group has-error'>" +
						"<label for='control' class='control-label col-md-2'>title</label>" +
						"<div class='col-md-10'>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"<div class='small text-danger'>broken</div>" +
						"</div>" +
						"</div>"
		);
	}

	public static class TestClass
	{
		private String control;

		public TestClass( String control ) {
			this.control = control;
		}

		public String getControl() {
			return control;
		}

		public void setControl( String control ) {
			this.control = control;
		}
	}
}
