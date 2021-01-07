package com.oest.usereucomb.providers.maps;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oest.usereucomb.R;
import com.oest.usereucomb.providers.vales.SectionVales;

import java.util.ArrayList;

public class AdapterEstaciones extends RecyclerView.Adapter<AdapterEstaciones.ViewHolder>{

    private ArrayList<SectionVales> sectionss;

    public AdapterEstaciones(ArrayList<SectionVales> sectionss){
        this.sectionss=sectionss;
    }

    @Override
    public AdapterEstaciones.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_estaciones,parent,false);
        return new AdapterEstaciones.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdapterEstaciones.ViewHolder holder, int position) {
        SectionVales section=sectionss.get(position);
        holder.bind(section);
    }

    @Override
    public int getItemCount() {
        return sectionss!=null?sectionss.size():0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtEstacion;
        private TextView txtPuntos;
        private TextView txtTelefono;
        private TextView txtCorreo;
        private ImageView imgItem;


        public ViewHolder(View itemView) {
            super(itemView);
            //Canjes
            this.txtEstacion=(TextView)itemView.findViewById(R.id.estacion);
            this.txtPuntos=(TextView)itemView.findViewById(R.id.puntos);
            this.txtTelefono=(TextView)itemView.findViewById(R.id.telefono);
            this.txtCorreo=(TextView)itemView.findViewById(R.id.email);
            this.imgItem=(ImageView)itemView.findViewById(R.id.ivProfilePhoto);

        }

        public void bind(final SectionVales section){

            txtEstacion.setText(section.getEstacion());
            txtPuntos.setText(section.getPuntos());
            txtTelefono.setText(section.getTipo());
            txtCorreo.setText(section.getVales());

            String ima = section.getEstacion();
            Log.e("imagenjjj", ima);
            final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/" + ima + ".jpg";

            Glide.with(itemView.getContext()).load(urlfinal).into(imgItem);

        }
    }

}

