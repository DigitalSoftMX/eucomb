package com.oest.usereucomb.billing;

import android.provider.BaseColumns;

public class MovementContract {

    public static abstract class MovementEntry implements BaseColumns {
        public static final String TABLE_NAME ="movement";

        public static final String id = "id";
        public static final String qr_membership = "qr_membership";
        public static final String qr_dispatcher = "qr_dispatcher";
        public static final String qr_ticket = "qr_ticket";
    }
}
