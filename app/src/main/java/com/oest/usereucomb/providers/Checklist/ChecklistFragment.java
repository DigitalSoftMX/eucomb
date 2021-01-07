package com.oest.usereucomb.providers.Checklist;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.providers.rendimiento.TabRendimientoAdapter;

public class ChecklistFragment extends Fragment {

        private RelativeLayout rl;

        private TabRendimientoAdapter adapter;
        private TabLayout tabLayout;
        private ViewPager viewPager;

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rl = (RelativeLayout) inflater.inflate(R.layout.activity_principalchecklist, null);


            String imamen = "checklist";
            Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

            viewPager = (ViewPager) rl.findViewById(R.id.viewPager);
            tabLayout = (TabLayout) rl.findViewById(R.id.tabLayout);
            adapter = new TabRendimientoAdapter(getActivity().getSupportFragmentManager());
            adapter.addFragment(new ImgUnoFragment(), "Uno");
            adapter.addFragment(new ImgDosFragment(), "Dos");
            //adapter.addFragment(new SincronizarFragment(), "Enviar");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);


            return rl;
        }


    }