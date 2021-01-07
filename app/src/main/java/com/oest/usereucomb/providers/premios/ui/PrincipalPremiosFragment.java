package com.oest.usereucomb.providers.premios.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Preferences;
import com.oest.usereucomb.providers.canjespremios.ui.CanjesDisponiblesPremiosFragment;
import com.oest.usereucomb.providers.canjespremios.ui.CanjesPremiosFragment;
import com.oest.usereucomb.providers.login.ui.LoginActivity;
import com.oest.usereucomb.providers.vales.ui.TabValeAdapter;

public class PrincipalPremiosFragment extends Fragment {

    private RelativeLayout rl;

    private TabValeAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String usuario;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl = (RelativeLayout) inflater.inflate(R.layout.activity_principlarendimiento, null);

        usuario = Preferences.obtenerPreferenceString(getContext(), Preferences.PREFERENCE_USUARIO_LOGIN);

        viewPager = (ViewPager) rl.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) rl.findViewById(R.id.tabLayout);
        adapter = new TabValeAdapter(getActivity().getSupportFragmentManager());

        if(usuario == "") {
            iniciarActividadSiguiente2();
        }
        else {
            adapter.addFragment(new PremiosFragment(), "SOLICITUD");
            adapter.addFragment(new CanjesDisponiblesPremiosFragment(), "POR RECOGER");
            adapter.addFragment(new CanjesPremiosFragment(), "HISTORIAL");
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return rl;
    }

    public void iniciarActividadSiguiente2(){
        //Toast.makeText(getContext(), "Inicia Sesión", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        this.getActivity().finish();
        //finish();
    }

}
