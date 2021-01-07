package com.oest.usereucomb.providers.facturacion.ui;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.facturacion.AdapterFacturacion;
import com.oest.usereucomb.providers.facturacion.SectionFacturacion;
import com.oest.usereucomb.providers.login.ui.LoginActivity;
import com.oest.usereucomb.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class FacturacionFragment extends Fragment {

    ArrayList<SectionFacturacion> sectionss;
    AdapterFacturacion adapterSection;
    RecyclerView rvSection;

    private RelativeLayout rl;

    private VolleyRP volley;
    private RequestQueue mRequest;
    String usuario;
    String envMes;
    String envEstacion;
    TextView titulo;
    TextView total;
    String tit;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/apiestadocuentafactura?";
    private static String IP_puntos = "https://eucomb.lealtaddigitalsoft.mx/apiestaciones?username=";
    public static final String JSON_ARRAY = "resultado";
    private Button filtro;
    Spinner mes;
    Spinner estacion;
    private static final int PERMISSION_REQUEST_CODE = 100;

    String namemes [] = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    ArrayAdapter <String> meses;

    //String nameestaciones [];
    //ArrayAdapter <String> estaciones;
    private ArrayList<String> estaciones;
    private JSONArray result;
    ArrayList<SectionFacturacion> listSection;

    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_list_facturacion,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        filtro = (Button) rl.findViewById(R.id.enviar);
        mes = (Spinner) rl.findViewById(R.id.mes);
        estacion = (Spinner) rl.findViewById(R.id.estacion);

        //Check for external storage and internet permission
        checkPermission();
            requestPermission();

            //Initializing the ArrayList
        estaciones = new ArrayList<String>();

        meses = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,namemes);
        registrarWebEstaciones();
        mes.setAdapter(meses);

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

        estacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                //Toast.makeText(adapterView.getContext(),(String) adapterView.getItemAtPosition(pos), Toast.LENGTH_SHORT).show();
                envEstacion = (String) adapterView.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });

        listSection = new ArrayList<>();
        final Context c = getContext();

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               listSection.clear();
                //Toast.makeText(getContext(), "aver historial" + envEstacion, Toast.LENGTH_LONG).show();

                if(!envMes.equals(" ") & !envEstacion.equals(" ")){
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Espere..."); // Setting Message
                    progressDialog.setTitle("Puede tardar un momento"); // Setting Title
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                    progressDialog.show(); // Display Progress Dialog
                    progressDialog.setCancelable(false);
                    SolicitudJSON(usuario,
                            envMes,
                            envEstacion);
                }
                else{
                    String mensa = "Seleccionar el Mes y la Estacion";
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

    public void SolicitudJSON(String usuario, String envMes, String envEstacion) {

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("username", usuario);
        hashMapToken.put("mes", envMes);
        hashMapToken.put("estacion", envEstacion);

        Log.d("estacion", envEstacion);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
            public void onResponse(JSONObject datos) {

                getSection(datos);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "Ocurrio un error", Toast.LENGTH_LONG).show();
                String mensa = "Ocurrio un error1";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);

    }

    public void SolicitudJSON2(final JSONArray extra, String usuario, String envMes, String envEstacion) {

            // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMapToken = new HashMap<>();
            hashMapToken.put("username", usuario);
            hashMapToken.put("mes", envMes);
            hashMapToken.put("estacion", envEstacion);



            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {

           @Override
            public void onResponse(JSONObject datos2) {

                sectionss = getSection2(datos2, extra);
                if (sectionss != null) {

                    adapterSection = new AdapterFacturacion(sectionss);
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

    public ArrayList<SectionFacturacion> getSection2(JSONObject datos, JSONArray extra) {


        if (extra.toString() == " ") {
            Toast.makeText(getContext(), "no tiene datos", Toast.LENGTH_LONG).show();
        } else {

            try {
                for (int i = 0; i < extra.length(); i++) {
                    JSONObject js = extra.getJSONObject(i);
                    int id = js.getInt("id");
                    String puntos = js.getString("litros");
                    String estacion = js.getString("namestation");
                    String concepto = js.getString("combustible");
                    String fecha = js.getString("created_at");
                    String folio = js.getString("folio");
                    String pdf = js.getString("archivo");

                    SectionFacturacion sectionss = new SectionFacturacion();
                    sectionss.setId(id);
                    sectionss.setPuntos(puntos);
                    sectionss.setEstacion(estacion);
                    sectionss.setConcepto(concepto);
                    sectionss.setTodate_certificado(fecha);
                    sectionss.setFolio(folio);
                    sectionss.setValor(pdf);
                    listSection.add(sectionss);
                }
                progressDialog.dismiss();

            } catch (JSONException e) {
                // Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
                progressDialog.dismiss();
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
                    envEstacion);
            progressDialog.dismiss();

        } catch (JSONException e) {
          //  Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            String mensa = "No hay información";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());
            progressDialog.dismiss();

        }
    }

//Estaciones *************************************************************************************

    public void registrarWebEstaciones(){

        //Creating a string request
        StringRequest stringRequest = new StringRequest(IP_puntos + usuario ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            //Parsing the fetched Json String to JSON Object
                            j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray("resultado");

                            //Calling method getStudents to get the students from the JSON Array
                            getSectionEstadoCuenta(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    public void getSectionEstadoCuenta(JSONArray datos) {

        //Traversing through all the items in the json array
        for (int i = 0; i < datos.length(); i++) {
            try {
                //Getting json object
                JSONObject json = datos.getJSONObject(i);

                //Adding the name of the student to array list
                estaciones.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            estacion.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, estaciones));

        }
    }

    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }


  //PERMISOS ***********************************************************************************+
  private boolean checkPermission() {
      int result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
      if (result == PackageManager.PERMISSION_GRANTED) {
          return true;
      } else {
          return false;
      }
  }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
            break;
        }
    }
}

