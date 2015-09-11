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
package com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration.Format;
import com.foreach.across.modules.bootstrapui.elements.builder.DateTimeFormElementBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactoryHelper;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.common.test.MockedLoader;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.Printer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestDateTimeFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestDateTimeFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<ViewElement>
{
	private static final Date PRINT_DATE;

	static {
		try {
			PRINT_DATE = DateUtils.parseDate( "2015-08-07 10:31:22", "yyyy-MM-dd HH:mm:ss" );
		}
		catch ( ParseException pe ) {
			throw new RuntimeException( pe );
		}
	}

	@Autowired
	private ConversionService mvcConversionService;

	@Autowired
	private EntityViewElementBuilderService entityViewElementBuilderService;

	@Override
	protected Class getTestClass() {
		return DateProperties.class;
	}

	@Test
	public void withoutAnnotations() {
		LocaleContextHolder.setLocale( Locale.UK );

		try {
			DateTimeFormElement datetime = assembleAndVerify( "withoutAnnotations", false );
			assertEquals( Format.DATETIME, datetime.getConfiguration().getFormat() );
			assertEquals( "en-GB", datetime.getConfiguration().get( "locale" ) );
			assertEquals( true, datetime.getConfiguration().get( "showClear" ) );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
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

	@Test
	public void valueOrderIsPrinterFormatDateTimeConfigurationAndConversionService() {
		LocaleContextHolder.setLocale( Locale.UK );

		try {
			ValueFetcher valueFetcher = mock( ValueFetcher.class );
			when( builderContext.getAttribute( EntityView.ATTRIBUTE_ENTITY ) ).thenReturn( "entity" );
			when( valueFetcher.getValue( any() ) ).thenReturn( PRINT_DATE );
			when( properties.get( "required" ).getValueFetcher() ).thenReturn( valueFetcher );

			TextViewElement text = assembleValue( "required" );
			assertEquals( "07-Aug-2015 10:31", text.getText() );

			DateTimeFormElementConfiguration configuration = new DateTimeFormElementConfiguration( Format.DATE );
			when( properties.get( "required" ).hasAttribute( DateTimeFormElementConfiguration.class ) )
					.thenReturn( true );
			when( properties.get( "required" ).getAttribute( DateTimeFormElementConfiguration.class ) )
					.thenReturn( configuration );
			assertEquals( "07-Aug-2015", assembleValue( "required" ).getText() );

			DateTimeFormElementConfiguration timeConfiguration = new DateTimeFormElementConfiguration( Format.TIME );
			DateTimeFormElementBuilder builder = new DateTimeFormElementBuilder().configuration( timeConfiguration );
			when( entityViewElementBuilderService.getElementBuilder(
					properties.get( "required" ), ViewElementMode.CONTROL ) ).thenReturn( builder );
			assertEquals( "10:31", assembleValue( "required" ).getText() );

			java.text.Format format = new MessageFormat( "messageFormat" );
			when( properties.get( "required" ).hasAttribute( java.text.Format.class ) ).thenReturn( true );
			when( properties.get( "required" ).getAttribute( java.text.Format.class ) ).thenReturn( format );
			assertEquals( "messageFormat", assembleValue( "required" ).getText() );

			Printer printer = mock( Printer.class );
			when( printer.print( any(), any() ) ).thenReturn( "printer" );
			when( properties.get( "required" ).hasAttribute( Printer.class ) ).thenReturn( true );
			when( properties.get( "required" ).getAttribute( Printer.class ) ).thenReturn( printer );
			assertEquals( "printer", assembleValue( "required" ).getText() );
		}
		finally {
			LocaleContextHolder.resetLocaleContext();
		}
	}

	private TextViewElement assembleValue( String propertyName ) {
		return (TextViewElement) assemble( propertyName, ViewElementMode.VALUE );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		DateTimeFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
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

		@Bean
		public EntityViewElementBuilderFactoryHelper builderFactoryHelper() {
			return new EntityViewElementBuilderFactoryHelper();
		}
	}
}
