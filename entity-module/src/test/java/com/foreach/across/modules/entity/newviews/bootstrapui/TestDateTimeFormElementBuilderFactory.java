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
package com.foreach.across.modules.entity.newviews.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration.Format;
import com.foreach.across.modules.entity.newviews.ViewElementMode;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.common.test.MockedLoader;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestDateTimeFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestDateTimeFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<DateTimeFormElement>
{
	@Override
	protected Class getTestClass() {
		return DateProperties.class;
	}

	@Test
	public void withoutAnnotations() {
		DateTimeFormElement datetime = assembleAndVerify( "withoutAnnotations", false );
		assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
		assertEquals( "en-GB", datetime.getConfiguration().get( "locale" ) );
		assertEquals( true, datetime.getConfiguration().get( "showClear" ) );
	}

	@Test
	public void required() {
		DateTimeFormElement datetime = assembleAndVerify( "required", true );
		assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
		assertEquals( false, datetime.getConfiguration().get( "showClear" ) );
	}

	@Test
	public void date() {
		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( Format.DATE, datetime.getConfiguration().getFormat() );
	}

	@Test
	public void timeWithPastAnnotation() {
		DateTimeFormElement datetime = assembleAndVerify( "timeWithPast", false );
		assertEquals( Format.TIME, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.TIME ) );
		assertNull( datetime.getConfiguration().get( "minDate" ) );
		assertNotNull( datetime.getConfiguration().get( "maxDate" ) );
	}

	@Test
	public void datetimeWithFutureAnnotation() {
		DateTimeFormElement datetime = assembleAndVerify( "datetimeWithFuture", false );
		assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.CALENDAR ) );
		assertNotNull( datetime.getConfiguration().get( "minDate" ) );
		assertNull( datetime.getConfiguration().get( "maxDate" ) );
	}

	@Test
	public void preferredFormatConfiguredStillHasAnnotationsProcessed() {
		when( properties.get( "timeWithPast" ).hasAttribute( Format.class ) ).thenReturn( true );
		when( properties.get( "timeWithPast" ).getAttribute( Format.class ) )
				.thenReturn( Format.DATE );

		DateTimeFormElement datetime = assembleAndVerify( "timeWithPast", false );
		assertEquals( Format.DATE, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.CALENDAR ) );
		assertNull( datetime.getConfiguration().get( "minDate" ) );
		assertNotNull( datetime.getConfiguration().get( "maxDate" ) );
	}

	@Test
	public void preferredConfigurationConfigured() {
		DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration();
		configuration.setFormat( Format.TIME );
		configuration.setShowClearButton( true );

		when( properties.get( "datetimeWithFuture" ).getAttribute( DateTimeFormElementConfiguration.class ) )
				.thenReturn( configuration );

		DateTimeFormElement datetime = assembleAndVerify( "datetimeWithFuture", false );
		assertEquals( Format.TIME, datetime.getConfiguration().getFormat() );
		assertTrue( datetime.getAddonAfter( GlyphIcon.class ).getGlyph().equals( GlyphIcon.TIME ) );
		assertNull( datetime.getConfiguration().get( "minDate" ) );
		assertNull( datetime.getConfiguration().get( "maxDate" ) );
		assertEquals( true, datetime.getConfiguration().get( "showClear" ) );
	}

	@Test
	public void valueSetFromEntity() throws ParseException {
		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );

		when( properties.get( "date" ).getValueFetcher() ).thenReturn( entity -> date );
		when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );

		DateTimeFormElement datetime = assembleAndVerify( "date", false );
		assertEquals( date, datetime.getValue() );
	}

	@Test
	public void customLocaleSpecified() {
		LocaleContextHolder.setLocale( Locale.FRANCE );

		try {
			DateTimeFormElement datetime = assembleAndVerify( "withoutAnnotations", false );
			assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
			assertEquals( "fr-FR", datetime.getConfiguration().get( "locale" ) );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		DateTimeFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( "entity." + propertyName, control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class DateProperties
	{
		public Date withoutAnnotations;

		@NotNull
		public Date required;

		@Future
		@Temporal(TemporalType.TIMESTAMP)
		public Date datetimeWithFuture;

		@Past
		@Temporal(TemporalType.TIME)
		public Date timeWithPast;

		@Temporal(TemporalType.DATE)
		public Date date;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public DateTimeFormElementBuilderFactory dateTimeFormElementBuilderFactory() {
			return new DateTimeFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}

		@Bean
		public ConversionService conversionService() {
			return mock( ConversionService.class );
		}
	}
}
