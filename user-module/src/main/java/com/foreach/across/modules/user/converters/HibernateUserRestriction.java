package com.foreach.across.modules.user.converters;

import com.foreach.across.modules.hibernate.types.HibernateBitFlag;
import com.foreach.across.modules.user.business.UserRestriction;

public class HibernateUserRestriction extends HibernateBitFlag
{
	public static final String CLASS_NAME = "com.foreach.across.modules.user.converters.HibernateUserRestriction";

	public HibernateUserRestriction() {
		super( UserRestriction.class );
	}
}
