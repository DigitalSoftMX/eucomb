package com.oest.usereucomb.providers.login.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.Preferences_Puntos;
import com.oest.usereucomb.billing.Preferences_Ticket;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2018
 */
public class CerrarFragment extends Fragment {

    private RelativeLayout rl;
    private VolleyRP volley;
    private RequestQueue mRequest;
    private String usuario;
    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/public/apicerrar?username=";
    ProgressDialog progressDialog;

    ImageView ima;
    private static String CACHE_FILE = "menuCache.srl";
    private Activity context;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_cerrar,null);
        setHasOptionsMenu(true);

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        ima = (ImageView) rl.findViewById(R.id.cerrar);

        //iniciarActividadSiguiente();
        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);
        if(usuario == "") {
           iniciarActividadSiguiente2();
        }
        else {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Espere..."); // Setting Message
            progressDialog.setTitle("Puede tardar un momento"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

            registrarWebService(usuario);
            //deleteFile();


        }


        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void registrarWebService(String username) {

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", username);
        Log.d("cerrarusuario", username);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.GET, IP + username, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                    if(estado == "No mando ningun usuario"){
                        //iniciarActividadSiguiente2();
                        ima.setImageResource(R.drawable.denegado);
                        progressDialog.dismiss();
                    }
                    else {

                        Log.d("estado", estado);
                        Preferences.savePreferenceBoolean(getContext(), false, Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                        Preferences.removePreferenceBoolean(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

                        Preferences_Puntos.savePreferenceBoolean(getContext(), false, Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                        Preferences_Puntos.removePreferenceBoolean(getContext(), Preferences_Puntos.PREFERENCE_PUNTOS);

                        Preferences_Ticket.savePreferenceBoolean(getContext(), false, Preferences_Ticket.PREFERENCE_ESTADO_BUTTON_SESION_TICKET);
                        Preferences_Ticket.removePreferenceBoolean(getContext(), Preferences_Ticket.PREFERENCE_TICKET);

                        SQLiteDatabase sqLiteDatabase =
                                getContext().openOrCreateDatabase("historial.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
                        sqLiteDatabase.execSQL("drop table if exists historial");
                        sqLiteDatabase.close();


                        SQLiteDatabase sqLiteDatabase2 =
                                getContext().openOrCreateDatabase("movement.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
                        sqLiteDatabase2.execSQL("drop table if exists movement");
                        sqLiteDatabase2.close();

                        // Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                        iniciarActividadSiguiente();
                        progressDialog.dismiss();
                    }

                } catch (JSONException e) {
                    //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    String mensa = "Error";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                    ima.setImageResource(R.drawable.denegado);
                    progressDialog.dismiss();

                }
                //Toast.makeText(RegistroActivity.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                String mensa = "No puede cerrar sesion no tiene datos";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                ima.setImageResource(R.drawable.denegado);
                progressDialog.dismiss();

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);
    }



    public void iniciarActividadSiguiente(){
        Toast.makeText(getContext(), "Sesión Cerrada", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(),MainActivity.class);
        startActivity(i);
        Objects.requireNonNull(this.getActivity()).finish();
    }

    public void iniciarActividadSiguiente2(){
        Intent i = new Intent(getContext(),LoginActivity.class);
        startActivity(i);
        this.getActivity();
    }


    public void deleteFile() {

        File cacheFile = new File(new File(context.getCacheDir(),"")+ CACHE_FILE);
        Toast.makeText(getContext(), "archivo", Toast.LENGTH_LONG).show();

        if (cacheFile.exists()) {
            Toast.makeText(getContext(), "eliminado", Toast.LENGTH_LONG).show();

        }

    }
}

