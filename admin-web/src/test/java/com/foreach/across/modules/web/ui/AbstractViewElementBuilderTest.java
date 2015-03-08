package com.foreach.across.modules.web.ui;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public abstract class AbstractViewElementBuilderTest<T extends ViewElementBuilderSupport<U, T>, U extends ViewElement>
{
	protected T builder;
	protected U element;

	private ViewElementBuilderFactory builderFactory;

	@Before
	public void reset() {
		builderFactory = mock( ViewElementBuilderFactory.class );

		builder = createBuilder( builderFactory );
		element = null;
	}

	@Test
	public void commonProperties() {
		assertSame( builder, builder.name( "componentName" ).customTemplate( "custom/template" ) );

		build();

		assertEquals( "componentName", element.getName() );
		assertEquals( "custom/template", element.getCustomTemplate() );
	}

	@Test
	public void methodsShouldReturnBuilderInstance() throws Exception {
		Class<?> c = builder.getClass();

		Collection<String> methodExceptions = Arrays.asList( "build", "wait", "equals", "toString", "hashCode",
		                                                     "getClass", "notify", "notifyAll" );
		methodExceptions.addAll( nonBuilderReturningMethods() );

		for ( Method method : c.getMethods() ) {
			if ( !methodExceptions.contains( method.getName() ) ) {
				Method declared = c.getDeclaredMethod( method.getName(), method.getParameterTypes() );

				assertEquals( "Method [" + method + "] does not return same builder type",
				              c,
				              declared.getReturnType() );
			}
		}
	}

	protected abstract T createBuilder( ViewElementBuilderFactory builderFactory );

	protected Collection<String> nonBuilderReturningMethods() {
		return Collections.emptyList();
	}

	protected void build() {
		element = builder.build( null );
	}
}
