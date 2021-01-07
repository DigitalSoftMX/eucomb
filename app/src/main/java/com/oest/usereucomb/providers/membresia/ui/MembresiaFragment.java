package com.oest.usereucomb.providers.membresia.ui;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
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

public class MembresiaFragment extends Fragment {

    private RelativeLayout rl;
    RecyclerView rvSection;

    ArrayList<SectionEstadoCuenta> sectionss;
    AdapterEstadoCuenta adapterSection;

    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/public/apimembresia?username=";
    private static String IPS = "https://eucomb.lealtaddigitalsoft.mx/public/apimembresiaestadocuenta?username=";

    private TextView email;
    private TextView name;
    private TextView usernames;
    private TextView resultad;
    private ImageView image;
    ProgressDialog progressDialog;
    private static int location;
    private static final int PETICION_PERMISO_LOCALIZACION = location;

    private Button vermem;
    private Button canmem;
    private Button premem;

    private VolleyRP volley;
    private RequestQueue mRequest;
    private String usuario;

    ArrayList<SectionEstadoCuenta> listSection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_membresia,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        //String imamen = "membresia";
        //Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        email = (TextView) rl.findViewById(R.id.qr);
        name = (TextView) rl.findViewById(R.id.points);
        usernames = (TextView) rl.findViewById(R.id.nombre);
        image = (ImageView) rl.findViewById(R.id.set_picture);
        vermem = (Button) rl.findViewById(R.id.user_ver_membresia);
        canmem = (Button) rl.findViewById(R.id.user_canjear_in_button);
        premem = (Button) rl.findViewById(R.id.user_canjearpremio_in_button);
        resultad = (TextView) rl.findViewById(R.id.resultado);
        listSection = new ArrayList<>();

        if(usuario == "") {
            iniciarActividadSiguiente2();
        }
        else {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Espere..."); // Setting Message
            progressDialog.setTitle("Puede tardar un momento"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PETICION_PERMISO_LOCALIZACION);
            }


            SolicitudJSON(IP + usuario);
            SolicitudJSON2(IPS + usuario);
        }

        vermem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String imamen = "qr";
                Mensajes.MyCustomAlertDialogPrincipal(imamen, getContext());
            }
        });

        canmem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               iniciarActividadSiguiente3();
            }
        });

        premem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iniciarActividadSiguiente4();
            }
        });

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
                //Toast.makeText(getContext(), "Ocurrio un error.",Toast.LENGTH_SHORT).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest,getContext(), volley);
    }

    public void VerificarDatos(JSONObject datos) {

        try {

            String estado = datos.getString("resultado");
            //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();

            if(estado.equals("error")) {

                Preferences.savePreferenceBoolean(getContext(), false, Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                Preferences.removePreferenceBoolean(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

                //Toast.makeText(getContext(), "La membresia no existe:", Toast.LENGTH_SHORT).show();
                String mensa = "La membresía no existe";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
                progressDialog.dismiss();
                iniciarActividadSiguiente2();
            }
            else{
                JSONObject Jsondatos = new JSONObject(datos.getString("resultado"));

                String qr = Jsondatos.getString("number_usuario");
                String point = Jsondatos.getString("totals");
                String visit = Jsondatos.getString("nombre");

                usernames.setText(visit);
                email.setText(qr);
                name.setText(point);

                final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/img/usuarioimg/" + qr + ".jpg";
                Glide.with(getContext()).load(urlfinal).into(image);
                //progressDialog.dismiss();
            }

        } catch (JSONException e) {
            //Toast.makeText(getContext(), "Error"+e, Toast.LENGTH_SHORT).show();
            String mensa = "Ocurrio un error";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

//            e.printStackTrace();
        }
    }

//------------------------------------------------------------------------------------------------------------------

//Tabla----------------------------------------------------------------------------------------------------------------



    public void SolicitudJSON2(String URL) {

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", usuario);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
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

    public void SolicitudJSON3(final JSONArray extra, String URL) {

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", usuario);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.GET, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {

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

        String ex = String.valueOf(extra.length());
        //Toast.makeText(getContext(), ex, Toast.LENGTH_LONG).show();

        if (ex.equals("0")) {
            //Toast.makeText(getContext(), "no tiene datos", Toast.LENGTH_LONG).show();
            resultad.setText("No hay resultados");
            progressDialog.dismiss();

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

                    Log.e("membrepunt", puntos);

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

            /*for (int i = 0; i <= jsonArray.length(); i++) {
                JSONObject js = jsonArray.getJSONObject(i);
                //us = js.getString("name");
               Toast.makeText(getContext(), "Exampleesss " + i + js.getString("folios"), Toast.LENGTH_SHORT).show();
            }*/

            SolicitudJSON3(jsonArray,
                    IPS + usuario);

        } catch (JSONException e) {
            //  Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            String mensa = "No hay información";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
        }
    }


    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    public void iniciarActividadSiguiente3(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), Canjear.class);
        startActivity(i);
        this.getActivity();
        //finish();
    }

    public void iniciarActividadSiguiente4(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), Premio.class);
        startActivity(i);
        this.getActivity();
        //finish();
    }

}



