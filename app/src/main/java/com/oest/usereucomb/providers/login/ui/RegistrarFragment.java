package com.oest.usereucomb.providers.login.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.oest.usereucomb.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2018
 */
public class RegistrarFragment extends Fragment {
    private RelativeLayout rl;

    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/public/apiregistrar?";
    private EditText email;
    private EditText password;
    private EditText name;
    private EditText app_name;
    private EditText apm_name;
    private EditText usernames;
    EditText etPlannedDate;

    private Button register;
    private RadioButton RDwoman;
    private RadioButton RDmen;

    private RadioButton aceptarSi;
    private RadioButton aceptarNo;
    private RadioButton condiciones;

    private VolleyRP volley;
    private RequestQueue mRequest;

    private String USER = "";
    ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_registrar,null);
        setHasOptionsMenu(true);

        if(Preferences.obtenerPreferenceBoolean(getContext(),Preferences.PREFERENCE_ESTADO_BUTTON_SESION)) {
            iniciarActividadSiguiente();
        }

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        email = (EditText) rl.findViewById(R.id.email);
        password = (EditText) rl.findViewById(R.id.password);
        name = (EditText) rl.findViewById(R.id.name);
        app_name = (EditText) rl.findViewById(R.id.appname);
        apm_name = (EditText) rl.findViewById(R.id.apmname);
        usernames = (EditText) rl.findViewById(R.id.numero);

        RDwoman = (RadioButton) rl.findViewById(R.id.mujer);
        RDmen = (RadioButton) rl.findViewById(R.id.hombre);

        //aceptarSi = (RadioButton) rl.findViewById(R.id.sitengo);
        //aceptarNo = (RadioButton) rl.findViewById(R.id.notengo);

        register = (Button) rl.findViewById(R.id.user_sign_in_button);

        RDmen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RDwoman.setChecked(false);
            }
        });
        RDwoman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RDmen.setChecked(false);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Espere..."); // Setting Message
                progressDialog.setTitle("Puede tardar un momento"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                VerificarRegistro();
            }
        });

        etPlannedDate.setOnClickListener((View.OnClickListener) this);

        return rl;
    }

    public void VerificarRegistro() {

        String emails = email.getText().toString();
        String contraseña = password.getText().toString();
        String cond = condiciones.getText().toString();
        String acepto = usernames.getText().toString();

            if (TextUtils.isEmpty(emails)) {
            email.setError(getString(R.string.error_field_required));
            email.requestFocus();
            return;
        }
            if (TextUtils.isEmpty(contraseña)) {
                password.setError(getString(R.string.error_field_required));
                password.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(cond)) {
                condiciones.setError(getString(R.string.error_field_required));
                condiciones.requestFocus();
                return;
            }

            if (aceptarSi.isChecked()) {

                if (TextUtils.isEmpty(acepto)) {
                    usernames.setError(getString(R.string.error_field_required));
                    usernames.requestFocus();
                    return;
                }
            }

            //correo electronico validacion
            String sex = "";

            if (RDmen.isChecked()) sex = "H";
            else if (RDwoman.isChecked()) sex = "M";

            Log.d("fecha", String.valueOf(etPlannedDate));

            registrarWebService(
                    getStringET(email).trim(),
                    getStringET(password).trim(),
                    getStringET(name).trim(),
                    getStringET(app_name).trim(),
                    getStringET(apm_name).trim(),
                    getStringET(usernames).trim(),
                    sex);
    }

    public void registrarWebService(String email, String password, String name, String app_name, String apm_name, String username, String sex) {

        if (!email.isEmpty()) {

            // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMapToken = new HashMap<>();
            hashMapToken.put("email", email);
            hashMapToken.put("password", password);
            hashMapToken.put("name", name);
            hashMapToken.put("first_name", app_name);
            hashMapToken.put("second_name", apm_name);
            hashMapToken.put("username", username);
            hashMapToken.put("sex", sex);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP_Registrar, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject datos) {
                    try {
                        String estado = datos.getString("resultado");

                        if(estado.equals("La membresia ya esta ocupada")) {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                            String mensa = "La membresía ya esta ocupada";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

                        }
                        else if(estado.equals("La membresia no existe")) {
                           // Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                            String mensa = "La membresía no existe";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                        }
                        else if (estado != "") {
                           //  Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            String imamen = "activo";
                            Mensajes.MyCustomAlertDialogSinEnviar(estado, imamen, getContext());
                            Preferences.savePreferenceBoolean(getContext(),true,Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                            Preferences.savePreferenceString(getContext(), USER,Preferences.PREFERENCE_USUARIO_LOGIN);

                            iniciarActividadSiguiente();
                            } else {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(estado, imamen, getContext());

                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        //Toast.makeText(getContext(), "No se pudo registrarse", Toast.LENGTH_LONG).show();
                        String mensa = "No se pudo registrar";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                        progressDialog.dismiss();
                    }
                    //Toast.makeText(RegistroActivity.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    String mensa = "No se pudo registrar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                }
            });
            VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);
        } else {
            String mensa = "Por favor rellene todos los  campos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, getContext());
        }
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



}

