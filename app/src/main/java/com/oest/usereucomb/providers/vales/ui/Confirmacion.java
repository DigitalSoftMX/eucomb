package com.oest.usereucomb.providers.vales.ui;

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


public class Confirmacion extends AppCompatActivity {

    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/public/apiagregarvale?";
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
    public Integer pubnumero;
    public String pubestacion;
    public String pubpuntos;
    public String pubfecha;
    public String pubvales;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion);


        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Eucomb");

        Mensajes.MyCustomAlertDialogPremiosVales(Confirmacion.this);

        usuarios = Preferences.obtenerPreferenceString(this, Preferences.PREFERENCE_USUARIO_LOGIN);

        estacion = (TextView) findViewById(R.id.datEstacion);
        puntos = (TextView) findViewById(R.id.puntos);
        //fecha = (TextView) findViewById(R.id.datFecha);
        //vales = (TextView) findViewById(R.id.datVales);
        imagen = (ImageView) findViewById(R.id.imagenvale);

        Intent extra = getIntent();
        Bundle bundle = extra.getExtras();
        pubnumero = bundle.getInt("numero");
        pubestacion = bundle.getString("estacion");
        pubpuntos = bundle.getString("puntos");
        //pubfecha = bundle.getString("fecha");
        pubvales = bundle.getString("vales");

        Log.e("kikos", pubpuntos);
        estacion.setText(pubestacion);
        //puntos.setText("500 puntos = 1 vale: " + pubpuntos);
        puntos.setText(pubpuntos + " puntos = 1 vale");
        //puntos.setText(pubpuntos);
        //fecha.setText(pubfecha);
        //vales.setText(pubvales);

        String ima = pubestacion;
        android.util.Log.e("imagenvale", ima);
        final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/" + ima + ".jpg";

        Glide.with(getBaseContext()).load(urlfinal).into(imagen);

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        register = (Button) findViewById(R.id.bTaceptarPerfil);

        final String number = String.valueOf(pubnumero);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registrarWebService(
                        number,
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

    public void registrarWebService(String number, final String usuario){

        if(!usuario.isEmpty()) {

            //Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
            HashMap<String, String> hashMapToken = new HashMap<>();
            hashMapToken.put("id", number);
            hashMapToken.put("username", usuario);
            Log.e("confirmacion", number);
            Log.e("confirmacion2", usuario);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP_Registrar, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject datos) {
                    try {
                        String estado = datos.getString("resultado");
                        Log.e("mensaje", estado);
                       /// Toast.makeText(getBaseContext(), estado, Toast.LENGTH_SHORT).show();

                        if (estado.equalsIgnoreCase("No se tiene suficientes puntos")) {
                            String mensa = "No se tiene suficientes puntos";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);
                        }
                        else if (estado.equalsIgnoreCase("Se acabaron los vales en esta estacion")) {
                            String mensa = "Se acabaron los vales en esta estacion";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);
                        }
                        else if (estado.equalsIgnoreCase("Solo se permite 1 vale por dia")) {
                            String mensa = "Solo se permiten 1 vale por dia";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);
                        }
                        else{
                            Log.e("mensaje bien ejecutado", estado);
                            String mensa = "Presenta una identificación oficial al recoger tu vale. Solo el propietario de la membresía puede recoger el vale.";
                            String imamen = "activo";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);
                            registrarWebPoint();
                        }


                        //iniciarActividadSiguiente();
                        //finish();

                    } catch (JSONException e) {
                        //Toast.makeText(Confirmacion.this, "No se actualizo", Toast.LENGTH_SHORT).show();
                        String mensa = "No se actualizo";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);

                    }
                    //Toast.makeText(Registro.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                   // Toast.makeText(Confirmacion.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                    String mensa = "No se pudo actualizar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);

                }
            });
            VolleyRP.addToQueue(solicitud, mRequest, this, volley);
        }
        else{
            //Toast.makeText(Confirmacion.this, "Por favor llene todos los campos.", Toast.LENGTH_SHORT).show();
            String mensa = "Porfavor llene todos los campos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);
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

                    Preferences_Puntos.savePreferenceBoolean(Confirmacion.this, false, Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                    Preferences_Puntos.removePreferenceBoolean(Confirmacion.this, Preferences_Puntos.PREFERENCE_PUNTOS);

                    Preferences_Puntos.savePreferenceBoolean(Confirmacion.this,true,Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                    Preferences_Puntos.savePreferenceString(Confirmacion.this,estado,Preferences_Puntos.PREFERENCE_PUNTOS);

                } catch (JSONException e) {
                    //Toast.makeText(Confirmacion.this, "No se actualizo", Toast.LENGTH_SHORT).show();
                    String mensa = "No se actualizo";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);
                }
                //Toast.makeText(Registro.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Confirmacion.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                String mensa = "No se actualizo";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, Confirmacion.this);

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, this, volley);

    }

    public void iniciarActividadSiguiente() {
        Intent i = new Intent(Confirmacion.this, MainActivity.class);
        //i.putExtra("user",USER);
        startActivity(i);
        finish();
    }

    public void iniciarActividadSiguiente2(){
       // Toast.makeText(Confirmacion.this, "Inicie Sesion", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Confirmacion.this, LoginActivity.class);
        //i.putExtra("user",USER);
        startActivity(i);
        finish();
    }


}
