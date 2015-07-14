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
package com.foreach.across.modules.entity.testmodules.solr.business;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;

import java.util.List;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class Product
{
	@Id
	@Field
	private String simpleProperty;

	@Field("somePropertyName")
	private String namedPropery;

	@Field
	private List<String> listOfValues;

	@Indexed(readonly = true)
	@Field("property_*")
	private List<String> ignoredFromWriting;

	@Field("mappedField_*")
	private Map<String, List<String>> mappedFieldValues;
}
