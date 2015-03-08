package com.foreach.across.modules.bootstrapui.testmodule.controllers;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.web.ui.ViewElement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Exposed
@Controller
public class RenderComponentController
{
	public static final String PATH = "/renderComponent";

	private ViewElement element;

	public void setElement( ViewElement element ) {
		this.element = element;
	}

	@RequestMapping(PATH)
	public String render( ModelMap model ) {
		model.put( "element", element );

		return "th/test/renderComponent";
	}
}
