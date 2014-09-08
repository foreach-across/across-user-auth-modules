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
package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.types.BitFlag;

public enum UserRestriction implements BitFlag
{
	DISABLED( 1 ),
	EXPIRED( 2 ),
	LOCKED( 4 ),
	CREDENTIALS_EXPIRED( 8 ),
	REQUIRES_CONFIRMATION( 16 );

	private int bit;

	public int getBitFlag() {
		return bit;
	}

	public String getName() {
		return name();
	}

	UserRestriction( int bit ) {
		this.bit = bit;
	}
}
