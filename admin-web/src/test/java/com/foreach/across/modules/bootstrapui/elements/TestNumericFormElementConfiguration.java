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

import org.junit.Test;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestNumericFormElementConfiguration
{
	@Test
	public void disableGroupingIsNeverRemoved() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setGroupingSeparator( null );

		assertEquals( "", configuration.get( "aSep" ) );
		assertEquals( "", configuration.localize( Locale.US ).get( "aSep" ) );

		NumberFormat format = configuration.localize( Locale.forLanguageTag( "nl-BE" ) ).createNumberFormat();
		assertNotNull( format );
		assertEquals( "123569,11", format.format( new BigDecimal( "123569.1111" ) ) );
	}

	@Test
	public void percentage() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration(
				NumericFormElementConfiguration.Format.PERCENT
		);

		assertEquals( 5, configuration.size() );
		assertEquals( Long.MIN_VALUE, configuration.get( "vMin" ) );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( "S", configuration.get( "mRound" ) );
		assertEquals( 's', configuration.get( "pSign" ) );
		assertEquals( " %", configuration.get( "aSign" ) );

		configuration.setDecimalPositions( 0 );
		assertEquals( 0, configuration.get( "mDec" ) );
		assertEquals( 1, configuration.getMultiplier() );

		NumberFormat format = configuration.createNumberFormat();
		assertEquals( "81 %", format.format( new BigDecimal( "80.52" ) ) );

		configuration.setMultiplier( 100 );
		format = configuration.createNumberFormat();
		assertEquals( "81 %", format.format( new BigDecimal( "0.8052" ) ) );
	}

	@Test
	public void euroInFront() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration(
				Currency.getInstance( "EUR" )
		);

		assertEquals( 6, configuration.size() );
		assertEquals( Long.MIN_VALUE, configuration.get( "vMin" ) );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( "B", configuration.get( "mRound" ) );
		assertEquals( 'p', configuration.get( "pSign" ) );
		assertEquals( "(,)", configuration.get( "nBracket" ) );
		assertEquals( Currency.getInstance( "EUR" ).getSymbol(), configuration.get( "aSign" ) );

		NumberFormat format = configuration.localize( Locale.US ).createNumberFormat();
		assertEquals( "EUR8,000.52", format.format( new BigDecimal( "8000.52" ) ) );
		assertEquals( "(EUR8,000.52)", format.format( new BigDecimal( "-8000.52" ) ) );
	}

	@Test
	public void euroInBack() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration(
				Locale.forLanguageTag( "nl-BE" )
		);

		assertEquals( 6, configuration.size() );
		assertEquals( Long.MIN_VALUE, configuration.get( "vMin" ) );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( "B", configuration.get( "mRound" ) );
		assertEquals( 's', configuration.get( "pSign" ) );
		assertEquals( "(,)", configuration.get( "nBracket" ) );
		assertEquals( " €", configuration.get( "aSign" )
		);

		NumberFormat format = configuration.localize( Locale.forLanguageTag( "nl-BE" ) ).createNumberFormat();
		assertEquals( "8.000,00 €", format.format( new BigDecimal( "8000" ) ) );
		assertEquals( "(8.000,56 €)", format.format( new BigDecimal( "-8000.555" ) ) );
	}

	@Test
	public void dollar() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration( Locale.US );

		assertEquals( 6, configuration.size() );
		assertEquals( Long.MIN_VALUE, configuration.get( "vMin" ) );
		assertEquals( 2, configuration.get( "mDec" ) );
		assertEquals( "B", configuration.get( "mRound" ) );
		assertEquals( 'p', configuration.get( "pSign" ) );
		assertEquals( "(,)", configuration.get( "nBracket" ) );
		assertEquals( "$", configuration.get( "aSign" ) );

		NumberFormat format = configuration.localize( Locale.US ).createNumberFormat();
		assertEquals( "$8,000.52", format.format( new BigDecimal( "8000.52" ) ) );
		assertEquals( "($8,000.52)", format.format( new BigDecimal( "-8000.52" ) ) );
	}

	@Test
	public void localizingDecimalOnly() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		assertEquals( 2, configuration.size() );

		NumericFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 4, localized.size() );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );

		NumberFormat format = localized.createNumberFormat();
		assertEquals( "8.000,52", format.format( new BigDecimal( "8000.52" ) ) );

		localized = localized.localize( Locale.US );
		assertEquals( 4, localized.size() );
		assertEquals( '.', localized.get( "aDec" ) );
		assertEquals( ',', localized.get( "aSep" ) );

		format = localized.createNumberFormat();
		assertEquals( "8,000.52", format.format( new BigDecimal( "8000.52" ) ) );

		assertEquals( 2, configuration.size() );

		configuration.setLocalizeDecimalSymbols( false );
		localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 2, localized.size() );
	}

	@Test
	public void localizingCurrencyItself() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration(
				NumericFormElementConfiguration.Format.CURRENCY
		);
		assertEquals( 5, configuration.size() );
		assertNull( configuration.get( "aSign" ) );

		NumericFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 8, localized.size() );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 's', localized.get( "pSign" ) );
		assertEquals( " €", localized.get( "aSign" ) );

		NumberFormat format = localized.createNumberFormat();
		assertEquals( "8.000,52 €", format.format( new BigDecimal( "8000.52" ) ) );

		localized = configuration.localize( Locale.US );
		assertEquals( 8, localized.size() );
		assertEquals( '.', localized.get( "aDec" ) );
		assertEquals( ',', localized.get( "aSep" ) );
		assertEquals( 'p', localized.get( "pSign" ) );
		assertEquals( "$", localized.get( "aSign" ) );

		format = localized.createNumberFormat();
		assertEquals( "$8,000.52", format.format( new BigDecimal( "8000.52" ) ) );
	}

	@Test
	public void localizingCurrencyFormat() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration(
				Currency.getInstance( "USD" )
		);
		assertEquals( 6, configuration.size() );

		NumericFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 8, localized.size() );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 's', localized.get( "pSign" ) );
		assertEquals( " USD", localized.get( "aSign" ) );

		NumberFormat format = localized.createNumberFormat();
		assertEquals( "8.000,52 USD", format.format( new BigDecimal( "8000.52" ) ) );

		localized.setLocalizeDecimalSymbols( false );
		localized = localized.localize( Locale.US );
		assertEquals( 8, localized.size() );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 'p', localized.get( "pSign" ) );
		assertEquals( "$", localized.get( "aSign" ) );

		format = localized.createNumberFormat();
		assertEquals( "$8.000,52", format.format( new BigDecimal( "8000.52" ) ) );

		configuration.setLocalizeDecimalSymbols( false );
		configuration.setLocalizeOutputFormat( false );
		localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 6, localized.size() );
	}

	@Test
	public void localizingPercentFormat() {
		NumericFormElementConfiguration configuration = new NumericFormElementConfiguration();
		configuration.setFormat( NumericFormElementConfiguration.Format.PERCENT );
		assertEquals( 5, configuration.size() );

		NumericFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 7, localized.size() );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 's', localized.get( "pSign" ) );
		assertEquals( "%", localized.get( "aSign" ) );

		NumberFormat format = localized.createNumberFormat();
		assertEquals( "123,05%", format.format( new BigDecimal( "123.05" ) ) );

		localized.setLocalizeDecimalSymbols( false );
		localized = localized.localize( Locale.US );
		assertEquals( 7, localized.size() );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 's', localized.get( "pSign" ) );
		assertEquals( "%", localized.get( "aSign" ) );

		format = localized.createNumberFormat();
		assertEquals( "123,05%", format.format( new BigDecimal( "123.05" ) ) );

		assertEquals( 5, configuration.size() );

		configuration.setLocalizeDecimalSymbols( false );
		configuration.setLocalizeOutputFormat( false );
		localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 5, localized.size() );
	}

	@Test
	public void quickCreatePercent() {
		NumericFormElementConfiguration configuration = NumericFormElementConfiguration.percent( 4, true );

		NumericFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 4, localized.get( "mDec" ) );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 's', localized.get( "pSign" ) );
		assertEquals( " %", localized.get( "aSign" ) );

		NumberFormat format = localized.createNumberFormat();
		assertEquals( "123,0500 %", format.format( new BigDecimal( "123.05" ) ) );
	}

	@Test
	public void quickCreateCurrency() {
		NumericFormElementConfiguration configuration = NumericFormElementConfiguration.currency(
				Currency.getInstance( "USD" ), 0, true
		);

		NumericFormElementConfiguration localized = configuration.localize( Locale.forLanguageTag( "nl-BE" ) );
		assertEquals( 0, localized.get( "mDec" ) );
		assertEquals( ',', localized.get( "aDec" ) );
		assertEquals( '.', localized.get( "aSep" ) );
		assertEquals( 's', localized.get( "pSign" ) );
		assertEquals( " USD", localized.get( "aSign" ) );

		NumberFormat format = localized.createNumberFormat();
		assertEquals( "123 USD", format.format( new BigDecimal( "123.05" ) ) );

		localized = configuration.localize( Locale.US );
		assertEquals( 0, localized.get( "mDec" ) );
		assertEquals( '.', localized.get( "aDec" ) );
		assertEquals( ',', localized.get( "aSep" ) );
		assertEquals( 'p', localized.get( "pSign" ) );
		assertEquals( "$ ", localized.get( "aSign" ) );

		format = localized.createNumberFormat();
		assertEquals( "$ 123", format.format( new BigDecimal( "123.05" ) ) );
	}

	@Test
	public void customFormat() {
		NumericFormElementConfiguration speedLimit = new NumericFormElementConfiguration();
		speedLimit.setGroupingSeparator( null );
		speedLimit.setDecimalPositions( 0 );
		speedLimit.setMinValue( 0 );
		speedLimit.setSign( " km/h" );
		speedLimit.setSignPositionRight( true );

		NumberFormat format = speedLimit.createNumberFormat();
		assertEquals( "1253 km/h", format.format( new BigDecimal( "1253.05" ) ) );
		assertEquals( "-50 km/h", format.format( new BigDecimal( "-50" ) ) );
	}
}
