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

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * @author Arne Vandamme
 */
public class TestDateTimeFormElement extends AbstractBootstrapViewElementTest
{
	private static final String DATA_ATTRIBUTE =
			"data-datetimepicker='{\"datepickerInput\":\"input[type=text]\"," +
					"\"format\":\"L LT\",\"extraFormats\":[\"YYYY-MM-DD HH:mm\",\"L\",\"YYYY-MM-DD\"]," +
					"\"locale\":\"en-GB\",\"exportFormat\":\"YYYY-MM-DD HH:mm\"}'";

	private DateTimeFormElement datetime;

	@Before
	public void before() {
		datetime = new DateTimeFormElement();
	}

	@Test
	public void emptyDateTime() {
		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input class='form-control' type='text' />" +
						"<span class='input-group-addon'>" +
						"<span aria-hidden='true' class='glyphicon glyphicon-calendar'></span>" +
						"</span>" +
						"<input type='hidden' />" +
						"</div>"
		);
	}

	@Test
	public void withDateAndControlName() throws ParseException {
		datetime.setRequired( true );
		datetime.setControlName( "birthday" );

		Date date = DateUtils.parseDate( "2015-08-07 10:31", "yyyy-MM-dd HH:mm" );
		datetime.setValue( date );

		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input class='form-control' type='text' id='_birthday' name='_birthday' required='required'" +
						" value='2015-08-07 10:31' />" +
						"<span class='input-group-addon'>" +
						"<span aria-hidden='true' class='glyphicon glyphicon-calendar'></span>" +
						"</span>" +
						"<input type='hidden' name='birthday' value='2015-08-07 10:31' />" +
						"</div>"
		);
	}

	@Test
	public void customIcon() {
		datetime.setAddonAfter( new GlyphIcon( GlyphIcon.TIME ) );

		renderAndExpect(
				datetime,
				"<div class='input-group js-form-datetimepicker date' " + DATA_ATTRIBUTE + ">" +
						"<input class='form-control' type='text' />" +
						"<span class='input-group-addon'>" +
						"<span aria-hidden='true' class='glyphicon glyphicon-time'></span>" +
						"</span>" +
						"<input type='hidden' />" +
						"</div>"
		);
	}
}
