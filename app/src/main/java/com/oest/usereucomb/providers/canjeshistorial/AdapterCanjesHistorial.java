package com.oest.usereucomb.providers.canjeshistorial;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oest.usereucomb.R;

import java.util.ArrayList;

    public class AdapterCanjesHistorial extends RecyclerView.Adapter<AdapterCanjesHistorial.ViewHolder>{

        private ArrayList<SectionCanjesHistorial> sectionss;

        public AdapterCanjesHistorial(ArrayList<SectionCanjesHistorial> sectionss){
            this.sectionss=sectionss;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_canjeshistorial,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SectionCanjesHistorial section=sectionss.get(position);
            holder.bind(section);
        }

        @Override
        public int getItemCount() {
            return sectionss!=null?sectionss.size():0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtMembresia;
            private TextView txtTipo;
            private TextView txtPuntos;
            private TextView txtValor;
            private TextView txtFecha;
            private ImageView imgItem;

            public ViewHolder(View itemView) {
                super(itemView);
                //this.txtMembresia=(TextView)itemView.findViewById(R.id.membresia);
                this.txtTipo=(TextView)itemView.findViewById(R.id.tipo);
                this.txtPuntos=(TextView)itemView.findViewById(R.id.puntos);
                //this.txtValor=(TextView)itemView.findViewById(R.id.valor);
                this.txtFecha=(TextView)itemView.findViewById(R.id.fecha);
                this.imgItem=(ImageView)itemView.findViewById(R.id.ivProfilePhoto);

            }

            public void bind(final SectionCanjesHistorial section){

                txtMembresia.setText(section.getMembresia());
                txtTipo.setText(section.getTipo());
                txtPuntos.setText(section.getPuntos());
                txtValor.setText(section.getValor());
                txtFecha.setText(section.getTodate_certificado());

                String ima = section.getTipo();
                Log.e("imagenjjj", ima);
                final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/" + ima + ".jpg";

                Glide.with(itemView.getContext()).load(urlfinal).into(imgItem);

            }
        }

    }
