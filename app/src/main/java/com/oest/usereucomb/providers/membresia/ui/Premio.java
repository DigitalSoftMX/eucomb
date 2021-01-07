package com.oest.usereucomb.providers.membresia.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.providers.canjespremios.ui.CanjesDisponiblesPremiosFragment;
import com.oest.usereucomb.providers.canjespremios.ui.CanjesPremiosFragment;
import com.oest.usereucomb.providers.premios.ui.PremiosFragment;
import com.oest.usereucomb.providers.vales.ui.TabValeAdapter;
import com.oest.usereucomb.util.ThemeUtils;

import java.util.Objects;

public class Premio extends AppCompatActivity {

    private RelativeLayout rl;

    private TabValeAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String usuario;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_principalcanjear);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle("Eucomb");

        usuario = Preferences.obtenerPreferenceString(getBaseContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabValeAdapter(this.getSupportFragmentManager());

        adapter.addFragment(new PremiosFragment(), "SOLICITUD");
        adapter.addFragment(new CanjesDisponiblesPremiosFragment(), "POR RECOGER");
        adapter.addFragment(new CanjesPremiosFragment(), "HISTORIAL");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eliminar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                // Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

