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
package com.foreach.across.modules.oauth2.config;

import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.database.SchemaObject;

import java.util.Arrays;

public class OAuth2SchemaConfiguration
{
	public static final String TABLE_CLIENT = "oauth_client";
	public static final String TABLE_CLIENT_SCOPE = "oauth_client_scope";
	public static final String TABLE_SCOPE = "oauth_scope";
	public static final String TABLE_RESOURCEID = "oauth_resource_id";
	public static final String TABLE_GRANT_TYPE = "oauth_grant_type";
	public static final String TABLE_REDIRECT_URI = "oauth_redirect_uri";
}
