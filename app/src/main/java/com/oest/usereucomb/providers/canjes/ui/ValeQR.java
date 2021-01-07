package com.oest.usereucomb.providers.canjes.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import com.oest.usereucomb.providers.login.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ValeQR extends AppCompatActivity {

    private static String IP = "https://lealtadeucomb.lealtaddigitalsoft.mx/public/apivaleqr?username=";

    private Button register;

    private VolleyRP volley;
    private RequestQueue mRequest;

    private String usuarios;
    private String pubnumero;
    private String pubfolio;
    private String tit;
    private TextView titulo;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valeqr);

        usuarios = Preferences.obtenerPreferenceString(this, Preferences.PREFERENCE_USUARIO_LOGIN);
        image = (ImageView) findViewById(R.id.set_picture);

        Intent extra = getIntent();
        Bundle bundle = extra.getExtras();
        pubnumero = bundle.getString("numero");
        pubfolio = bundle.getString("folio");

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();
        SolicitudJSON(IP);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    //---------------------------------------------------------------------------------------------------


    public void SolicitudJSON(String URL){

        JsonObjectRequest solicitud = new JsonObjectRequest(URL + pubfolio,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                VerificarDatos(datos);

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un error.",Toast.LENGTH_SHORT).show();
                String mensa = "No se pudo registrar";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, ValeQR.this);
            }

        });
        VolleyRP.addToQueue(solicitud, mRequest,getBaseContext(), volley);
    }

    public void VerificarDatos(JSONObject datos) {

        try {

            String estado = datos.getString("resultado");
            //Toast.makeText(this, estado, Toast.LENGTH_SHORT).show();

            if(estado.equals("error")) {
                //Toast.makeText(getContext(), "La membresia no existe:", Toast.LENGTH_SHORT).show();
                String mensa = "La membresía no existe";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getBaseContext());
            }
            else{

                final String urlfinal = "https://lealtadeucomb.lealtaddigitalsoft.mx/public/img/valeqrimg/" + pubfolio + ".jpg";
                Glide.with(this).load(urlfinal).into(image);

            }
        } catch (JSONException e) {
            //Toast.makeText(getContext(), "Error"+e, Toast.LENGTH_SHORT).show();
            String mensa = "ERROR";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, ValeQR.this);

//            e.printStackTrace();
        }
    }

    public void iniciarActividadSiguiente() {
        Intent i = new Intent(ValeQR.this, MainActivity.class);
        //i.putExtra("user",USER);
        startActivity(i);
        finish();
    }

    public void iniciarActividadSiguiente2(){
        Toast.makeText(ValeQR.this, "Inicie Sesion", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(ValeQR.this,LoginActivity.class);
        //i.putExtra("user",USER);
        startActivity(i);
        finish();
    }


}
