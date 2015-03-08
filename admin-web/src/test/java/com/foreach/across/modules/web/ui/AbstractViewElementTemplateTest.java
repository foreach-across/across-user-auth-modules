package com.foreach.across.modules.web.ui;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.bootstrapui.testmodule.WebTestModule;
import com.foreach.across.modules.bootstrapui.testmodule.controllers.RenderComponentController;
import com.foreach.across.test.AcrossTestWebConfiguration;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@WebAppConfiguration
@ContextConfiguration(classes = AbstractViewElementTemplateTest.Config.class)
public abstract class AbstractViewElementTemplateTest
{
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private RenderComponentController renderController;

	private MockMvc mockMvc;

	@Before
	public void initMvc() {
		mockMvc = MockMvcBuilders.webAppContextSetup( wac ).build();
	}

	protected void renderAndExpect( ViewElement viewElement, final String expectedContent ) {
		renderController.setElement( viewElement );

		String xml = "<?xml version=\"1.0\"?><root>" + expectedContent + "</root>";

		try {
			mockMvc.perform( get( RenderComponentController.PATH ) )
			       .andExpect( status().isOk() )
			       .andExpect( content().xml( xml ) );

		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
	}

	@Configuration
	@AcrossTestWebConfiguration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
			context.addModule( new WebTestModule() );
		}
	}
}
