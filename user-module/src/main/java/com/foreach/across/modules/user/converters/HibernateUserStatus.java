package com.foreach.across.modules.user.converters;

import com.foreach.across.modules.hibernate.types.HibernateBitFlag;
import com.foreach.across.modules.user.business.UserStatus;

public class HibernateUserStatus extends HibernateBitFlag {
    public static final String CLASS_NAME = "com.foreach.across.modules.user.converters.HibernateUserStatus";

    public HibernateUserStatus() {
        super( UserStatus.class );
    }
}
