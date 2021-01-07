package com.oest.usereucomb.providers.login.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PerfilFragment extends Fragment {
    private RelativeLayout rl;

    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/public/apiperfilupdate?";
    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/public/apiperfil?username=";
    private EditText email;
    private EditText password;
    private EditText name;
    private EditText app_name;
    private EditText apm_name;
    private EditText usernames;
    private ImageView image;
    private String usuario;

    private Button register;
    private RadioButton RDwoman;
    private RadioButton RDmen;

    private VolleyRP volley;
    private RequestQueue mRequest;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_registrar,null);
        setHasOptionsMenu(true);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        //String imamen = "perfil";
        //Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        email = (EditText) rl.findViewById(R.id.email);
        password = (EditText) rl.findViewById(R.id.password);
        name = (EditText) rl.findViewById(R.id.name);
        app_name = (EditText) rl.findViewById(R.id.appname);
        apm_name = (EditText) rl.findViewById(R.id.apmname);
        usernames = (EditText) rl.findViewById(R.id.numero);
        image = (ImageView) rl.findViewById(R.id.image);
        RDwoman = (RadioButton) rl.findViewById(R.id.mujer);
        RDmen = (RadioButton) rl.findViewById(R.id.hombre);
        register = (Button) rl.findViewById(R.id.user_sign_in_button);

        if(Preferences.obtenerPreferenceBoolean(getContext(),Preferences.PREFERENCE_ESTADO_BUTTON_SESION)) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Espere..."); // Setting Message
            progressDialog.setTitle("Puede tardar un momento"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

            SolicitudJSON(IP + usuario);
        }
        else{
            iniciarActividadSiguiente2();
        }


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
                VerificarRegistro();
            }
        });


        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void VerificarRegistro() {

        String username = usernames.getText().toString();
        String contra = password.getText().toString();
        //USER = name.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernames.setError(getString(R.string.error_field_required));
            usernames.requestFocus();
            return;
        } else if (TextUtils.isEmpty(contra)) {
            password.setError(getString(R.string.error_field_required));
            password.requestFocus();
            return;
        }

        else if (email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") && email.length() > 0) {
            String sex = "";

            if (RDmen.isChecked()) sex = "H";
            else if (RDwoman.isChecked()) sex = "M";

            registrarWebService(
                    getStringET(email).trim(),
                    getStringET(password).trim(),
                    getStringET(name).trim(),
                    getStringET(app_name).trim(),
                    getStringET(apm_name).trim(),
                    getStringET(usernames).trim(),
                    sex);
        } else {
            email.setError(getString(R.string.error_field_required));
            email.requestFocus();
            return;
        }

    }

    public void registrarWebService(String email, String password, String name, String app_name, String apm_name, String username, String sex) {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere..."); // Setting Message
        progressDialog.setTitle("Puede tardar un momento"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

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

                        if (estado.equalsIgnoreCase("La informacion se actualizo correctamente")) {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            String imamen = "La informacion se actualizo correctamente";
                            Mensajes.MyCustomAlertDialogSinEnviar(estado,imamen, getContext());
                            //iniciarActividadSiguiente();
                            progressDialog.dismiss();

                        } else {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(estado,imamen, getContext());
                            progressDialog.dismiss();

                        }
                    } catch (JSONException e) {
                        String mensa = "No se pudo registrar";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                    }
                    //Toast.makeText(RegistroActivity.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    String mensa = "No se pudo registrar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                }
            });
            VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);
        } else {
            String mensa = "Por favor rellene todos los campos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
        }
    }

    private String getStringET(EditText e){
        return e.getText().toString();
    }

    public void iniciarActividadSiguiente(){
        //Toast.makeText(getContext(), "Se actualizaron los datos", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(),MainActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(),LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    public void SolicitudJSON(String URL){

        JsonObjectRequest solicitud = new JsonObjectRequest(URL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                VerificarDatos(datos);

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest,getContext(), volley);
    }

    public void VerificarDatos(JSONObject datos) {

        try {

            String estado = datos.getString("resultado");
            //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();

            if(estado.equals("error")) {
                //Toast.makeText(getContext(), "El usuario no existe:", Toast.LENGTH_LONG).show();
                String mensa = "El usuario no existe";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                progressDialog.dismiss();

            }
            else{
                JSONObject Jsondatos = new JSONObject(datos.getString("resultado"));

                String app_nam = Jsondatos.getString("first_name");
                String apm_nam = Jsondatos.getString("second_name");
                String na = Jsondatos.getString("name");
                String correo = Jsondatos.getString("email");
                String username = Jsondatos.getString("username");
                String sexx = Jsondatos.getString("sex");

                usernames.setText(username);
                email.setText(correo);
                name.setText(na);
                app_name.setText(app_nam);
                apm_name.setText(apm_nam);

                if(sexx.equals("M")){
                    RDwoman.setChecked(true);
                    RDmen.setChecked(false);
                }
                else{
                    RDmen.setChecked(true);
                    RDwoman.setChecked(false);
                }

                final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/img/usuarioimg/" + username + ".jpg";
                Log.d("urlss", urlfinal);
                Glide.with(getContext()).load(urlfinal).into(image);
                progressDialog.dismiss();

            }
        } catch (JSONException e) {
            //Toast.makeText(getContext(), "Error"+e, Toast.LENGTH_LONG).show();
            String mensa = "Error";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
        }
    }



}

