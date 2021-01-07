package com.oest.usereucomb.providers.rendimiento;


import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.PreferencesRendimiento;
import com.oest.usereucomb.billing.PreferencesRendimientoL;
import com.oest.usereucomb.billing.RendimientoContract;
import com.oest.usereucomb.billing.VolleyRP;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;
import static okhttp3.internal.Util.UTC;

public class RendimientoFragment extends Fragment {

    private RelativeLayout rl;

    private EditText kl;
    private EditText carga;
    private String usuario;
    private String fecha;
    private PieChart pieChart;

    private Button register;

    private VolleyRP volley;
    private RequestQueue mRequest;
    Calendar calendar = Calendar.getInstance(Locale.US);
    String kls;
    String cargas;
    String fechas;
    String kilometros;
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_rendimiento,null);
        setHasOptionsMenu(true);

        calendar.setTimeZone(UTC);

        calendar.set(Calendar.HOUR, 17);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 2);
        //System.out.println(simpleDateFormat.format(calendar.getTime()));
        //fecha = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();

        fecha = dateFormat.format(date);

        android.util.Log.e("idfecha", "fechassdd" + fecha);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        String tit = "RENDIMIENTO";
        TextView titulo = (TextView) rl.findViewById(R.id.titulo);
        titulo.setText(tit);

        String imamen = "rendimiento";
        Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        volley = VolleyRP.getInstance(getContext());
        mRequest = volley.getRequestQueue();

        kl = (EditText) rl.findViewById(R.id.kl);
        carga = (EditText) rl.findViewById(R.id.carga);
        register = (Button) rl.findViewById(R.id.user_sign_in_button);

        SQLiteDatabase sqLiteDatabase =
                getContext().openOrCreateDatabase("rendimiento.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + RendimientoContract.RendimientoEntry.TABLE_NAME + " ("
                + RendimientoContract.RendimientoEntry.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RendimientoContract.RendimientoEntry.kl + " TEXT NOT NULL,"
                + RendimientoContract.RendimientoEntry.carga + " TEXT NOT NULL,"
                + RendimientoContract.RendimientoEntry.fecha + " TEXT NOT NULL,"
                + RendimientoContract.RendimientoEntry.kilometros + " TEXT NOT NULL,"
                + "UNIQUE (" + RendimientoContract.RendimientoEntry.id + "))");


        String countQuery = "SELECT * FROM rendimiento ORDER BY id DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        android.util.Log.e("Count", "numero"+ count);

        if (count > 0){
            Cursor c = sqLiteDatabase.rawQuery("select id, kl, carga, fecha, kilometros, MAX(id) from rendimiento ORDER BY id DESC", null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    String id= c.getString(0);
                     kls = c.getString(1);
                     cargas = c.getString(2);
                     fechas = c.getString(3);
                     kilometros = c.getString(4);
                    android.util.Log.e("id", "movement"+ id);
                    android.util.Log.e("kl", "movement"+ kls);
                    android.util.Log.e("carga", "movement"+ cargas);
                    android.util.Log.e("fecha", "movement"+ fechas);
                    android.util.Log.e("kilometros", "movement"+ kilometros);
                } while(c.moveToNext());
            }
               Grafic();
        }
        else{
              GraficSinDatos();
        }




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enviar();
                //Log.d("fecaas", simpleDateFormat.format(calendar.getTime()));

            }
        });


        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void enviar() {

        String nom = kl.getText().toString();
        String car = carga.getText().toString();

        String cadenaModificada1 = nom.trim();
        String cadenaModificada1s = car.trim();

        if (!nom.equals("")) {
            //Toast.makeText(getContext(), qr_dispatcher, Toast.LENGTH_SHORT).show();

            SQLiteDatabase sqLiteDatabase =
                    getContext().openOrCreateDatabase("rendimiento.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);

            //sqLiteDatabase.execSQL("drop table if exists movement");

            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + RendimientoContract.RendimientoEntry.TABLE_NAME + " ("
                    + RendimientoContract.RendimientoEntry.id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + RendimientoContract.RendimientoEntry.kl + " TEXT NOT NULL,"
                    + RendimientoContract.RendimientoEntry.carga + " TEXT NOT NULL,"
                    + RendimientoContract.RendimientoEntry.fecha + " TEXT NOT NULL,"
                    + RendimientoContract.RendimientoEntry.kilometros + " TEXT NOT NULL,"
                    + "UNIQUE (" + RendimientoContract.RendimientoEntry.id + "))");

            // Contenedor de valores
            ContentValues values = new ContentValues();

            values.put(RendimientoContract.RendimientoEntry.kl, cadenaModificada1);
            values.put(RendimientoContract.RendimientoEntry.carga, cadenaModificada1s);
            values.put(RendimientoContract.RendimientoEntry.fecha, fecha);

            String change = PreferencesRendimiento.obtenerPreferenceString(getContext(), PreferencesRendimiento.PREFERENCE_REND);
            if(change.equals("")){
                String kill = "0";
                //Kilometros
                PreferencesRendimiento.savePreferenceBoolean(getContext(),true,PreferencesRendimiento.PREFERENCE_ESTADO_BUTTON_SESION_REND);
                PreferencesRendimiento.savePreferenceString(getContext(),cadenaModificada1,PreferencesRendimiento.PREFERENCE_REND);
                //Litros
                PreferencesRendimientoL.savePreferenceBoolean(getContext(),true,PreferencesRendimientoL.PREFERENCE_ESTADO_BUTTON_SESION_RENDL);
                PreferencesRendimientoL.savePreferenceString(getContext(),cadenaModificada1s,PreferencesRendimientoL.PREFERENCE_RENDL);
                GraficPrimeraVes(cadenaModificada1, cadenaModificada1s, fecha, kill);
                values.put(RendimientoContract.RendimientoEntry.kilometros, kill);
                kl.setText("");
                carga.setText("");

            }
            else{
                String kilos = PreferencesRendimiento.obtenerPreferenceString(getContext(), PreferencesRendimiento.PREFERENCE_REND);
                String litros = PreferencesRendimientoL.obtenerPreferenceString(getContext(), PreferencesRendimientoL.PREFERENCE_RENDL);


                float changekilo = Float.parseFloat(kilos); //kilometros anteriores
                float changelitr = Float.parseFloat(litros); //carga anteriores
                float chanenom = Float.parseFloat(cadenaModificada1); //kilometros dados

                float restaKl =  (chanenom - changekilo);
                float resta = (restaKl / changelitr);
                String guardarresta = Float.toString(resta);

                //Toast.makeText(getContext(), "Puntos " + guardarresta, Toast.LENGTH_SHORT).show();

                //Kilimetros
                PreferencesRendimiento.removePreferenceBoolean(getContext(), PreferencesRendimiento.PREFERENCE_REND);
                PreferencesRendimiento.savePreferenceBoolean(getContext(),true,PreferencesRendimiento.PREFERENCE_ESTADO_BUTTON_SESION_REND);
                PreferencesRendimiento.savePreferenceString(getContext(),cadenaModificada1,PreferencesRendimiento.PREFERENCE_REND);
                //Litros
                PreferencesRendimientoL.removePreferenceBoolean(getContext(), PreferencesRendimientoL.PREFERENCE_RENDL);
                PreferencesRendimientoL.savePreferenceBoolean(getContext(),true,PreferencesRendimientoL.PREFERENCE_ESTADO_BUTTON_SESION_RENDL);
                PreferencesRendimientoL.savePreferenceString(getContext(),cadenaModificada1s,PreferencesRendimientoL.PREFERENCE_RENDL);
                GraficPrimeraVes(cadenaModificada1, cadenaModificada1s, fecha, guardarresta);
                values.put(RendimientoContract.RendimientoEntry.kilometros, guardarresta);
                kl.setText("");
                carga.setText("");

                // GraficPrimeraVes(nom, car, fecha, kilometros);
            }


            // Insertar...
            sqLiteDatabase.insert(RendimientoContract.RendimientoEntry.TABLE_NAME, null, values);

            getProfilesCount();
            sqLiteDatabase.close();

            String mensa = "El rendimiento se guardó con éxito";
            String imamen = "active";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            //Intent i = new Intent(getContext(), MainActivity.class);
            //startActivity(i);

        } else {
            //Toast.makeText(getContext(), "Falta QR de capturar", Toast.LENGTH_SHORT).show();
            String mensa = "Por favor llene todos los campos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

        }

    }


    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM rendimiento";
        SQLiteDatabase sqLiteDatabase =
                getContext().openOrCreateDatabase("rendimiento.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        android.util.Log.e("Count", "numero" + count);

        if (count > 0) {
            Cursor c = sqLiteDatabase.rawQuery("select id, kl, carga, fecha from rendimiento", null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    String id = c.getString(0);
                    String kl = c.getString(1);
                    String carga = c.getString(2);
                    String fecha = c.getString(3);
                    android.util.Log.e("id", "rendimiento" + id);
                    android.util.Log.e("kl", "rendimiento" + kl);
                    android.util.Log.e("carga", "rendimiento" + carga);
                    android.util.Log.e("fecha", "fecha" + fecha);
                } while (c.moveToNext());
            }
        }
        return count;
    }

  /*  private SQLiteDatabase openOrCreateDatabase(String s, int modeEnableWriteAheadLogging, Object o) {
        return null;
    }*/


    public void Grafic(){

        //CHART--------------------------------------------------------------------------------------------------------
        pieChart = (PieChart) rl.findViewById(R.id.pieChart);

        /*definimos algunos atributos*/
        pieChart.setHoleRadius(10f);
        pieChart.setRotationEnabled(true);
        pieChart.animateXY(1500, 1500);
        //pieChart.setCenterTextSize(20);
        pieChart.setCenterTextSize(23f);

        String cadenaModificada1 = kls.trim();
        String cadenaModificada1s = cargas.trim();
        float newkl = Float.parseFloat(cadenaModificada1);
        float newcarga = Float.parseFloat(cadenaModificada1s);
        /*creamos una lista para los valores Y*/
        ArrayList<Entry> valsY = new ArrayList<Entry>();
        valsY.add(new Entry(newkl,0));
        valsY.add(new Entry(newcarga,1));

        /*creamos una lista para los valores X*/
        ArrayList<String> valsX = new ArrayList<String>();
        valsX.add("Kilómetros");
        valsX.add("Carga" );

        /*creamos una lista de colores*/
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.myPrimaryColor));
        colors.add(getResources().getColor(R.color.light_gray));

        /*seteamos los valores de Y y los colores*/
        PieDataSet set1 = new PieDataSet(valsY, "Fecha: " + fechas);
        set1.setSliceSpace(3f);
        set1.setColors(colors);
        set1.setValueTextSize(12f);

        /*seteamos los valores de X*/
        PieData data = new PieData(valsX, set1);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

        //String resta = PreferencesRendimiento.obtenerPreferenceString(getContext(), PreferencesRendimiento.PREFERENCE_REND);
        /*Ocutar descripcion*/
        pieChart.setDescription(kilometros + " Km X Litro");

    }

    public void GraficSinDatos(){


        //CHART--------------------------------------------------------------------------------------------------------
        pieChart = (PieChart) rl.findViewById(R.id.pieChart);

        /*definimos algunos atributos*/
        pieChart.setHoleRadius(10f);
        pieChart.setRotationEnabled(true);
        pieChart.animateXY(1500, 1500);
        pieChart.setCenterTextSize(19f);

        /*creamos una lista para los valores Y*/
        ArrayList<Entry> valsY = new ArrayList<Entry>();
        valsY.add(new Entry(0,0));
        valsY.add(new Entry(0,1));

        /*creamos una lista para los valores X*/
        ArrayList<String> valsX = new ArrayList<String>();
        valsX.add("Kilómetros");
        valsX.add("Carga");

        /*creamos una lista de colores*/
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.myPrimaryColor));
        colors.add(getResources().getColor(R.color.light_gray));

        /*seteamos los valores de Y y los colores*/
        PieDataSet set1 = new PieDataSet(valsY, "Fecha: ");
        set1.setSliceSpace(3f);
        set1.setColors(colors);
        set1.setValueTextSize(22f);

        /*seteamos los valores de X*/
        PieData data = new PieData(valsX, set1);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

        /*Ocutar descripcion*/
        pieChart.setDescription("");
        /*Ocultar leyenda*/

    }

    public void GraficPrimeraVes(String nom, String car, String fechass, String kill){

        //CHART--------------------------------------------------------------------------------------------------------
        pieChart = (PieChart) rl.findViewById(R.id.pieChart);

        /*definimos algunos atributos*/
        pieChart.setHoleRadius(10f);
        pieChart.setRotationEnabled(true);
        pieChart.animateXY(1500, 1500);
        pieChart.setCenterTextSize(19f);

        String cadenaModificada1 = nom.trim();
        String cadenaModificada1s = car.trim();
        float newkl = Float.parseFloat(cadenaModificada1);
        float newcarga = Float.parseFloat(cadenaModificada1s);
        /*creamos una lista para los valores Y*/
        ArrayList<Entry> valsY = new ArrayList<Entry>();
        valsY.add(new Entry(newkl,0));
        valsY.add(new Entry(newcarga,1));

        /*creamos una lista para los valores X*/
        ArrayList<String> valsX = new ArrayList<String>();
        valsX.add("Kilómetros");
        valsX.add("Carga");

        /*creamos una lista de colores*/
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.myPrimaryColor));
        colors.add(getResources().getColor(R.color.light_gray));

        /*seteamos los valores de Y y los colores*/
        PieDataSet set1 = new PieDataSet(valsY, "Fecha: " + fechass);
        set1.setSliceSpace(3f);
        set1.setColors(colors);
        set1.setValueTextSize(22f);

        /*seteamos los valores de X*/
        PieData data = new PieData(valsX, set1);
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();

        /*Ocutar descripcion*/
        pieChart.setDescription(kill + " Km X litros");
        /*Ocultar leyenda*/
    }


}


