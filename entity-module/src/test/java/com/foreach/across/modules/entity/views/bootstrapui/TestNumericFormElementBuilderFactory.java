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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactoryImpl;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElement;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElementConfiguration;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelpers;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.common.test.MockedLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(classes = TestNumericFormElementBuilderFactory.Config.class, loader = MockedLoader.class)
public class TestNumericFormElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<NumericFormElement>
{
	@Override
	protected Class getTestClass() {
		return NumericProperties.class;
	}

	@Test
	public void withoutAnnotations() {
		NumericFormElement numeric = assembleAndVerify( "withoutAnnotations", false );
		assertNull( numeric.getConfiguration() );
	}

	@Test
	public void required() {
		NumericFormElement numeric = assembleAndVerify( "required", true );
		assertNull( numeric.getConfiguration() );
	}

	@Test
	public void defaultDecimal() {
		NumericFormElement numeric = assembleAndVerify( "decimal", false );
		assertNull( numeric.getConfiguration() );
	}

	@Test
	public void currencyInteger() {
		when( properties.get( "withoutAnnotations" ).hasAttribute( Currency.class ) ).thenReturn( true );
		when( properties.get( "withoutAnnotations" ).getAttribute( Currency.class ) )
				.thenReturn( Currency.getInstance( "EUR" ) );

		NumericFormElement numeric = assembleAndVerify( "withoutAnnotations", false );

		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 0, configuration.get( "mDec" ) );
		assertEquals( Currency.getInstance( "EUR" ), configuration.getCurrency() );
	}

	@Test
	public void percentageLong() {
		when( properties.get( "required" ).hasAttribute( NumericFormElementConfiguration.Format.class ) )
				.thenReturn( true );
		when( properties.get( "required" ).getAttribute( NumericFormElementConfiguration.Format.class ) )
				.thenReturn( NumericFormElementConfiguration.Format.PERCENT );

		NumericFormElement numeric = assembleAndVerify( "required", true );

		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 0, configuration.get( "mDec" ) );
		assertEquals( NumericFormElementConfiguration.Format.PERCENT, configuration.getFormat() );
		assertEquals( 1, configuration.getMultiplier() );
	}

	@Test
	public void manualConfiguration() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setDecimalPositions( 6 );
		configuration.setLocalizeDecimalSymbols( false );

		when( properties.get( "decimal" ).hasAttribute( NumericFormElementConfiguration.class ) )
				.thenReturn( true );
		when( properties.get( "decimal" ).getAttribute( NumericFormElementConfiguration.class ) )
				.thenReturn( configuration );

		NumericFormElement numeric = assembleAndVerify( "decimal", false );
		assertEquals( configuration, numeric.getConfiguration() );
	}

	@Test
	public void currencyNumberFormat() {
		LocaleContextHolder.setLocale( Locale.forLanguageTag( "nl-BE" ) );

		NumericFormElement numeric = assembleAndVerify( "currency", false );
		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( Currency.getInstance( "EUR" ), configuration.getCurrency() );
	}

	@Test
	public void percentNumberFormat() {
		LocaleContextHolder.setLocale( Locale.forLanguageTag( "nl-BE" ) );

		NumericFormElement numeric = assembleAndVerify( "percent", false );
		NumericFormElementConfiguration configuration = numeric.getConfiguration();
		assertNotNull( configuration );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( NumericFormElementConfiguration.Format.PERCENT, configuration.getFormat() );
		assertEquals( 100, configuration.getMultiplier() );
	}

	@SuppressWarnings("unchecked")
	private <V> V assembleAndVerify( String propertyName, boolean required ) {
		NumericFormElement control = assemble( propertyName, ViewElementMode.CONTROL );
		assertEquals( propertyName, control.getName() );
		assertEquals( propertyName, control.getControlName() );
		assertFalse( control.isReadonly() );
		assertFalse( control.isDisabled() );
		assertEquals( required, control.isRequired() );

		return (V) control;
	}

	@SuppressWarnings("unused")
	private static class NumericProperties
	{
		public int withoutAnnotations;

		@NotNull
		public Long required;

		public BigDecimal decimal;

		@NumberFormat(style = NumberFormat.Style.CURRENCY)
		public BigDecimal currency;

		@NumberFormat(style = NumberFormat.Style.PERCENT)
		public BigDecimal percent;
	}

	@Configuration
	protected static class Config
	{
		@Bean
		@Primary
		public NumericFormElementBuilderFactory numericFormElementBuilderFactory() {
			return new NumericFormElementBuilderFactory();
		}

		@Bean
		public BootstrapUiFactory bootstrapUiFactory() {
			return new BootstrapUiFactoryImpl();
		}

		@Bean
		public EntityViewElementBuilderHelpers viewElementBuilderHelpers() {
			return new EntityViewElementBuilderHelpers();
		}
	}
}
