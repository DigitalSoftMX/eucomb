package com.oest.usereucomb.providers.login.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
import com.oest.usereucomb.billing.VolleyRP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2018
 */
public class LoginFragment extends Fragment {

    private RelativeLayout rl;

    private EditText eTusuario;
    private EditText eTcontraseña;
    private Button bTaceptar;
    private Button bTregistrar;
    private Button bTreset;
    private Button bTdudas;

    private VolleyRP volley;
    private RequestQueue mRequest;

    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/public/apilogin?";

    private String USER = "";
    private String PASS = "";
    ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_login_frame,null);
        setHasOptionsMenu(true);

        if(Preferences.obtenerPreferenceBoolean(getContext(),Preferences.PREFERENCE_ESTADO_BUTTON_SESION)) {
            iniciarActividadSiguiente();
        }

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        //View myFragmentView = inflater.inflate(R.layout.activity_login, container, false);

        eTusuario = (EditText) rl.findViewById(R.id.user);
        eTcontraseña = (EditText) rl.findViewById(R.id.password);
        bTaceptar = (Button) rl.findViewById(R.id.user_sign_in_button);
        bTregistrar = (Button) rl.findViewById(R.id.user_register_button);
        bTreset = (Button) rl.findViewById(R.id.olvidocontra);
        bTdudas = (Button) rl.findViewById(R.id.user_dudas_button);

        bTaceptar.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Espere..."); // Setting Message
                progressDialog.setTitle("Puede tardar un momento"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                VerificarLogin(eTusuario.getText().toString(), eTcontraseña.getText().toString());
            }

        });

        bTregistrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                iniciarActividadSiguiente2();
            }

        });

        bTreset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                iniciarActividadSiguiente3();
            }

        });

        bTdudas.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                iniciarActividadSiguiente4();
            }

        });

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void VerificarLogin(String user, String password){

        eTusuario.setError(null);


        String email = eTusuario.getText().toString();
        String contraseña = eTcontraseña.getText().toString();

        if(isEmpty(email)){
            eTusuario.setError(getString(R.string.error_field_required));
            eTusuario.requestFocus();
            return;
        }
        else if(isEmpty(contraseña)){
            eTcontraseña.setError(getString(R.string.error_field_required));
            eTcontraseña.requestFocus();
            return;
        }

        USER = user;
        PASS = password;
        registrarWebService(
                USER,
                PASS
                 );
    }

    public void registrarWebService(String username, String password) {

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", username);
        hashMapToken.put("password", password);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                   // Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                    if(estado.equals("No es un usuario valido")){
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "No es un usuario valido";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, getContext());

                    }
                    else if(estado.equals("El usuario o el password son incorrectos")){
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "El usuario o el password son incorrectos";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, getContext());

                    }
                    else if (estado.equals("La cuenta ya inicio sesion")) {
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "La cuenta ya inicio sesion";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                        progressDialog.dismiss();
                    }
                    else if(estado != ""){
                        //Toast.makeText(getContext(), "bienvenido", Toast.LENGTH_LONG).show();
                        //Preferences.savePreferenceBoolean(Login.this,true,Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                        //Preferences.savePreferenceString(Login.this,USER,Preferences.PREFERENCE_USUARIO_LOGIN);

                        Preferences.savePreferenceBoolean(getContext(), true, Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                        Preferences.savePreferenceString(getContext(), estado, Preferences.PREFERENCE_USUARIO_LOGIN);

                        iniciarActividadSiguiente();
                    }
                    else {
                        //Toast.makeText(getContext(), "No se pudo ingresar", Toast.LENGTH_LONG).show();
                        String mensa = "No se pudo ingresar";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, getContext());
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    String mensa = "No se pudo registrar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, getContext());

                }
                //Toast.makeText(RegistroActivity.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                String mensa = "No se pudo registrar";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, getContext());

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);
    }

    private String getStringET(EditText e){
        return e.getText().toString();
    }


    public void iniciarActividadSiguiente(){
        //Toast.makeText(getContext(), "Usted ya inició sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(),MainActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    public void iniciarActividadSiguiente2(){
        Intent i = new Intent(getContext(),RegistroActivity.class);
        startActivity(i);
        this.getActivity();
        //finish();
    }


    public void iniciarActividadSiguiente3() {
        Intent i = new Intent(getContext(), ResetActivity.class);
        startActivity(i);
        this.getActivity();
    }

    public void iniciarActividadSiguiente4() {
        Intent i = new Intent(getContext(), PreguntasFrecuentesActivity.class);
        startActivity(i);
        this.getActivity();
        //finish();
    }

}

