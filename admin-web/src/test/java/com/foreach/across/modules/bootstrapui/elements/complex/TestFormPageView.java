package com.foreach.across.modules.bootstrapui.elements.complex;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextImpl;
import org.junit.Test;

public class TestFormPageView extends AbstractBootstrapViewElementTest
{
	@Test
	public void buildFormManually() {
		FormViewElement form = new FormViewElement();

		FormGroupElement nameGroup = new FormGroupElement();
		LabelFormElement nameLabel = new LabelFormElement();
		nameLabel.setText( "Name *" );
		TextboxFormElement name = new TextboxFormElement();
		name.setText( "John Doe" );
		nameGroup.setLabel( nameLabel );
		nameGroup.setControl( name );

		form.add( nameGroup );

		FormGroupElement buttons = new FormGroupElement();

		ButtonViewElement save = new ButtonViewElement();
		save.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		save.setStyle( Style.Button.PRIMARY );
		save.setText( "Save" );
		buttons.add( save );

		ButtonViewElement cancel = new ButtonViewElement();
		cancel.setType( ButtonViewElement.Type.LINK );
		cancel.setStyle( Style.Button.LINK );
		cancel.setText( "Cancel" );
		cancel.setUrl( "/goback" );
		buttons.add( cancel );

		form.add( buttons );

		verify( form );
	}

	@Test
	public void buildFormThroughBuilders() {
		BootstrapUiFactory bootstrapUi = new BootstrapUiFactoryImpl();

		FormViewElementBuilder formBuilder = bootstrapUi
				.form()
				.add(
						bootstrapUi.formGroup(
								bootstrapUi.label( "Name *" ),
								bootstrapUi.textbox().text( "John Doe" )
						)
				)
				.add(
						bootstrapUi.formGroup()
						           .add(
								           bootstrapUi.button().submit().style( Style.Button.PRIMARY ).text( "Save" ),
								           bootstrapUi.button().link( "/goback" ).text( "Cancel" )
						           )
				);

		verify( formBuilder.build( new ViewElementBuilderContextImpl() ) );
	}

	private void verify( ViewElement element ) {
		renderAndExpect(
				element,
				"<form role='form' method='post'>" +
						"<div class='form-group'>" +
						"<label class='control-label'>Name *</label>" +
						"<input type='text' class='form-control' value='John Doe' />" +
						"</div>" +
						"<div class='form-group'>" +
						"<button type='submit' class='btn btn-primary'>Save</button>" +
						"<a role='button' class='btn btn-link' href='/goback'>Cancel</a>" +
						"</div>" +
						"</form>"
		);
	}
}
