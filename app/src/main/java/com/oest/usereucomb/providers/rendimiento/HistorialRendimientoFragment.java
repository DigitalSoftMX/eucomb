package com.oest.usereucomb.providers.rendimiento;


import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.PreferencesRendimiento;

import java.util.ArrayList;

import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;

public class HistorialRendimientoFragment extends Fragment {

    //Views
    private RelativeLayout rl;

    ArrayList<SectionRendimiento> sectionss;
    AdapterRendimiento adapterSection;
    RecyclerView rvSection;

    String membership;
    String dispatcher;
    String ticket;
    TextView titulo;
    String tit;

    ArrayList<SectionRendimiento> listSectionRendimiento;
    String usuario;

    String kls;
    String cargas;
    String fechas;
    String kilometros;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.fragment_list_refresh_rendimiento,null);
        setHasOptionsMenu(true);
        rvSection = rl.findViewById(R.id.list);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);
        /*tit = "HISTORIAL RENDIMIENTO";
        titulo = (TextView) rl.findViewById(R.id.titulo);
        titulo.setText(tit);*/

        listSectionRendimiento = new ArrayList<>();

        rvSection = (RecyclerView) rl.findViewById(R.id.list);
        rvSection.setHasFixedSize(true);

        String countQuery = "SELECT * FROM rendimiento ORDER BY id DESC";
        SQLiteDatabase sqLiteDatabase =
                getActivity().openOrCreateDatabase("rendimiento.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        android.util.Log.e("Count", "numero"+ count);

        if (count > 0){
            Cursor c = sqLiteDatabase.rawQuery("select id, kl, carga, fecha, kilometros from rendimiento ORDER BY id DESC", null);
            if (c.moveToFirst()) {
                //Recorremos el cursor hasta que no haya más registros
                do {
                    Integer id = c.getInt(0);
                    kls = c.getString(1);
                    cargas = c.getString(2);
                    fechas = c.getString(3);
                    kilometros = c.getString(4);
                    android.util.Log.e("id", "movement"+ id);
                    android.util.Log.e("kl", "movement"+ kls);
                    android.util.Log.e("carga", "movement"+ cargas);
                    android.util.Log.e("fecha", "movement"+ fechas);

                    SectionRendimiento sectionss = new SectionRendimiento();

                    sectionss.setId(id);
                    sectionss.setKl(kls);
                    sectionss.setCarga(cargas);
                    sectionss.setFecha(fechas);
                    sectionss.setKilometros(kilometros);

                    listSectionRendimiento.add(sectionss);

                } while(c.moveToNext());
            }
            adapterSection = new AdapterRendimiento(listSectionRendimiento);
            rvSection.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            rvSection.setAdapter(adapterSection);

        } else {
            String mensa = "No hay historial de rendimientos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, getContext());

            //Intent i = new Intent(getContext(), MainActivity.class);
            //startActivity(i);

        }

        return rl;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eliminar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_eliminar:

                String countQuery = "SELECT * FROM rendimiento ORDER BY id DESC";
                SQLiteDatabase sqLiteDatabase = getContext().openOrCreateDatabase("rendimiento.db", MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
                Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);

                sqLiteDatabase.execSQL("DELETE FROM rendimiento");

                //Intent i = new Intent(getContext(),MainActivity.class);
                //startActivity(i);

                PreferencesRendimiento.removePreferenceBoolean(getContext(), PreferencesRendimiento.PREFERENCE_REND);

                String mensa = "Se eliminaron correctamente";
                String imamen = "activo";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
