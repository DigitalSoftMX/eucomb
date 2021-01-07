package com.oest.usereucomb.billing;

import android.provider.BaseColumns;

public class RendimientoContract {

    public static abstract class RendimientoEntry implements BaseColumns {
        public static final String TABLE_NAME ="rendimiento";

        public static final String id = "id";
        public static final String kl = "kl";
        public static final String carga = "carga";
        public static final String fecha = "fecha";
        public static final String kilometros = "kilometros";
    }
}

