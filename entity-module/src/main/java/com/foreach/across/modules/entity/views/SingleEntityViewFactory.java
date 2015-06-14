package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.newviews.EntityViewElementBuilderContext;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElements;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import org.springframework.ui.ModelMap;

import java.util.Collection;
import java.util.List;

/**
 * Simple implementation of {@link ConfigurablePropertiesEntityViewFactorySupport} that builds the view elements
 * in the mode specified, for the properties configured.
 */
public class SingleEntityViewFactory extends ConfigurablePropertiesEntityViewFactorySupport
{
	private com.foreach.across.modules.entity.views.elements.ViewElementMode mode =
			com.foreach.across.modules.entity.views.elements.ViewElementMode.FOR_READING;

	private ViewElementMode viewElementMode = ViewElementMode.FORM_READ;

	public ViewElementMode getViewElementMode() {
		return viewElementMode;
	}

	/**
	 * Set the mode for which the {@link com.foreach.across.modules.web.ui.ViewElement}s should be created.
	 * Defaults to {@link com.foreach.across.modules.entity.newviews.ViewElementMode#FORM_READ}.
	 *
	 * @param viewElementMode to generate controls for
	 */
	public void setViewElementMode( com.foreach.across.modules.entity.newviews.ViewElementMode viewElementMode ) {
		this.viewElementMode = viewElementMode;
	}

	@Deprecated
	@Override
	public com.foreach.across.modules.entity.views.elements.ViewElementMode getMode() {
		return mode;
	}

	@Deprecated
	public void setMode( com.foreach.across.modules.entity.views.elements.ViewElementMode mode ) {
		this.mode = mode;
	}

	@Override
	@Deprecated
	protected void extendViewModel( ViewCreationContext viewCreationContext, EntityView view ) {

	}

	@Override
	protected EntityView createEntityView( ModelMap model ) {
		return new EntityView( model );
	}

	@Override
	protected ViewElements buildViewElements(
			ViewCreationContext viewCreationContext,
			EntityViewElementBuilderContext viewElementBuilderContext,
			EntityMessageCodeResolver messageCodeResolver
	) {
		EntityConfiguration entityConfiguration = viewCreationContext.getEntityConfiguration();
		List<EntityPropertyDescriptor> descriptors = getPropertyDescriptors( entityConfiguration );

		ContainerViewElementBuilder container = bootstrapUi.container();
		Collection<ViewElementBuilder> builders =
				getViewElementBuilders( entityConfiguration, descriptors, viewElementMode );

		for ( ViewElementBuilder builder : builders ) {
			if ( builder != null ) {
				container.add( builder );
			}
		}

		return container.build( viewElementBuilderContext );
	}
}
