package com.oest.usereucomb.providers.login.ui;

import android.app.ProgressDialog;
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
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DatosFacturaFragment extends Fragment {

    private RelativeLayout rl;
    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/apidatosfacturaupdate?";
    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/apidatosfactura?username=";
    private String usuario;
    private Button register;

    private EditText rfc;
    private EditText razonsocial;
    Spinner usocfdi;
    private EditText emailfiscal;
    private EditText calle;
    private EditText numext;
    private EditText numint;
    private EditText colonia;
    private EditText ciudad;
    private EditText municipio;
    private EditText estat;

    private VolleyRP volley;
    private RequestQueue mRequest;
    ProgressDialog progressDialog;

    String namemes [] = {"G01","G02","G03"};
    ArrayAdapter<String> meses;
    String envMes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_datosfacturacion,null);
        setHasOptionsMenu(true);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);
        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        rfc = (EditText) rl.findViewById(R.id.rfc);
        razonsocial = (EditText) rl.findViewById(R.id.razonsocial);
        usocfdi = (Spinner) rl.findViewById(R.id.usocfdi);
        emailfiscal = (EditText) rl.findViewById(R.id.emailfiscal);

        /*calle = (EditText) rl.findViewById(R.id.calle);
        numext = (EditText) rl.findViewById(R.id.numext);
        numint = (EditText) rl.findViewById(R.id.numint);
        colonia = (EditText) rl.findViewById(R.id.colonia);*/
        //estat = (EditText) rl.findViewById(R.id.estado);
        //ciudad = (EditText) rl.findViewById(R.id.ciudad);
        //municipio = (EditText) rl.findViewById(R.id.municipio);
        register = (Button) rl.findViewById(R.id.user_sign_in_button);

        meses = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,namemes);
        usocfdi.setAdapter(meses);

        usocfdi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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


        if(Preferences.obtenerPreferenceBoolean(getContext(),Preferences.PREFERENCE_ESTADO_BUTTON_SESION)) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Espere..."); // Setting Message
            progressDialog.setTitle("Puede tardar un momento"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);

            SolicitudJSON(IP + usuario);
        }

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


        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void VerificarRegistro() {

        String rfcs = rfc.getText().toString();
        String razon = razonsocial.getText().toString();
        String emafi = emailfiscal.getText().toString();
        /*String call = calle.getText().toString();
        String nuex = numext.getText().toString();
        String nuint = numint.getText().toString();
        String col = colonia.getText().toString();*/
        //String est = estat.getText().toString();
        //String ciu = ciudad.getText().toString();
        //String mun = municipio.getText().toString();

        if (TextUtils.isEmpty(rfcs)) {
            rfc.setError(getString(R.string.error_field_required));
            rfc.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(razon)) {
            razonsocial.setError(getString(R.string.error_field_required));
            razonsocial.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(emafi)) {
            emailfiscal.setError(getString(R.string.error_field_required));
            emailfiscal.requestFocus();
            return;
        }
        /*if (TextUtils.isEmpty(call)) {
            calle.setError(getString(R.string.error_field_required));
            calle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nuex)) {
            numext.setError(getString(R.string.error_field_required));
            numext.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nuint)) {
            numint.setError(getString(R.string.error_field_required));
            numint.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(col)) {
            colonia.setError(getString(R.string.error_field_required));
            colonia.requestFocus();
            return;
        }*/
       /* if (TextUtils.isEmpty(est)) {
            estat.setError(getString(R.string.error_field_required));
            estat.requestFocus();
            return;
        }*/
        /*if (TextUtils.isEmpty(ciu)) {
            ciudad.setError(getString(R.string.error_field_required));
            ciudad.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mun)) {
            municipio.setError(getString(R.string.error_field_required));
            municipio.requestFocus();
            return;
        }*/
            registrarWebService(
                    getStringET(rfc).trim(),
                    getStringET(razonsocial).trim(),
                    envMes,
                    getStringET(emailfiscal).trim()
                    /*getStringET(calle).trim(),
                    getStringET(numext).trim(),
                    getStringET(numint).trim(),
                    getStringET(colonia).trim(),
                    getStringET(estat).trim(),
                    getStringET(ciudad).trim(),
                    getStringET(municipio).trim()*/
            );
    }

    //public void registrarWebService(String rfc, String razonsocial, String calle, String numext, String numint, String colonia, String estat, String ciudad, String municipio) {
    public void registrarWebService(String rfc, String razonsocial, String usocfdi, String emailfiscal) {

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        progressDialog = ProgressDialog.show(getContext(), "",
                "Espere ............", true);

            HashMap<String, Object> hashMapToken = new HashMap<>();
            hashMapToken.put("username", usuario);
            hashMapToken.put("rfc", rfc);
            hashMapToken.put("nombre", razonsocial);
            hashMapToken.put("usocfdi", usocfdi);
            hashMapToken.put("emailfiscal", emailfiscal);

            /*hashMapToken.put("calle", calle);
            hashMapToken.put("numero_ext", numext);
            hashMapToken.put("numero_int", numint);
            hashMapToken.put("colonia", colonia);
            hashMapToken.put("estado", estat);
            hashMapToken.put("ciudad", ciudad);
            hashMapToken.put("municipio", municipio);
            hashMapToken.put("username", usuario);*/

            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP_Registrar, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject datos) {
                    try {
                        String estado = datos.getString("resultado");

                        if (estado.equalsIgnoreCase("La informacion se actualizo correctamente")) {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            iniciarActividadSiguiente();
                            progressDialog.dismiss();

                        } else {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(estado,imamen, getContext());
                            progressDialog.dismiss();

                        }
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

    private String getStringET(EditText e){
        return e.getText().toString();
    }

    public void iniciarActividadSiguiente(){
        //Toast.makeText(getContext(), "Se actualizaron los datos", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(),MainActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(),LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

    //consultar registro*******************************************************************************
    public void SolicitudJSON(String URL){
        //Toast.makeText(getContext(),URL, Toast.LENGTH_SHORT).show();

        JsonObjectRequest solicitud = new JsonObjectRequest(URL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                VerificarDatos(datos);
                progressDialog.dismiss();

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                String mensa = "Ocurrio un error";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                progressDialog.dismiss();

            }

        });
        VolleyRP.addToQueue(solicitud, mRequest,getContext(), volley);
    }

    public void VerificarDatos(JSONObject datos) {

        try {

            String estados = datos.getString("resultado");
            //Toast.makeText(getContext(), estados, Toast.LENGTH_SHORT).show();

            if(estados.equals("error")) {
                //Toast.makeText(getContext(), "El usuario no existe:", Toast.LENGTH_LONG).show();
                String mensa = "Los datos de facturación no se han llenado";
                String imamen = "error";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
                progressDialog.dismiss();

            }
            else{
                JSONObject Jsondatos = new JSONObject(datos.getString("resultado"));

                String rfcs = Jsondatos.getString("rfc");
                String razon = Jsondatos.getString("nombre");
                String usocf = Jsondatos.getString("usocfdi");
                String emafi = Jsondatos.getString("emailfiscal");

                /*String cal = Jsondatos.getString("calle");
                String nue = Jsondatos.getString("numero_ext");
                String nui = Jsondatos.getString("numero_int");
                String col = Jsondatos.getString("colonia");
                String est = Jsondatos.getString("estado");
                String ciu = Jsondatos.getString("ciudad");
                String mun = Jsondatos.getString("municipio");*/

                rfc.setText(rfcs);
                razonsocial.setText(razon);
                usocfdi.setSelection(meses.getPosition(usocf));
                emailfiscal.setText(emafi);

                /*calle.setText(cal);
                numext.setText(nue);
                numint.setText(nui);
                colonia.setText(col);
                estat.setText(est);
                ciudad.setText(ciu);
                municipio.setText(mun);*/

                progressDialog.dismiss();

            }
        } catch (JSONException e) {
            //Toast.makeText(getContext(), "Error"+e, Toast.LENGTH_LONG).show();
            String mensa = "Error";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());
            progressDialog.dismiss();

        }
    }
}

