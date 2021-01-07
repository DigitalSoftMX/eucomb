package com.oest.usereucomb.providers.vales.ui;


import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.oest.usereucomb.providers.vales.AdapterVales;
import com.oest.usereucomb.providers.vales.SectionVales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ValesFragment extends Fragment {

    //Views
    private RelativeLayout rl;

    ArrayList<SectionVales> sectionss;
    AdapterVales adapterSection;
    RecyclerView rvSection;

    private VolleyRP volley;
    private RequestQueue mRequest;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apivale?";
    ArrayList<SectionVales> listSectionVale;
    String usuario;
    ProgressDialog progressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_list_refresh_vale,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        String tit = "SELECCIONA LA ESTACIÓN";
        TextView titulo = (TextView) rl.findViewById(R.id.titulo);
        titulo.setText(tit);

        String imamen = "vales";
        Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        listSectionVale = new ArrayList<>();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Espere..."); // Setting Message
        progressDialog.setTitle("Puede tardar un momento"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);

        SolicitudJSON(URL);

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

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
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

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

                    adapterSection = new AdapterVales(sectionss);
                    rvSection.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    rvSection.setAdapter(adapterSection);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un errorsolicitud2.", Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud2, mRequest, getContext(), volley);

    }

    public ArrayList<SectionVales> getSection2(JSONObject datos, JSONArray extra){

        // Toast.makeText(Vale.this, "aver historial" + extra.toString(), Toast.LENGTH_SHORT).show();
        try {
            for (int i = 0; i < extra.length(); i++) {
                JSONObject js = extra.getJSONObject(i);
                int id = js.getInt("id_station");
                String estacion = js.getString("name");
                String puntos = js.getString("points");
                //String fecha = js.getString("validity");
                String vales = js.getString("total_voucher");

                SectionVales sectionss = new SectionVales();
                sectionss.setId(id);
                sectionss.setEstacion(estacion);
                sectionss.setPuntos(puntos);
                //sectionss.setTodate_certificado(fecha);
                sectionss.setVales(vales);
                listSectionVale.add(sectionss);
                progressDialog.dismiss();

            }

        } catch (JSONException e) {
            //Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            String mensa = "Ocurrio un error";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
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
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
        }
    }

}

