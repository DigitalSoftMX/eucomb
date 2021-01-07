package com.oest.usereucomb.providers.login.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oest.usereucomb.R;
import com.oest.usereucomb.providers.Checklist.ImgDosFragment;
import com.oest.usereucomb.providers.Checklist.ImgTresFragment;
import com.oest.usereucomb.providers.Checklist.ImgUnoFragment;
import com.oest.usereucomb.providers.rendimiento.TabRendimientoAdapter;
import com.oest.usereucomb.util.Log;
import com.oest.usereucomb.util.ThemeUtils;

public class PreguntasFrecuentesActivity extends AppCompatActivity {

    private TabRendimientoAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setTheme(this);
        setContentView(R.layout.activity_principalchecklist);

        mToolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //String imamen = "checklist";
        //Mensajes.MyCustomAlertDialogIntrucciones(imamen, this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabRendimientoAdapter(this.getSupportFragmentManager());
        adapter.addFragment(new ImgUnoFragment(), "Uno");
        adapter.addFragment(new ImgDosFragment(), "Dos");
        adapter.addFragment(new ImgTresFragment(), "Tres");
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
                Log.i("ActionBar", "Atrás!");
                Intent i = new Intent(PreguntasFrecuentesActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

