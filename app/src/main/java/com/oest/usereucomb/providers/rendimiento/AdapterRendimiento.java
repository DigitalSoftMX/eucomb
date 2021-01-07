package com.oest.usereucomb.providers.rendimiento;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.oest.usereucomb.R;
import java.util.ArrayList;

public class AdapterRendimiento extends RecyclerView.Adapter<AdapterRendimiento.ViewHolder>{

    private ArrayList<SectionRendimiento> sectionss;

    AdapterRendimiento(ArrayList<SectionRendimiento> sectionss){
        this.sectionss=sectionss;
    }

    @Override
    public AdapterRendimiento.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_historial_rendimiento,parent,false);
        return new AdapterRendimiento.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterRendimiento.ViewHolder holder, int position) {
        SectionRendimiento sectionRendimiento=sectionss.get(position);
        holder.bind(sectionRendimiento);
    }

    @Override
    public int getItemCount() {
        return sectionss!=null?sectionss.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView kl;
        private TextView carga;
        private TextView fecha;
        private TextView kilometros;

        public ViewHolder(View itemView) {
            super(itemView);
            this.kl=(TextView)itemView.findViewById(R.id.kl);
            this.carga=(TextView)itemView.findViewById(R.id.carga);
            this.fecha=(TextView)itemView.findViewById(R.id.fecha);
            this.kilometros=(TextView)itemView.findViewById(R.id.kilometros);
        }

        public void bind(final SectionRendimiento section){

            kl.setText(section.getKl());
            carga.setText(section.getCarga());
            fecha.setText(section.getFecha());
            kilometros.setText(section.getKilometros());

        }
    }

}

