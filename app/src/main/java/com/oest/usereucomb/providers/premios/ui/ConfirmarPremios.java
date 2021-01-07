package com.oest.usereucomb.providers.premios.ui;

import android.content.Intent;
import android.app.ProgressDialog;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.premios.AdapterConfirmarPremios;
import com.oest.usereucomb.providers.vales.SectionVales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConfirmarPremios extends AppCompatActivity {

    //Views
    private RelativeLayout rl;

    ArrayList<SectionVales> sectionss;
    AdapterConfirmarPremios adapterSection;
    RecyclerView rvSection;

    private VolleyRP volley;
    private RequestQueue mRequest;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apipremioestacion?";
    ArrayList<SectionVales> listSectionVale;
    String usuario;
    ProgressDialog progressDialog;

    public Integer pubnumero;
    public String pubestacion;
    public String pubpuntos;
    public String pubfecha;
    public String pubvales;
    private ImageView imagen;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list_refresh_confirmacionpremio);

        rvSection = findViewById(R.id.list);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Eucomb");

        Mensajes.MyCustomAlertDialogPremiosVales(ConfirmarPremios.this);

        usuario = Preferences.obtenerPreferenceString(getBaseContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        String tit = "SELECCIONA LA ESTACIÓN";
        TextView titulo = (TextView) findViewById(R.id.titulo);
        titulo.setText(tit);

        Intent extra = getIntent();
        Bundle bundle = extra.getExtras();
        pubnumero = bundle.getInt("numero");
        pubestacion = bundle.getString("img");
        pubpuntos = bundle.getString("puntos");
        pubvales = bundle.getString("nombre");

        Log.e("kikos", String.valueOf(pubnumero));

        TextView name = (TextView) findViewById(R.id.name);
        name.setText("Premio: " + pubvales);
        TextView puntos = (TextView) findViewById(R.id.puntos);
        puntos.setText("Puntos: " + pubpuntos);

        imagen = (ImageView) findViewById(R.id.imagenvale);

        String ima = pubestacion;
        android.util.Log.e("imagenvale", ima);
        final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/premios/" + ima;

        Glide.with(getBaseContext()).load(urlfinal).into(imagen);


        volley = VolleyRP.getInstance(getBaseContext());
        mRequest = volley.getRequestQueue();

        listSectionVale = new ArrayList<>();

        progressDialog = ProgressDialog.show(this, "",
                "Espere ............", true);


         SolicitudJSON(URL);


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

//********************************************************************************************

    public void SolicitudJSON(String URL) {
        JsonObjectRequest solicitud = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject datos) {

                getSection(datos);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getBaseContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest, getBaseContext(), volley);

    }

    public void SolicitudJSON2(String URL, final JSONArray extra) {

        JsonObjectRequest solicitud2 = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject datos2) {

                sectionss = getSection2(datos2, extra);
                if (sectionss != null) {

                    adapterSection = new AdapterConfirmarPremios(sectionss);
                    rvSection.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false));
                    rvSection.setAdapter(adapterSection);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un errorsolicitud2.", Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getBaseContext());

            }

        });
        VolleyRP.addToQueue(solicitud2, mRequest, getBaseContext(), volley);

    }

    public ArrayList<SectionVales> getSection2(JSONObject datos, JSONArray extra){

        //Toast.makeText(ConfirmarPremios.this, "aver historial" + extra.toString(), Toast.LENGTH_SHORT).show();
        try {
            for (int i = 0; i < extra.length(); i++) {
                JSONObject js = extra.getJSONObject(i);
                int id = js.getInt("id");
                String estacion = js.getString("name");

                SectionVales sectionss = new SectionVales();
                sectionss.setId(id);
                sectionss.setEstacion(estacion);
                sectionss.setNumero(String.valueOf(pubnumero));
                sectionss.setImagen1(pubestacion);
                sectionss.setPuntos(pubpuntos);
                sectionss.setConcepto(pubvales);
                listSectionVale.add(sectionss);
                progressDialog.dismiss();

            }

        } catch (JSONException e) {
            //Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            String mensa = "Ocurrio un error";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getBaseContext());
        }
        return listSectionVale;
    }

    public void getSection(JSONObject datos) {

        try {
            String TodoslosDatos = datos.getString("resultado");
            JSONArray jsonArray = new JSONArray(TodoslosDatos);

           /* for (int i = 0; i <= jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);
                //us = js.getString("name");
               Toast.makeText(Change.this, "Exampleesss " + i + js.getString("name"), Toast.LENGTH_SHORT).show();
            }
*/
            SolicitudJSON2(URL, jsonArray);

        } catch (JSONException e) {
            //Toast.makeText(getContext(), "No hay vales", Toast.LENGTH_SHORT).show();
            String mensa = "No hay Vales";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getBaseContext());
        }
    }

}


