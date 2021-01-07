package com.oest.usereucomb.providers.login.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.oest.usereucomb.util.ThemeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {


    private EditText eTusuario;
    private EditText eTcontraseña;
    private Button bTaceptar;
    private Button bTregistrar;
    private Button bTreset;
    private Button bTdudas;
    ProgressDialog progressDialog;

    private VolleyRP volley;
    private RequestQueue mRequest;

    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/public/apilogin?";

    private String USER = "";
    private String PASS = "";

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_login_frame);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Valero");

        //checkPermission()
        boolean login = Preferences.obtenerPreferenceBoolean(this, Preferences.PREFERENCE_ESTADO_BUTTON_SESION);

        Log.d("infor", String.valueOf(login));
        /*if(!usuario) {
            iniciarActividadSiguiente3();
        }
        else*/

        if(Preferences.obtenerPreferenceBoolean(getBaseContext(),Preferences.PREFERENCE_ESTADO_BUTTON_SESION)) {
            iniciarActividadSiguiente();
        }
        else{
            Preferences.removePreferenceBoolean(getBaseContext(), Preferences.PREFERENCE_USUARIO_LOGIN);
        }

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        //View myFragmentView = inflater.inflate(R.layout.activity_login, container, false);

        eTusuario = (EditText) findViewById(R.id.user);
        eTcontraseña = (EditText) findViewById(R.id.password);
        bTaceptar = (Button) findViewById(R.id.user_sign_in_button);
        bTregistrar = (Button) findViewById(R.id.user_register_button);
        bTreset = (Button) findViewById(R.id.olvidocontra);
        bTdudas = (Button) findViewById(R.id.user_dudas_button);


        bTaceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
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
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eliminar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                // Log.i("ActionBar", "Atrás!");
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void VerificarLogin(String user, String password) {

        eTusuario.setError(null);


        String email = eTusuario.getText().toString();
        String contraseña = eTcontraseña.getText().toString();

        if (isEmpty(email)) {
            eTusuario.setError(getString(R.string.error_field_required));
            eTusuario.requestFocus();
            return;
        } else if (isEmpty(contraseña)) {
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

        progressDialog = ProgressDialog.show(this, "",
                "Espere ............", true);

        //Toast.makeText(LoginActivity.this, username, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", username);
        hashMapToken.put("password", password);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                    // Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                    if (estado.equals("No es un usuario valido")) {
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "No es un usuario valido ya estan usando esta cuenta comuniquese con el proveedor";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
                        progressDialog.dismiss();
                    } else if (estado.equals("El usuario o el password son incorrectos")) {
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "El usuario o el password son incorrectos";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
                        progressDialog.dismiss();
                    } else if (estado.equals("Otro usuario ya tiene este mismo correo electronico")) {
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "Otro usuario ya tiene este mismo correo electronico";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
                        progressDialog.dismiss();

                    } else if (estado.equals("La cuenta ya inicio sesion")) {
                    //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                    String mensa = "La cuenta ya inicio sesion";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
                    progressDialog.dismiss();
                    } else if (!estado.equals("")) {
                        //Toast.makeText(getContext(), "bienvenido", Toast.LENGTH_LONG).show();
                        //Preferences.savePreferenceBoolean(Login.this,true,Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                        //Preferences.savePreferenceString(Login.this,USER,Preferences.PREFERENCE_USUARIO_LOGIN);

                        Preferences.savePreferenceBoolean(LoginActivity.this, true, Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                        Preferences.savePreferenceString(LoginActivity.this, estado, Preferences.PREFERENCE_USUARIO_LOGIN);
                        //Preferences_Puntos.savePreferenceBoolean(LoginActivity.this, true, Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                        //Preferences_Puntos.savePreferenceString(LoginActivity.this, estado, Preferences_Puntos.PREFERENCE_PUNTOS);
                        progressDialog.dismiss();
                        iniciarActividadSiguiente();

                    } else {
                        //Toast.makeText(getContext(), "No se pudo ingresar", Toast.LENGTH_LONG).show();
                        String mensa = "No se pudo ingresar";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    String mensa = "No se pudo registrar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
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
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, LoginActivity.this);
                //Toast.makeText(LoginActivity.this, "puta madre", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, LoginActivity.this, volley);
    }

    private String getStringET(EditText e) {
        return e.getText().toString();
    }


    public void iniciarActividadSiguiente() {
        //Toast.makeText(getBaseContext(), "Usted ya inició sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void iniciarActividadSiguiente2() {
        Intent i = new Intent(LoginActivity.this, RegistroActivity.class);
        startActivity(i);
        finish();
    }

    public void iniciarActividadSiguiente3() {
        Intent i = new Intent(LoginActivity.this, ResetActivity.class);
        startActivity(i);
        finish();
        //finish();
    }

    public void iniciarActividadSiguiente4() {
        Intent i = new Intent(LoginActivity.this, PreguntasFrecuentesActivity.class);
        startActivity(i);
        finish();
        //finish();
    }
}

