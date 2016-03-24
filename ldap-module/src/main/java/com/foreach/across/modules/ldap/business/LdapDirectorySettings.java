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

package com.foreach.across.modules.ldap.business;

import java.util.LinkedHashMap;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
public abstract class LdapDirectorySettings
{

	private LinkedHashMap<String, String> settings;

	public LinkedHashMap<String, String> getSettings() {
		return settings;
	}

	public void setSettings( LinkedHashMap<String, String> settings ) {
		this.settings = settings;
	}

	public abstract LdapConnectorType getConnectorType();
}
