package com.foreach.across.modules.user.business;

import com.foreach.across.modules.hibernate.types.BitFlag;

public enum UserRestriction implements BitFlag {
    DISABLED(1),
    EXPIRED(2),
    LOCKED(4),
    CREDENTIALS_EXPIRED(8),
	REQUIRES_CONFIRMATION(16);

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
