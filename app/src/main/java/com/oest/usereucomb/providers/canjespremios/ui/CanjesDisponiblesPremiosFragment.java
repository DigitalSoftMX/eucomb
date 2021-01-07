package com.oest.usereucomb.providers.canjespremios.ui;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.oest.usereucomb.providers.canjespremios.AdapterCanjesPremios;
import com.oest.usereucomb.providers.canjespremios.SectionCanjesPremios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CanjesDisponiblesPremiosFragment extends Fragment {

    //Views
    private RelativeLayout rl;

    ArrayList<SectionCanjesPremios> sectionss;
    AdapterCanjesPremios adapterSection;
    RecyclerView rvSection;

    private VolleyRP volley;
    private RequestQueue mRequest;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apiexchangedisponiblespremios?username=";
    ArrayList<SectionCanjesPremios> listSectionCanjes;
    String usuario;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_list_refresh_rendimiento,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        // String imamen = "canjes";
        // Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        listSectionCanjes = new ArrayList<>();

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

                    adapterSection = new AdapterCanjesPremios(sectionss);
                    rvSection.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    rvSection.setAdapter(adapterSection);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getContext(), "Ocurrio un errorsolicitud2.", Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud2, mRequest, getContext(), volley);

    }

    public ArrayList<SectionCanjesPremios> getSection2(JSONObject datos, JSONArray extra){

        //Toast.makeText(Historial.this, "aver historial" + extra.toString(), Toast.LENGTH_SHORT).show();
        try {
            for (int i = 0; i < extra.length(); i++) {
                JSONObject js = extra.getJSONObject(i);
                int id = js.getInt("id");
                String folio = js.getString("identificador");
                String numero = js.getString("conta");
                String lugar = js.getString("name");
                String image = js.getString("img");
                String puntos = js.getString("punto");
                String valor = js.getString("value");
                String estatus = js.getString("name_state");
                String fecha = js.getString("estado_uno");

                SectionCanjesPremios sectionss = new SectionCanjesPremios();
                sectionss.setId(id);
                sectionss.setFolio(folio);
                sectionss.setNumero(numero);
                sectionss.setLugar(lugar);
                sectionss.setImagen1(image);
                sectionss.setPuntos(puntos);
                sectionss.setValor(valor);
                sectionss.setEstatus(estatus);
                sectionss.setTodate_certificado(fecha);
                listSectionCanjes.add(sectionss);
            }

        } catch (JSONException e) {
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
        }
        return listSectionCanjes;
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
            // Toast.makeText(getContext(), "No hay canjes", Toast.LENGTH_SHORT).show();
            String mensa = "No hay canjes";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
        }
    }


}

