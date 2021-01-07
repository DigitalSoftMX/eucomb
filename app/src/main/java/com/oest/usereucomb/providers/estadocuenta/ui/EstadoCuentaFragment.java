package com.oest.usereucomb.providers.estadocuenta.ui;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.Preferences_Puntos;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.estadocuenta.AdapterEstadoCuenta;
import com.oest.usereucomb.providers.estadocuenta.SectionEstadoCuenta;
import com.oest.usereucomb.providers.login.ui.LoginActivity;
import com.oest.usereucomb.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EstadoCuentaFragment extends Fragment {

    ArrayList<SectionEstadoCuenta> sectionss;
    AdapterEstadoCuenta adapterSection;
    RecyclerView rvSection;

    private RelativeLayout rl;

    private VolleyRP volley;
    private RequestQueue mRequest;
    String usuario;
    String envMes;
    String envYear;
    TextView titulo;
    TextView total;
    String tit;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apimovimientos?";
    private static String IP_puntos = "https://eucomb.lealtaddigitalsoft.mx/public/apipuntos?username=";
    private Button filtro;
    Spinner mes;
    Spinner year;

    String namemes [] = {"Mes","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    ArrayAdapter <String> meses;

    String nameyear [] = {"Año","2018","2019","2020","2021"};
    ArrayAdapter <String> years;

    ArrayList<SectionEstadoCuenta> listSection;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_list_estadocuenta,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        String imamen = "estadocuenta";
        Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

         total = (TextView) rl.findViewById(R.id.totalpoint);
        //Log.d("suma", totpoin);

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        filtro = (Button) rl.findViewById(R.id.enviar);
        mes = (Spinner) rl.findViewById(R.id.mes);
        year = (Spinner) rl.findViewById(R.id.year);

        meses = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,namemes);
        years = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,nameyear);

        mes.setAdapter(meses);
        year.setAdapter(years);

        mes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                // Toast.makeText(adapterView.getContext(),(String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
                envMes = (String) adapterView.getItemAtPosition(pos);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                //Toast.makeText(adapterView.getContext(),(String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
                envYear = (String) adapterView.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        listSection = new ArrayList<>();

        if(usuario == "") {
            iniciarActividadSiguiente2();
        }
        else {

            registrarWebPoint();
        }
        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               listSection.clear();
                //Toast.makeText(getContext(), "aver historial" + envMes, Toast.LENGTH_LONG).show();

                if(!envMes.equals("Mes") & !envYear.equals("Año")){
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Espere..."); // Setting Message
                    progressDialog.setTitle("Puede tardar un momento"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    SolicitudJSON(usuario,
                            envMes,
                            envYear);
                    /*registrarWebService(
                            usuario,
                            envMes,
                            envYear);*/
                }
                else{
                    String mensa = "Seleccionar el Mes y el Año";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                }

            }
        });

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void SolicitudJSON(String usuario, String envMes, String envYear) {

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", usuario);
        hashMapToken.put("mes", envMes);
        hashMapToken.put("year", envYear);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
            public void onResponse(JSONObject datos) {

                getSection(datos);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un error", Toast.LENGTH_LONG).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);

    }

    public void SolicitudJSON2(final JSONArray extra, String usuario, String envMes, String envYear) {

            // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMapToken = new HashMap<>();
            hashMapToken.put("username", usuario);
            hashMapToken.put("mes", envMes);
            hashMapToken.put("year", envYear);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {

           @Override
            public void onResponse(JSONObject datos2) {

                sectionss = getSection2(datos2, extra);
                if (sectionss != null) {

                    adapterSection = new AdapterEstadoCuenta(sectionss);
                    rvSection.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    rvSection.setAdapter(adapterSection);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un error.", Toast.LENGTH_LONG).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);

    }

    public ArrayList<SectionEstadoCuenta> getSection2(JSONObject datos, JSONArray extra) {


        if (extra.toString() == " ") {
            Toast.makeText(getContext(), "no tiene datos", Toast.LENGTH_LONG).show();
        } else {

            try {
                for (int i = 0; i < extra.length(); i++) {
                    JSONObject js = extra.getJSONObject(i);
                    int id = js.getInt("id");
                    String puntos = js.getString("punto");
                    String estacion = js.getString("name");
                    String concepto = js.getString("descrip");
                    String fecha = js.getString("fh_ticket");
                    String folio = js.getString("folios");

                    Log.e("estadopunt", fecha);

                    SectionEstadoCuenta sectionss = new SectionEstadoCuenta();
                    sectionss.setId(id);
                    sectionss.setPuntos(puntos);
                    sectionss.setEstacion(estacion);
                    sectionss.setConcepto(concepto);
                    sectionss.setTodate_certificado(fecha);
                    sectionss.setFolio(folio);
                    listSection.add(sectionss);
                }
                progressDialog.dismiss();

            } catch (JSONException e) {
                // Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
            }
        }
            return listSection;
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
            SolicitudJSON2(jsonArray,
                    usuario,
                    envMes,
                    envYear);

        } catch (JSONException e) {
          //  Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            String mensa = "No hay información";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
        }
    }

    public void registrarWebPoint(){

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.GET, IP_puntos + usuario, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                    //Toast.makeText(Confirmacion.this, "Puntos " + estado, Toast.LENGTH_SHORT).show();

                    Preferences_Puntos.savePreferenceBoolean(getContext(), false, Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                    Preferences_Puntos.removePreferenceBoolean(getContext(), Preferences_Puntos.PREFERENCE_PUNTOS);

                    Preferences_Puntos.savePreferenceBoolean(getContext(),true,Preferences_Puntos.PREFERENCE_ESTADO_BUTTON_SESION_PUNTOS);
                    Preferences_Puntos.savePreferenceString(getContext(),estado,Preferences_Puntos.PREFERENCE_PUNTOS);

                    String totpoin = Preferences_Puntos.obtenerPreferenceString(getContext(),Preferences_Puntos.PREFERENCE_PUNTOS);
                    total.setText(totpoin);

                } catch (JSONException e) {
                    //Toast.makeText(Confirmacion.this, "No se actualizo", Toast.LENGTH_SHORT).show();
                    String mensa = "No se actualizo";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                }
                //Toast.makeText(Registro.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Confirmacion.this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
                String mensa = "No se actualizo";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);

    }

    public void registrarWebService(String usuario, String envMes, String envYear) {

            // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMapToken = new HashMap<>();
            hashMapToken.put("username", usuario);
            hashMapToken.put("mes", envMes);
            hashMapToken.put("year", envYear);

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject datos) {
                    try {
                        String estado = datos.getString("resultado");




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

    }

    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }


}

