package com.oest.usereucomb.billing;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;

import java.io.InputStream;
import java.net.URL;


public class Mensajes extends Activity {


    public static void MyCustomAlertDialog(String mensa, String imagmens, final Context context){
        final Dialog MyDialog = new Dialog(context);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.message);
        MyDialog.setCancelable(false);
        MyDialog.setCanceledOnTouchOutside(false);

        Button close = (Button)MyDialog.findViewById(R.id.close);
        ImageView image = (ImageView) MyDialog.findViewById(R.id.imgmensaje);
        TextView titu = (TextView) MyDialog.findViewById(R.id.mensaje);
        if(imagmens == "error"){
            image.setImageResource(R.drawable.mens6);
        }
        else if(imagmens == "folio"){
            image.setImageResource(R.drawable.folio);
        }
        else{
            image.setImageResource(R.drawable.mens3);
        }
        titu.setText(mensa);

        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,MainActivity.class);
                //i.putExtra("user",USER);
                context.startActivity(i);
                //context.finish();
            }
        });
//        MyDialog.show();
    }

    public static void MyCustomAlertDialogSinEnviar(String mensa, String imagmens, final Context context){
        final Dialog MyDialog = new Dialog(context);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.message);
        MyDialog.setCancelable(false);
        MyDialog.setCanceledOnTouchOutside(false);

        Button close = (Button)MyDialog.findViewById(R.id.close);
        ImageView image = (ImageView) MyDialog.findViewById(R.id.imgmensaje);
        TextView titu = (TextView) MyDialog.findViewById(R.id.mensaje);
        if(imagmens == "error"){
            image.setImageResource(R.drawable.mens6);
        }
        else if(imagmens == "folio"){
            image.setImageResource(R.drawable.folio);
        }
        else{
            image.setImageResource(R.drawable.mens3);
        }
        titu.setText(mensa);

        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }

    public static void MyCustomAlertDialogPrincipal(String imagmens, final Context context) {
        final Dialog MyDialog = new Dialog(context);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.messageimgqr);
        MyDialog.setCancelable(false);
        MyDialog.setCanceledOnTouchOutside(false);

        Button close = (Button) MyDialog.findViewById(R.id.close);
        ImageView image = (ImageView) MyDialog.findViewById(R.id.imgmensaje);
        TextView numero = (TextView) MyDialog.findViewById(R.id.texto);

        String qr = Preferences.obtenerPreferenceString(context, Preferences.PREFERENCE_USUARIO_LOGIN);
        final String urlfinal = "https://eucomb.lealtaddigitalsoft.mx/public/img/usuarioimg/" + qr + ".jpg";
        new DownLoadImageTask(image).execute(urlfinal);
        numero.setText(qr);

        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }

    public static void MyCustomAlertDialogIntrucciones(String imagmens, final Context context){
        final Dialog MyDialog = new Dialog(context);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.messageprincipal);
        MyDialog.setCancelable(false);
        MyDialog.setCanceledOnTouchOutside(false);

        Button close = (Button)MyDialog.findViewById(R.id.close);
        ImageView image = (ImageView) MyDialog.findViewById(R.id.imgmensaje);
        //TextView titu = (TextView) MyDialog.findViewById(R.id.mensaje);


        if(imagmens == "sincronizarpuntos") {
            image.setImageResource(R.drawable.info3);
        }
        else if(imagmens == "estadocuenta") {
            image.setImageResource(R.drawable.info);
        }
        else if(imagmens == "vales") {
            image.setImageResource(R.drawable.vales);
        }
        else if(imagmens == "checklist") {
            image.setImageResource(R.drawable.checklist);
        }
        else if(imagmens == "estaciones") {
            image.setImageResource(R.drawable.vales);
        }
        else if(imagmens == "rendimiento") {
            image.setImageResource(R.drawable.kilometraje);
        }
        else if(imagmens == "perfil") {
            image.setImageResource(R.drawable.perfil);
        }
        else if(imagmens == "escanear") {
            image.setImageResource(R.drawable.sumapuntos);
        }
        else if(imagmens == "entrada") {
            image.setImageResource(R.drawable.info2);
        }
        else if(imagmens == "premiosmensaje") {
            image.setImageResource(R.drawable.presentarife);
        }
        else if(imagmens == "premios") {
            image.setImageResource(R.drawable.premi);
        }
        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }

    public static void MyCustomAlertDialogPremiosVales(final Context context){
        final Dialog MyDialog = new Dialog(context);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.messageprincipal);
        MyDialog.setCancelable(false);
        MyDialog.setCanceledOnTouchOutside(false);

        Button close = (Button)MyDialog.findViewById(R.id.close);
        ImageView image = (ImageView) MyDialog.findViewById(R.id.imgmensaje);
        TextView titu = (TextView) MyDialog.findViewById(R.id.mensaje);

        image.setImageResource(R.drawable.presentarife);
        //titu.setText(mensa);

        close.setEnabled(true);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }


    private static class DownLoadImageTask extends AsyncTask<String,Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}
