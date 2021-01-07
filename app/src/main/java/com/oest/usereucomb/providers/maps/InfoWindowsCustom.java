package com.oest.usereucomb.providers.maps;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.oest.usereucomb.R;

public class InfoWindowsCustom implements GoogleMap.InfoWindowAdapter {
    Context context;
    LayoutInflater inflater;

    public InfoWindowsCustom(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // R.layout.echo_info_window is a layout in my
        // res/layout folder. You can provide your own
        View v = inflater.inflate(R.layout.infowindow_layout, null);

        TextView title = (TextView) v.findViewById(R.id.info_window_nombre);
        TextView subtitle = (TextView) v.findViewById(R.id.info_window_placas);
        ImageView img = v.findViewById(R.id.info_window_imagen);
        ImageView imgT = v.findViewById(R.id.info_window_ticket);

        title.setText(marker.getTitle());
        subtitle.setText(marker.getSnippet());

        String im="";
        String imticket="";

        Log.d("unadjjss", marker.getTitle());

        //azul
        if(marker.getTitle().equals("Servicio Cuautlapan Doncellas")){ im="doncellas"; imticket = "";}
        else if(marker.getTitle().equals("Energéticos de Cordoba Matriz")){ im="cordoba"; imticket = "";}
        else if(marker.getTitle().equals("Combustibles y Servicios Esmeralda")){ im="esmeralda"; imticket = "";}
        else if(marker.getTitle().equals("Servicio Cuautlapan Chapulco")){ im="chapulco"; }
        else if(marker.getTitle().equals("Energéticos Santa María del Monte")){ im="mariamonte"; imticket = "";}
        //rojo
        else if(marker.getTitle().equals("Energéticos de Cordoba Nogales")){ im="nogales"; imticket = "";}
        else if(marker.getTitle().equals("Servicio Cuautlapan Matriz")){ im="matriz"; imticket = "";}
        //verde
        else if(marker.getTitle().equals("Servicio Cuatlapan Tehuacan")){ im="ete"; imticket = "eticketa";}
        else if(marker.getTitle().equals("Gasolinería Ele")){ im="ele"; imticket = "eticketa"; }
        else if(marker.getTitle().equals("Energéticos Solé")){ im="sole"; imticket = "eticketa"; }
        else if(marker.getTitle().equals("Gasolinera Zavaleta")){ im="zavaleta"; imticket = "eticketa"; }
        else if(marker.getTitle().equals("Litro Exacto")){ im="litroexacto"; imticket = "eticketa"; }
        //amarillo
        else if(marker.getTitle().equals("Energéticos Ahuacatlan")){ im="ahuacatlan"; imticket = "";}
        else if(marker.getTitle().equals("Servicio Alfa Bravo Coca")){ im="bravo"; imticket = "";}


        int imageId = context.getResources().getIdentifier(im.toLowerCase(),
                "drawable", context.getPackageName());
        img.setImageResource(imageId);

        int imageIdT = context.getResources().getIdentifier(imticket.toLowerCase(),
                "drawable", context.getPackageName());
        imgT.setImageResource(imageIdT);

        Log.d("unadjj", String.valueOf(imageId));

        String phoneNo = marker.getTitle();
        if(!"".equals(phoneNo)) {
            String dial = "tel:" + phoneNo;
            //startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));
            Log.d("nummero", "paso");
        }else {
            //    Toast.makeText(getBase, "Enter a phone number", Toast.LENGTH_SHORT).show();
            Log.d("nummero", "no hay telefono");
        }

        return v;
    }




}