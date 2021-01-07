package com.oest.usereucomb.providers.login.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.oest.usereucomb.billing.VolleyRP;
import com.oest.usereucomb.util.Log;
import com.oest.usereucomb.util.ThemeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class  ResetActivity extends AppCompatActivity {

    private EditText eTusuario;
    private Button bTaceptar;

    private VolleyRP volley;
    private RequestQueue mRequest;

    private static String IP = "https://eucomb.lealtaddigitalsoft.mx/public/password/email?";

    private String USER = "";
    private String PASS = "";
    ProgressDialog progressDialog;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_reset);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        volley = VolleyRP.getInstance(this);
        mRequest = volley.getRequestQueue();

        //View myFragmentView = inflater.inflate(R.layout.activity_login, container, false);

        eTusuario = (EditText) findViewById(R.id.email);
        bTaceptar = (Button) findViewById(R.id.user_sign_in_button);

        bTaceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VerificarLogin(eTusuario.getText().toString());
            }

        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        Log.i("ActionBar", "Atrás!");
        Intent i = new Intent(ResetActivity.this,LoginActivity.class);
        startActivity(i);
        finish();
                return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        return true;
    }


    public void VerificarLogin(String user) {

        eTusuario.setError(null);


        String email = eTusuario.getText().toString();

        if (isEmpty(email)) {
            eTusuario.setError(getString(R.string.error_field_required));
            eTusuario.requestFocus();
            return;
        }

        USER = user;

        registrarWebService(
                USER
        );
    }

    public void registrarWebService(String username) {

        progressDialog = ProgressDialog.show(this, "",
                "Espere ............", true);

        // Toast.makeText(RegistroActivity.this, dateb, Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMapToken = new HashMap<>();
        hashMapToken.put("email", username);

        JsonObjectRequest solicitud = new JsonObjectRequest(Request.Method.POST, IP, new JSONObject(hashMapToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject datos) {
                try {
                    String estado = datos.getString("resultado");
                     //Toast.makeText(getBaseContext(), estado, Toast.LENGTH_LONG).show();

                    // Toast.makeText(getContext(), estado, Toast.LENGTH_LONG).show();
                    if (estado.equals("no existe este correo")) {
                        //Toast.makeText(getContext(), estado, Toast.LENGTH_SHORT).show();
                        String mensa = "No existe el correo";
                        String imamen = "error";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ResetActivity.this);
                        eTusuario.setText("");

                    } else {
                        //Toast.makeText(getContext(), "No se pudo ingresar", Toast.LENGTH_LONG).show();
                        String mensa = "Se envío el correo correctamente";
                        String imamen = "bien";
                        Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ResetActivity.this);
                        iniciarActividadSiguiente();
                        eTusuario.setText("");
                    }
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                    String mensa = "Se envío el correo correctamente";
                    String imamen = "bien";
                    Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ResetActivity.this);
                    progressDialog.dismiss();
                    eTusuario.setText("");
                    iniciarActividadSiguiente();


                }
                progressDialog.dismiss();
                //Toast.makeText(RegistroActivity.this, "No se registro correctamente android", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), "No se pudo registrar", Toast.LENGTH_LONG).show();
                String mensa = "Se envío el correo correctamente";
                String imamen = "bien";
                Mensajes.MyCustomAlertDialogSinEnviar(mensa, imamen, ResetActivity.this);
                eTusuario.setText("");
                progressDialog.dismiss();
                // iniciarActividadSiguiente();


            }
        });
        VolleyRP.addToQueue(solicitud, mRequest, ResetActivity.this, volley);
    }

    private String getStringET(EditText e) {
        return e.getText().toString();
    }


    public void iniciarActividadSiguiente() {
        //Toast.makeText(getBaseContext(), "Usted ya inició sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(ResetActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}

