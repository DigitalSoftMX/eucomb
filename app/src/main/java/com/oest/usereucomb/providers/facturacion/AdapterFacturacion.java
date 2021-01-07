package com.oest.usereucomb.providers.facturacion;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oest.usereucomb.R;

import java.util.ArrayList;


public class AdapterFacturacion extends RecyclerView.Adapter<AdapterFacturacion.ViewHolder>{

    private ArrayList<SectionFacturacion> sectionss;

    public AdapterFacturacion(ArrayList<SectionFacturacion> sectionss){
        this.sectionss=sectionss;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_facturacion,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SectionFacturacion section=sectionss.get(position);
        holder.bind(section);
    }

    @Override
    public int getItemCount() {
        return sectionss!=null?sectionss.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtPuntos;
        private TextView txtEstacion;
        private TextView txtConcepto;
        private TextView txtFecha;
        private TextView txtFolio;
        private TextView txtArchivo;
        private Button send;


        public ViewHolder(View itemView) {
            super(itemView);
            //Canjes
            this.txtPuntos=(TextView)itemView.findViewById(R.id.puntos);
            this.txtEstacion=(TextView)itemView.findViewById(R.id.estacion);
            this.txtConcepto=(TextView)itemView.findViewById(R.id.concepto);
            this.txtFecha=(TextView)itemView.findViewById(R.id.fecha);
            this.txtFolio=(TextView)itemView.findViewById(R.id.folio);
            this.send=(Button) itemView.findViewById(R.id.user_sign_in_button);
            //this.txtArchivo=(TextView)itemView.findViewById(R.id.folio);

        }

        public void bind(final SectionFacturacion section){

            txtPuntos.setText(section.getPuntos());
            txtEstacion.setText(section.getEstacion());
            txtConcepto.setText(section.getConcepto());
            txtFecha.setText(section.getTodate_certificado());
            txtFolio.setText(section.getFolio());
            //txtArchivo.setText(section.getValor());

            /*String ima = section.getEstacion();
            Log.e("imagenjjj", ima);
            final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/storage/app/public/" + ima + ".jpg";*/

            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //  Toast.makeText(itemView.getContext(), "salio" + section.getValor() ,Toast.LENGTH_LONG).show();
                    Log.e("descpdf", section.getValor());
                    Log.e("descpdffol", section.getFolio());
                    String archpdf = section.getValor();
                    String foli = section.getFolio();
                    String u = "https://eucomb.lealtaddigitalsoft.mx/storage/app/public/factura/"+ foli +"/" + archpdf;
                    Log.e("descpdfurl", u);

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(u));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("Descargando");
                request.setDescription("Factura");

                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+ System.currentTimeMillis());

                DownloadManager manager =(DownloadManager) itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                }
            });

        }

    }

}
