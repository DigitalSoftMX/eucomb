package com.oest.usereucomb.providers.qr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.login.ui.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class QRFormulario extends Fragment {

    private RelativeLayout rl;
    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/apiqrformulario?";
    private static String IP_puntos = "https://eucomb.lealtaddigitalsoft.mx/apiestaciones?username=";
    private String usuario;
    private Button register;

    private EditText idventa;
    private EditText alfanumerico;
    String envEstacion;
    Spinner estacion;

    private VolleyRP volley;
    private RequestQueue mRequest;
    ProgressDialog progressDialog;
    private JSONArray result;
    private ArrayList<String> estaciones;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_qrformulario, null);
        setHasOptionsMenu(true);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);
        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        idventa = (EditText) rl.findViewById(R.id.venta);
        alfanumerico = (EditText) rl.findViewById(R.id.alfa);
        estacion = (Spinner) rl.findViewById(R.id.estaciones);
        register = (Button) rl.findViewById(R.id.user_sign_in_button);
        estaciones = new ArrayList<String>();
        registrarWebEstaciones();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Espere..."); // Setting Message
                progressDialog.setTitle("Puede tardar un momento"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                VerificarRegistro();
            }
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

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void VerificarRegistro() {

        String rfcs = idventa.getText().toString();
        String razon = alfanumerico.getText().toString();

        if (TextUtils.isEmpty(rfcs)) {
            idventa.setError(getString(R.string.error_field_required));
            idventa.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(razon)) {
            alfanumerico.setError(getString(R.string.error_field_required));
            alfanumerico.requestFocus();
            return;
        }

        registrarWebService(
                getStringET(idventa).trim(),
                getStringET(alfanumerico).trim(),
                envEstacion
        );
    }

    //public void registrarWebService(String rfc, String razonsocial, String calle, String numext, String numint, String colonia, String estat, String ciudad, String municipio) {
    public void registrarWebService(String venta, String alfa, String envEstacion) {

        android.util.Log.e("respuesta", envEstacion);
        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        progressDialog = ProgressDialog.show(getContext(), "",
                "Espere ............", true);
        String dispatcher="App";
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("membership", usuario);
        hashMapToken.put("dispatcher", dispatcher);
        hashMapToken.put("venta", venta);
        hashMapToken.put("alfa", alfa);
        hashMapToken.put("estacion", envEstacion);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP_Registrar, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                    //Toast.makeText(getBaseContext(), estado, Toast.LENGTH_SHORT).show();
                    android.util.Log.e("respuesta", "estad" + estado);

                    if(estado.equals("Enviar mas tarde exceso de traficos")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo enviar, intentelo mas tarde o comuniquese con el proveedor.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("Enviar mas tarde exceso de trafico")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo enviar, intentelo mas tarde o comuniquese con el proveedor.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        progressDialog.dismiss();
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("El maximo de puntos acumulados es de 80 por dia")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "El maximo de puntos acumulados es de 80 por dia.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        progressDialog.dismiss();
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("El folio ya fue utilizado")){
                        //Toast.makeText(Guardar.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "El folio ya fue utilizado.";
                        String imamen = "folio";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        progressDialog.dismiss();

                        showMessageError(mensa);

                    }

                    else if(estado.equals("No se pudo agregar se paso de las 24 horas para ingresar su tiket")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo agregar se paso de las 24 horas para ingresar su tiket.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("Intentelo otro dia solo se permiten un numero limitado")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "Intentelo otro dia solo se permiten un numero limitado.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("EL folio no pertenece a esta estacion")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "Comunicate con el proveedor el ticket esta mal impreso.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);
                        progressDialog.dismiss();


                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("El QR escaneado no se encuentra")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "Vuelva a escanear el QR dentro de 30 minutos.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }

                    else if (estado.equalsIgnoreCase("La membresia no existe")) {
                        //Toast.makeText(Guardar.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "La membresia no se encuentra activa.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());

                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("La membresia no existe")){
                        //Toast.makeText(Guardar.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo enviar.";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }


                    else {

                        String mensa = "Se enviaron los datos con éxito.";
                        String imamen = "active";
                        showMessageCorrecto(mensa);

                    }
                } catch (JSONException e) {
                    //Toast.makeText(getContext(), "No se pudo registrar intententelo mas tarde", Toast.LENGTH_SHORT).show();
                    String mensa = "No se pudo registrar intentelo mas tarde.";
                    String imamen = "error";
                    //Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getBaseContext());
                    showMessageError(mensa);
                    //iniciarActividadSiguiente2();
                    // Toast.makeText(Guardar.this, "Comuniquese con el proveedor", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(Registro.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();
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

    private String getStringET(EditText e) {
        return e.getText().toString();
    }

    public void iniciarActividadSiguiente() {
        //Toast.makeText(getContext(), "Se actualizaron los datos", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    public void iniciarActividadSiguiente2() {
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    //Estaciones *************************************************************************************

    public void registrarWebEstaciones() {

        //Creating a string request
        StringRequest stringRequest = new StringRequest(IP_puntos + usuario,
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

    //MENSAJES*******************************************************************************************************
    private void showMessageError(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.not)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(browserIntent);
                    }
                })
                .create()
                .show();
    }

    private void showMessageCorrecto(String message) {
        new AlertDialog.Builder(getContext())
                .setTitle("Correcto")
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.yes)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(browserIntent);
                    }
                })
                .create()
                .show();
    }
}
