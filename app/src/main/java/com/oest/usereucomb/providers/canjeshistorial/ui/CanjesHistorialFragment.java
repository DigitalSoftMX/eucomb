package com.oest.usereucomb.providers.canjeshistorial.ui;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.canjeshistorial.AdapterCanjesHistorial;
import com.oest.usereucomb.providers.canjeshistorial.SectionCanjesHistorial;
import com.oest.usereucomb.providers.login.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CanjesHistorialFragment extends Fragment {

    //Views
    private RelativeLayout rl;

    ArrayList<SectionCanjesHistorial> sectionss;
    AdapterCanjesHistorial adapterSection;
    RecyclerView rvSection;

    private VolleyRP volley;
    private RequestQueue mRequest;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apihistorial?username=";
    ArrayList<SectionCanjesHistorial> listSectionCanjesHistorial;
    String usuario;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_list_refresh,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        if(usuario == "") {
            iniciarActividadSiguiente2();
        }

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        listSectionCanjesHistorial = new ArrayList<>();

           SolicitudJSON(URL + usuario);

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // Cambio metodos

    public void SolicitudJSON(String URL) {
        JsonObjectRequest solicitud = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject datos) {

                getSection(datos);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getContext(), "Ocurrio un error", Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);

    }

    public void SolicitudJSON2(String URL, final JSONArray extra) {

        JsonObjectRequest solicitud2 = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject datos2) {

                sectionss = getSection2(datos2, extra);
                if (sectionss != null) {

                    adapterSection = new AdapterCanjesHistorial(sectionss);
                    rvSection.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    rvSection.setAdapter(adapterSection);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  Toast.makeText(getContext(), "Ocurrio un errorsolicitud2.", Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud2, mRequest, getContext(), volley);

    }

    public ArrayList<SectionCanjesHistorial> getSection2(JSONObject datos, JSONArray extra){

        //Toast.makeText(Historial.this, "aver historial" + extra.toString(), Toast.LENGTH_SHORT).show();
        try {
            for (int i = 0; i < extra.length(); i++) {
                JSONObject js = extra.getJSONObject(i);
                int id = js.getInt("id");
                String membresia = js.getString("qr_memberships");
                String tipo = js.getString("name_exchange");
                String puntos = js.getString("points");
                String valor = js.getString("value");
                String fecha = js.getString("todate");

                SectionCanjesHistorial sectionss = new SectionCanjesHistorial();
                sectionss.setId(id);
                sectionss.setMembresia(membresia);
                sectionss.setTipo(tipo);
                sectionss.setPuntos(puntos);
                sectionss.setValor(valor);
                sectionss.setTodate_certificado(fecha);
                listSectionCanjesHistorial.add(sectionss);
            }

        } catch (JSONException e) {
           // Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            String mensa = "Ocurrio un error";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
        }
        return listSectionCanjesHistorial;
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
           // Toast.makeText(getContext(), "No hay historial", Toast.LENGTH_SHORT).show();
            String mensa = "No hay historial";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
        }
    }


    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }
}

