package com.oest.usereucomb.providers.login.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oest.usereucomb.MainActivity;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.util.Log;
import com.oest.usereucomb.util.ThemeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    private static String IP_Registrar = "https://eucomb.lealtaddigitalsoft.mx/apiregistrar?";

    private Toolbar mToolbar;

    private EditText email;
    private EditText password;
    private EditText name;
    private EditText app_name;
    private EditText apm_name;
    private EditText usernames;

    private Button register;
    private RadioButton RDwoman;
    private RadioButton RDmen;

    private RadioButton aceptarSi;
    private RadioButton aceptarNo;
    private RadioButton condiciones;

    private VolleyRP volley;
    private RequestQueue mRequest;
    EditText etPlannedDate;

    private String USER = "";

    private EditText day;
    private EditText month;
    private EditText year;

    private TextView mDisplayDate;
    private TextView mDisplayDateM;
    private TextView mDisplayDateY;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_registrarse);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registarse");


        // Set up the login form.
        if(Preferences.obtenerPreferenceBoolean(this,Preferences.PREFERENCE_ESTADO_BUTTON_SESION)) {
            iniciarActividadSiguiente();
        }

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        app_name = (EditText) findViewById(R.id.appname);
        apm_name = (EditText) findViewById(R.id.apmname);


        day = (EditText) findViewById(R.id.diaRegistro);
        month = (EditText) findViewById(R.id.mesRegistro);
        year = (EditText) findViewById(R.id.añoRegistro);

        RDwoman = (RadioButton) findViewById(R.id.mujer);
        RDmen = (RadioButton) findViewById(R.id.hombre);

        condiciones = (RadioButton) findViewById(R.id.termino);

        register = (Button) findViewById(R.id.user_sign_in_button);

        RDmen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RDwoman.setChecked(false);
            }
        });
        RDwoman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RDmen.setChecked(false);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VerificarRegistro();
            }
        });

        mDisplayDate = (EditText) findViewById(R.id.diaRegistro);
        mDisplayDateM = (EditText) findViewById(R.id.mesRegistro);
        mDisplayDateY = (EditText) findViewById(R.id.añoRegistro);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistroActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDisplayDateM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistroActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDisplayDateY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        RegistroActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //android.util.Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String dateDay = "" +day;
                String dateMon = "" +month;
                String dateYea = "" +year;
                mDisplayDate.setText(dateDay);
                mDisplayDateM.setText(dateMon);
                mDisplayDateY.setText(dateYea);
            }
        };
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eliminar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                // Log.i("ActionBar", "Atrás!");
                Intent i = new Intent(RegistroActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void VerificarRegistro() {


        String emails = email.getText().toString();
        String contraseña = password.getText().toString();

        if (TextUtils.isEmpty(emails)) {
            email.setError(getString(R.string.error_field_required));
            email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(contraseña)) {
            password.setError(getString(R.string.error_field_required));
            password.requestFocus();
            return;
        }
        if (condiciones.isChecked()) {
            Log.d("dd", "dd");
        }
        else{
            condiciones.setError(getString(R.string.error_field_required));
            condiciones.requestFocus();
            return;
        }

        //USER = user;
        String dia = day.getText().toString();
        String mes = month.getText().toString();
        String año = year.getText().toString();

        if(TextUtils.isEmpty(dia)){
            day.setError(getString(R.string.error_field_required));
            day.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(mes)){
            day.setError(getString(R.string.error_field_required));
            day.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(año)){
            day.setError(getString(R.string.error_field_required));
            day.requestFocus();
            return;
        }

        //correo electronico validacion
        String sex = "";

        if (RDmen.isChecked()) sex = "H";
        else if (RDwoman.isChecked()) sex = "M";

        Log.d("fecha", String.valueOf(etPlannedDate));

        registrarWebService(
                getStringET(email).trim(),
                getStringET(password).trim(),
                getStringET(name).trim(),
                getStringET(app_name).trim(),
                getStringET(apm_name).trim(),
                sex,
                getStringET(year)+"-"+getStringET(month)+"-"+getStringET(day).trim()
        );

    }

    public void registrarWebService(String email, String password, String name, String app_name, String apm_name, String sex, String birthday) {

        progressDialog = ProgressDialog.show(this, "",
                "Espere ............", true);

        if (!email.isEmpty()) {

            // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMapToken = new HashMap<>();
            hashMapToken.put("email", email);
            hashMapToken.put("password", password);
            hashMapToken.put("name", name);
            hashMapToken.put("first_name", app_name);
            hashMapToken.put("second_name", apm_name);
            hashMapToken.put("sex", sex);
            hashMapToken.put("birthdate", birthday);


            JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP_Registrar, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject datos) {
                    try {
                        String estado = datos.getString("resultado");

                        Log.d("estado registro",estado);

                        if(estado.equals("La membresia ya esta ocupada")) {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                            String mensa = "La membresia ya esta ocupada";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, RegistroActivity.this);

                        }
                        else if(estado.equals("La membresia no existe")) {
                            // Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                            String mensa = "La membresia no existe";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, RegistroActivity.this);
                        }
                        else if(estado.equals("Otro usuario ya tiene este mismo correo electronico")) {
                            // Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                            String mensa = "Otro usuario ya tiene este mismo correo electronico";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, RegistroActivity.this);
                        }
                        else if(estado.equals("No es un correo electronico")) {
                            // Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                            String mensa = "No es un correo electronico";
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, RegistroActivity.this);
                        }
                        else if (estado != "") {
                            //  Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            //String imamen = "activo";
                            //Mensajes.MyCustomAlertDialogSinEnviar(estado, imamen, RegistroActivity.this);
                            Preferences.savePreferenceBoolean(RegistroActivity.this,true,Preferences.PREFERENCE_ESTADO_BUTTON_SESION);
                            Preferences.savePreferenceString(RegistroActivity.this, estado,Preferences.PREFERENCE_USUARIO_LOGIN);

                            iniciarActividadSiguiente();
                        } else {
                            //Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                            String imamen = "error";
                            Mensajes.MyCustomAlertDialogSinEnviar(estado, imamen, RegistroActivity.this);

                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        //Toast.makeText(getContext(), "No se pudo registrarse", Toast.LENGTH_LONG).show();
                        String mensa = "No se pudo registrar";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, RegistroActivity.this);
                    }
                    //Toast.makeText(RegistroActivity.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    String mensa = "No se pudo registrar";
                    String imamen = "error";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, RegistroActivity.this);
                }
            });
            VolleyRP.addToQueue(solicitud, mRequest, RegistroActivity.this, volley);
        } else {
            //Toast.makeText(getContext(), "Por favor rellene todos los campos.", Toast.LENGTH_LONG).show();
            String mensa = "Por favor rellene todos los campos";
            String imamen = "error";
            Mensajes.MyCustomAlertDialogSinEnviar(mensa,imamen, RegistroActivity.this);
        }
    }
    private String getStringET(EditText e){
        return e.getText().toString();
    }


    public void iniciarActividadSiguiente(){
        //Toast.makeText(getBaseContext(), "Usted ya inició sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(RegistroActivity.this,MainActivity.class);
        startActivity(i);
        finish();
    }

}

