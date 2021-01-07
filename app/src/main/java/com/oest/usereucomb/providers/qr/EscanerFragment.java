package com.oest.usereucomb.providers.qr;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.Preferences_Ticket;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.providers.login.ui.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;



public class EscanerFragment extends Fragment {

    private RelativeLayout rl;
    String usuario;
    String qr_ticket;
    Button escanear1;
    ProgressDialog progressDialog;

    //Nuevos escaner
    String qr_dispatcher="App";
    private VolleyRP volley;
    private RequestQueue mRequest;
    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apisticket";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_escanear,null);

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        if(usuario == "") {
            iniciarActividadSiguiente2();
        }

        String imamen = "escanear";
        Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        qr_ticket = Preferences_Ticket.obtenerPreferenceString(getContext(), Preferences_Ticket.PREFERENCE_TICKET);
        escanear1 = (Button) rl.findViewById(R.id.user_escanear);

        escanear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(getContext(), QRT.class);
                startActivity(i);*/
                //IntentIntegrator.forFragment(this).initiateScan();
                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt(" ");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getContext(), "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getContext(), "Escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
                handleResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    //CARGAR QR A LA BD ***********************************************************************

    public void handleResult(String result) {
        progressDialog = ProgressDialog.show(getContext(), "",
                "Espere ............", true);

        final String myResult = result;
        Log.d("QRTicketScanner", result);

        String string = result;
        String part1 = ""+string.charAt(0)+string.charAt(1)+string.charAt(2)+string.charAt(3);
        //Toast.makeText(QRT.this, "empieza con" + part1, Toast.LENGTH_SHORT).show();

        Log.d("QRTicketScannerparte", part1);

            Log.d("QRTicketconcriptdos", part1);
            //hasConnectivityDos(result.getText());
            progressDialog.dismiss();
            usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);
            registrarWebServiceDos(usuario, qr_dispatcher, result);

    }

    //METODOS POST**************************************************************************************

    public void registrarWebServiceDos(final String membership, String dispatcher, final String ticket) {

        // Toast.makeText(Registro.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        // hashMapToken.put("ticket", ticket);
        hashMapToken.put("membership", membership);
        hashMapToken.put("dispatcher", dispatcher);
        hashMapToken.put("folio", ticket);

        android.util.Log.e("qrsm", "m" + membership);
        android.util.Log.e("qrsd", "m" + dispatcher);
        android.util.Log.e("qrst", "m" + ticket);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
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
                //Toast.makeText(getContext(), "No se pudo registrar no tiene datos", Toast.LENGTH_SHORT).show();
                String mensa = "No se pudo registrar no tiene datos.";
                String imamen = "error";
                //Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getBaseContext());
                showMessageError(mensa);
                //iniciarActividadSiguiente2();

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, getContext(), volley);
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

    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }


}

