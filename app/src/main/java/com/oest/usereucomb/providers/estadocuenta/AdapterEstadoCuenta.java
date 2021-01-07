package com.oest.usereucomb.providers.estadocuenta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oest.usereucomb.R;

import java.util.ArrayList;


public class AdapterEstadoCuenta extends RecyclerView.Adapter<AdapterEstadoCuenta.ViewHolder>{

    private ArrayList<SectionEstadoCuenta> sectionss;

    public AdapterEstadoCuenta(ArrayList<SectionEstadoCuenta> sectionss){
        this.sectionss=sectionss;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_estadocuenta,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SectionEstadoCuenta section=sectionss.get(position);
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


        public ViewHolder(View itemView) {
            super(itemView);
            //Canjes
            this.txtPuntos=(TextView)itemView.findViewById(R.id.puntos);
            this.txtEstacion=(TextView)itemView.findViewById(R.id.estacion);
            this.txtConcepto=(TextView)itemView.findViewById(R.id.concepto);
            this.txtFecha=(TextView)itemView.findViewById(R.id.fecha);
            this.txtFolio=(TextView)itemView.findViewById(R.id.folio);

        }

        public void bind(final SectionEstadoCuenta section){

            txtPuntos.setText(section.getPuntos());
            txtEstacion.setText(section.getEstacion());
            txtConcepto.setText(section.getConcepto());
            txtFecha.setText(section.getTodate_certificado());
            txtFolio.setText(section.getFolio());

        }
    }

}
