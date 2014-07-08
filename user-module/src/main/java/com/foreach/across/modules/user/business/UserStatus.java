package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.types.BitFlag;

import java.util.EnumSet;
import java.util.Set;


public enum UserStatus implements BitFlag {
    ACTIVE(1),
    NON_EXPIRED(2),
    NON_LOCKED(4),
    CREDENTIALS_NON_EXPIRED(8);

    public static final Set<UserStatus> DEFAULT_USER_STATUS = EnumSet.of( ACTIVE, NON_EXPIRED, NON_LOCKED, CREDENTIALS_NON_EXPIRED );
    private int bit;

    public int getBitFlag() {
        return bit;
    }

    public String getName() {
        return name();
    }

    UserStatus( int bit ) {
        this.bit = bit;
    }


}
