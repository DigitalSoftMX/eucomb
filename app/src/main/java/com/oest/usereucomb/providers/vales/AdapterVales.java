package com.oest.usereucomb.providers.vales;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oest.usereucomb.R;
import com.oest.usereucomb.providers.vales.ui.Confirmacion;

import java.util.ArrayList;

    public class AdapterVales extends RecyclerView.Adapter<AdapterVales.ViewHolder>{

        private ArrayList<SectionVales> sectionss;

        public AdapterVales(ArrayList<SectionVales> sectionss){
            this.sectionss=sectionss;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vales,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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
            //private TextView txtFecha;
            private TextView txtVales;
            private ImageView imgItem;


            public ViewHolder(View itemView) {
                super(itemView);
                //Canjes
                this.txtEstacion=(TextView)itemView.findViewById(R.id.estacion);
                this.txtPuntos=(TextView)itemView.findViewById(R.id.puntos);
                //this.txtFecha=(TextView)itemView.findViewById(R.id.fecha);
                //this.txtVales=(TextView)itemView.findViewById(R.id.vale);
                this.imgItem=(ImageView)itemView.findViewById(R.id.ivProfilePhoto);

            }

            public void bind(final SectionVales section){

                txtEstacion.setText(section.getEstacion());
                txtPuntos.setText(section.getPuntos());
                //txtFecha.setText(section.getTodate_certificado());
                //txtVales.setText(section.getVales());

                String ima = section.getEstacion();
                Log.e("imagenjjj", ima);
                final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/" + ima + ".jpg";

                Glide.with(itemView.getContext()).load(urlfinal).into(imgItem);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Toast.makeText(itemView.getContext(), "salio" + section.getId() ,Toast.LENGTH_LONG).show();

                        Context context = itemView.getContext();
                        Intent list = new Intent(itemView.getContext(), Confirmacion.class);
                        list.putExtra("numero", section.getId());
                        list.putExtra("estacion", section.getEstacion());
                        list.putExtra("puntos", section.getPuntos());
                        //list.putExtra("fecha", section.getTodate_certificado());
                        //list.putExtra("vales", section.getVales());
                        context.startActivity(list);
                    }
                });

            }
        }

    }
