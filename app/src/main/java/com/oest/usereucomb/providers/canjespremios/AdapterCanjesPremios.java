package com.oest.usereucomb.providers.canjespremios;

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
import com.oest.usereucomb.providers.canjes.ui.ValeQR;

import java.util.ArrayList;

    public class AdapterCanjesPremios extends RecyclerView.Adapter<AdapterCanjesPremios.ViewHolder>{

        private ArrayList<SectionCanjesPremios> sectionss;

        public AdapterCanjesPremios(ArrayList<SectionCanjesPremios> sectionss){
            this.sectionss=sectionss;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_canjes,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SectionCanjesPremios section=sectionss.get(position);
            holder.bind(section);
        }

        @Override
        public int getItemCount() {
            return sectionss!=null?sectionss.size():0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtFolio;
            private TextView txtNumero;
            private TextView txtLugar;
            private TextView txtPuntos;
            private TextView txtValor;
            private TextView txtEstatus;
            private TextView txtFecha;
            private ImageView imgItem;

            public ViewHolder(View itemView) {
                super(itemView);
                //Canjes
                //this.txtFolio=(TextView)itemView.findViewById(R.id.folio);
                this.txtNumero=(TextView)itemView.findViewById(R.id.numero);
                this.txtLugar=(TextView)itemView.findViewById(R.id.lugar);
                //this.txtPuntos=(TextView)itemView.findViewById(R.id.puntos);
                //this.txtValor=(TextView)itemView.findViewById(R.id.valor);
                this.txtEstatus=(TextView)itemView.findViewById(R.id.estatus);
                this.txtFecha=(TextView)itemView.findViewById(R.id.fecha);
                this.imgItem=(ImageView)itemView.findViewById(R.id.ivProfilePhoto);

            }

            public void bind(final SectionCanjesPremios section){

                //txtFolio.setText(section.getFolio());
                txtNumero.setText(section.getNumero());
                txtLugar.setText(section.getLugar());
                //txtPuntos.setText(section.getPuntos());
                //txtValor.setText(section.getValor());
                txtEstatus.setText(section.getEstatus());
                txtFecha.setText(section.getTodate_certificado());

                String ima = section.getImagen1();
                Log.e("imagenjjjpremios", ima);
                final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/storage/premios/" + ima;

                Glide.with(itemView.getContext()).load(urlfinal).into(imgItem);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //  Toast.makeText(itemView.getContext(), "salio" + section.getId() ,Toast.LENGTH_LONG).show();

                        Context context = itemView.getContext();
                        Intent list = new Intent(itemView.getContext(), ValeQR.class);
                        list.putExtra("numero", section.getNumero());
                        list.putExtra("folio", section.getFolio());
                        context.startActivity(list);
                    }
                });

            }
        }

    }
