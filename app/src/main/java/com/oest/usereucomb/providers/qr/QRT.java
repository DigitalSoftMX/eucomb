package com.oest.usereucomb.providers.qr;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRT extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
    String qr_ticket;
    String qr_dispatcher="App";
    String usuario;
    private VolleyRP volley;
    private RequestQueue mRequest;
    ProgressDialog progressDialog;

    private static String URL = "https://eucomb.lealtaddigitalsoft.mx/public/apisticket";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volley = VolleyRP.getInstance(getBaseContext());
        mRequest = volley.getRequestQueue();

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Escanear");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(true);//"additional property"
        integrator.initiateScan();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelado", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getContext(), "Escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
                handleResult(result.getContents());
            }
        } else {
            Toast.makeText(this, "Canceladoss", Toast.LENGTH_LONG).show();
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    //CARGAR QR A LA BD ***********************************************************************

    public void handleResult(String result) {
        progressDialog = ProgressDialog.show(this, "",
                "Espere ............", true);

        final String myResult = result;
        Log.d("QRTicketScanner", result);

        String string = result;
        String part1 = ""+string.charAt(0)+string.charAt(1)+string.charAt(2)+string.charAt(3);
        //Toast.makeText(QRT.this, "empieza con" + part1, Toast.LENGTH_SHORT).show();

        Log.d("QRTicketScannerparte", part1);

        if(part1.equals("?id=")){
            Log.d("QRTicketsinenript", part1);
            registrarWebService(usuario, qr_dispatcher, result);
            progressDialog.dismiss();
        }
        else{
            Log.d("QRTicketconcriptdos", part1);
            //hasConnectivityDos(result.getText());
            progressDialog.dismiss();
            usuario = Preferences.obtenerPreferenceString(this, Preferences.PREFERENCE_USUARIO_LOGIN);
            registrarWebServiceDos(usuario, qr_dispatcher, result);

        }

    }


    public void registrarWebService(final String membership, String dispatcher, final String ticket) {

        Log.d("QRTicketScannerdddddd", ticket);

        //Toast.makeText(this, ticket, Toast.LENGTH_SHORT).show();
        String vals = "ticket";
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("membership", membership);
        hashMapToken.put("dispatcher", dispatcher);
        hashMapToken.put("folio", vals);

        android.util.Log.e("qrsm", "m" + membership);
        android.util.Log.e("qrsd", "m" + dispatcher);
        android.util.Log.e("qrst", "m" + ticket);
        android.util.Log.e("qrsv", "m" + vals);


        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, URL+ticket, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");

                    //Toast.makeText(getBaseContext(), estado, Toast.LENGTH_SHORT).show();
                    android.util.Log.e("respuesta", "estad" + estado);

                    if(estado.equals("Enviar mas tarde exceso de traficos")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo enviar, intentelo mas tarde o comuniquese con el proveedor";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("Enviar mas tarde exceso de trafico")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo enviar, intentelo mas tarde o comuniquese con el proveedor";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("El maximo de puntos acumulados es de 80 por dia")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "El maximo de puntos acumulados es de 80 por dia";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("El folio ya fue utilizado")){
                        //Toast.makeText(Guardar.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "El folio ya fue utilizado";
                        String imamen = "folio";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());

                        showMessageError(mensa);

                    }

                    else if(estado.equals("No se pudo agregar se paso de las 24 horas para ingresar su tiket")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo agregar se paso de las 24 horas para ingresar su tiket";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("Intentelo otro dia solo se permiten un numero limitado")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "Intentelo otro dia solo se permiten un numero limitado";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if(estado.equals("EL folio no pertenece a esta estacion")){
                        //Toast.makeText(getContext(), "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "Comunicate con el proveedor el ticket esta mal impreso";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);

                        //iniciarActividadSiguiente1();
                    }

                    else if (estado.equalsIgnoreCase("La membresia no existe")) {
                        //Toast.makeText(Guardar.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "La membresia no se encuentra activa";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());

                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }
                    else if(estado.equals("La membresia no existe")){
                        //Toast.makeText(Guardar.this, "No se pudo enviar al servidor", Toast.LENGTH_SHORT).show();
                        String mensa = "No se pudo enviar";
                        String imamen = "error";
                        //Mensajes.MyCustomAlertDialogQRconError(mensa,imamen, membership, ticket, getBaseContext());
                        showMessageError(mensa);
                        progressDialog.dismiss();

                        //iniciarActividadSiguiente1();
                    }


                    else {

                       /* if(qr_ticket.equals("")) {
                            String control = "no ejecuta el eliminador de la bd";
                        }
                        else {

                            //elimina el ultimo de la bd
                            SQLiteDatabase sqLiteDatabase =
                                    getContext().openOrCreateDatabase("historial.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
                            //sqLiteDatabase.execSQL("drop table if exists movement");
                            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + HistorialesContract.HistorialesEntry.TABLE_NAME + " ("
                                    + HistorialesContract.HistorialesEntry.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                    + HistorialesContract.HistorialesEntry.qr_membership + " TEXT NOT NULL,"
                                    + HistorialesContract.HistorialesEntry.qr_dispatcher + " TEXT NOT NULL,"
                                    + HistorialesContract.HistorialesEntry.qr_ticket + " TEXT NOT NULL,"
                                    + HistorialesContract.HistorialesEntry.errores + " TEXT NOT NULL,"
                                    + "UNIQUE (" + HistorialesContract.HistorialesEntry.id + "))");

                            String countQuery = "DELETE FROM historial WHERE qr_membership = " + qr_ticket;
                            Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
                            int count = cursor.getCount();
                            cursor.close();
                            android.util.Log.e("Count", "numero" + count);

                        }*/


                        //Toast.makeText(getContext(), "Se enviaron los datos con exito", Toast.LENGTH_SHORT).show();
                        String mensa = "Se enviaron los datos con éxito";
                        String imamen = "active";
                        //Mensajes.MyCustomAlertDialogQR(mensa,imamen, membership, ticket, getBaseContext());
                        //registrarWebPoint();
                        showMessageCorrecto(mensa);
                        //iniciarActividadSiguiente2();
                        //finish();

                    }
                } catch (JSONException e) {
                    //Toast.makeText(getContext(), "No se pudo registrar intententelo mas tarde", Toast.LENGTH_SHORT).show();
                    String mensa = "No se pudo registrar intentelo mas tarde";
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
                String mensa = "No se pudo registrar no tiene datos";
                String imamen = "error";
                //Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getBaseContext());
                showMessageError(mensa);
                //iniciarActividadSiguiente2();

            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, getBaseContext(), volley);
    }

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

                       /* if(qr_ticket.equals("")) {
                            String control = "no ejecuta el eliminador de la bd";
                        }
                        else {

                            //elimina el ultimo de la bd
                            SQLiteDatabase sqLiteDatabase =
                                    getContext().openOrCreateDatabase("historial.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
                            //sqLiteDatabase.execSQL("drop table if exists movement");
                            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + HistorialesContract.HistorialesEntry.TABLE_NAME + " ("
                                    + HistorialesContract.HistorialesEntry.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                    + HistorialesContract.HistorialesEntry.qr_membership + " TEXT NOT NULL,"
                                    + HistorialesContract.HistorialesEntry.qr_dispatcher + " TEXT NOT NULL,"
                                    + HistorialesContract.HistorialesEntry.qr_ticket + " TEXT NOT NULL,"
                                    + HistorialesContract.HistorialesEntry.errores + " TEXT NOT NULL,"
                                    + "UNIQUE (" + HistorialesContract.HistorialesEntry.id + "))");

                            String countQuery = "DELETE FROM historial WHERE qr_membership = " + qr_ticket;
                            Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
                            int count = cursor.getCount();
                            cursor.close();
                            android.util.Log.e("Count", "numero" + count);

                        }*/


                        //Toast.makeText(getContext(), "Se enviaron los datos con exito", Toast.LENGTH_SHORT).show();
                        String mensa = "Se enviaron los datos con exito.";
                        String imamen = "active";
                        //Mensajes.MyCustomAlertDialogQR(mensa,imamen, membership, ticket, getBaseContext());
                        //registrarWebPoint();
                        showMessageCorrecto(mensa);
                        //iniciarActividadSiguiente2();
                        //finish();

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
        VolleyRP.addToQueue(solicitud, mRequest, getBaseContext(), volley);
    }

    private void showMessageError(String message) {
        new AlertDialog.Builder(QRT.this)
                .setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.not)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.stopCameraPreview();
                        Intent browserIntent = new Intent(QRT.this, MainActivity.class);
                        startActivity(browserIntent);
                    }
                })
                .create()
                .show();
    }

    private void showMessageCorrecto(String message) {
        new AlertDialog.Builder(QRT.this)
                .setTitle("Correcto")
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.yes)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        scannerView.stopCameraPreview();
                        Intent browserIntent = new Intent(QRT.this, MainActivity.class);
                        startActivity(browserIntent);
                    }
                })
                .create()
                .show();
    }

}


