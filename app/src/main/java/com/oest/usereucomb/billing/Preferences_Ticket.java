package com.oest.usereucomb.billing;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by OeSt on 03/08/2017.
 */

public class Preferences_Ticket {

    public static final String STRING_PREFERENCES_TICKET = "com.itm.oest.ticket";
    public static final String PREFERENCE_ESTADO_BUTTON_SESION_TICKET = "estado.button.sesion";
    public static final String PREFERENCE_TICKET = "usuario.ticket";

    public static void savePreferenceBoolean(Context c, boolean b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES_TICKET,c.MODE_PRIVATE);
        preferences.edit().putBoolean(key,b).apply();
    }

    public static void savePreferenceString(Context c, String b, String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES_TICKET,c.MODE_PRIVATE);
        preferences.edit().putString(key,b).apply();
    }

    public static boolean obtenerPreferenceBoolean(Context c,String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES_TICKET,c.MODE_PRIVATE);
        return preferences.getBoolean(key,false);//Si es que nunca se ha guardado nada en esta key pues retornara false
    }

    public static String obtenerPreferenceString(Context c,String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES_TICKET,c.MODE_PRIVATE);
        return preferences.getString(key,"");//Si es que nunca se ha guardado nada en esta key pues retornara una cadena vacia
    }

    public static boolean removePreferenceBoolean(Context c,String key){
        SharedPreferences preferences = c.getSharedPreferences(STRING_PREFERENCES_TICKET,c.MODE_PRIVATE);
        return preferences.edit().remove(key).commit();
    }
}
