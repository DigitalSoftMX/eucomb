package com.oest.usereucomb.providers.premios.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.oest.usereucomb.billing.Preferences_Puntos;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.login.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ConfirmarAwards extends AppCompatActivity {

    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/public/apiagregarpremio?";
    private static String IP_puntos = "https://eucomb.lealtaddigitalsoft.mx/public/apipuntos?username=";
    private TextView estacion;
    private TextView puntos;
    private TextView fecha;
    private TextView vales;
    private ImageView imagen;

    private Button register;

    private VolleyRP volley;
    private RequestQueue mRequest;

    private String usuarios;
    public Integer pubpremio;
    public String pubestacion;
    public String pubpuntos;
    public String pubimg;
    public String pubnombre;
    public String pubnameestacion;
    String pubpre;
    Integer pubest;
    private Toolbar mToolbar;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacionaward);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Eucomb");

        usuarios = Preferences.obtenerPreferenceString(this, Preferences.PREFERENCE_USUARIO_LOGIN);

        estacion = (TextView) findViewById(R.id.datEstacion);
        imagen = (ImageView) findViewById(R.id.imagenvale);
        puntos = (TextView) findViewById(R.id.puntos);

        Intent extra = getIntent();
        Bundle bundle = extra.getExtras();
        pubpre = bundle.getString("pre");
        pubest = bundle.getInt("est");
        pubpuntos = bundle.getString("puntos");
        pubimg = bundle.getString("img");
        pubnombre = bundle.getString("nombre");
        pubnameestacion = bundle.getString("nameestacion");


        Log.e("kikosaward", pubpre);
        Log.e("kikosaward", String.valueOf(pubest));
        estacion.setText(pubnameestacion);
        puntos.setText("Puntos: " + pubpuntos);
        //fecha.setText(pubfecha);
        //vales.setText(pubvales);

        String ima = pubimg;
        android.util.Log.e("imagenpremio", ima);
        final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/premios/" + ima;

        Glide.with(getBaseContext()).load(urlfinal).into(imagen);

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        register = (Button) findViewById(R.id.bTaceptarPerfil);

        final String number = pubpre;
        final String number2 = String.valueOf(pubest);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registrarWebService(
                        number,
                        number2,
                        usuarios);

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
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //---------------------------------------------------------------------------------------------------

    public void registrarWebService(String number, String premio, final String usuario){
        progressDialog = ProgressDialog.show(this, "",
                "Espere ............", true);

        if(!usuario.isEmpty()) {

            //Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
            HashMap<String, String> hashMapToken = new HashMap<>();
            hashMapToken.put("premio", number);
            hashMapToken.put("estacion", premio);
            hashMapToken.put("username", usuario);
            Log.e("confirmacion", number);
            Log.e("confirmacion1", premio);
            Log.e("confirmacion2", usuario);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP_Registrar, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject datos) {
                    try {
                        String estado = datos.getString("resultado");

                        /// Toast.makeText(getBaseContext(), estado, Toast.LENGTH_SHORT).show();

                        if (estado.equalsIgnoreCase("No se tiene suficientes puntos")) {
                            String mensa = "No se tiene suficientes puntos";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
                        }
                        else if (estado.equalsIgnoreCase("Se acabaron los premios en esta estacion")) {
                            String mensa = "Se acabaron los premios en esta estacion";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
                        }
                        else if (estado.equalsIgnoreCase("Solo se permiten 1 premio por dia")) {
                            String mensa = "Solo se permiten 1 premio por dia";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
                        }
                        else{
                            String mensa = "Presenta una identificación oficial al recoger tu premio. Solo el propietario de la membresía puede recoger el premio.";
                            String imamen = "activo";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);

                            registrarWebPoint();
                        }

                        progressDialog.dismiss();

                        //iniciarActividadSiguiente();
                        //finish();

                    } catch (JSONException e) {
                        //Toast.makeText(Confirmacion.this, "No se actualizo", Toast.LENGTH_SHORT).show();
                        String mensa = "No se actualizo";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
                        progressDialog.dismiss();

                    }
                    //Toast.makeText(Registro.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(Confirmacion.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                    String mensa = "No se pudo actualizar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
                    progressDialog.dismiss();

                }
            });
            VolleyRP.addToQueue(solicitud, mRequest, this, volley);
        }
        else{
            //Toast.makeText(Confirmacion.this, "Por favor llene todos los campos.", Toast.LENGTH_SHORT).show();
            String mensa = "Porfavor llene todos los campos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
        }
    }

    private String getStringET(EditText e){
        return e.getText().toString();
    }


    public void registrarWebPoint(){

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.GET, IP_puntos + usuarios, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                    //Toast.makeText(Confirmacion.this, "Puntos " + estado, Toast.LENGTH_SHORT).show();

                    Preferences_Puntos.savePreferenceBoolean(ConfirmarAwards.this, false, Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                    Preferences_Puntos.removePreferenceBoolean(ConfirmarAwards.this, Preferences_Puntos.PREFERENCE_PUNTOS);

                    Preferences_Puntos.savePreferenceBoolean(ConfirmarAwards.this,true,Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                    Preferences_Puntos.savePreferenceString(ConfirmarAwards.this,estado,Preferences_Puntos.PREFERENCE_PUNTOS);

                } catch (JSONException e) {
                    //Toast.makeText(Confirmacion.this, "No se actualizo", Toast.LENGTH_SHORT).show();
                    String mensa = "No se actualizo";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);
                }
                //Toast.makeText(Registro.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Confirmacion.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                String mensa = "No se actualizo";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ConfirmarAwards.this);

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, this, volley);

    }

    public void iniciarActividadSiguiente() {
        Intent i = new Intent(ConfirmarAwards.this, MainActivity.class);
        //i.putExtra("user",USER);
        startActivity(i);
        finish();
    }

    public void iniciarActividadSiguiente2(){
        // Toast.makeText(Confirmacion.this, "Inicie Sesion", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ConfirmarAwards.this, LoginActivity.class);
        //i.putExtra("user",USER);
        startActivity(i);
        finish();
    }


}
