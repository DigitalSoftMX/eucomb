package com.oest.usereucomb.providers.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oest.usereucomb.R;
import com.oest.usereucomb.billing.Mensajes;
import com.oest.usereucomb.billing.PreferencesRendimiento;
import com.oest.usereucomb.util.Log;
import com.oest.usereucomb.util.ThemeUtils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.google.ads.AdRequest.LOGTAG;


public class FirstMapFragment extends Fragment implements OnMapReadyCallback {

    private static int location;
    private static final int PETICION_PERMISO_LOCALIZACION = location;
    private GoogleMap mMap;
    private Activity mAct;
    private String[] data;
    private Marker marker;
    Context context;
    double latitude;
    double longitud;
    LatLng actual;
    private MenuItem searchMenu;
    private SearchView searchView;
    private String searchQuery;

    public FirstMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String imamen = "estaciones";
        Mensajes.MyCustomAlertDialogIntrucciones(imamen, getContext());

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        }

        return view;
    }





    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_phonne, menu);



        // set & get the search button in the actionbar
        searchView = new SearchView(getActivity());
        searchView.setQueryHint(getResources().getString(
                R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    query = URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    Log.printStackTrace(e);
                }
                searchView.clearFocus();

                searchQuery = query;

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        searchView.addOnAttachStateChangeListener(
                new View.OnAttachStateChangeListener() {

                    @Override
                    public void onViewDetachedFromWindow(View arg0) {
                        searchQuery = null;
                    }

                    @Override
                    public void onViewAttachedToWindow(View arg0) {
                        // search was opened
                    }
                });

        searchMenu = menu.findItem(R.id.menu_search);
        searchMenu.setActionView(searchView);

        if (searchQuery == null)
            searchMenu.setVisible(false);

        ThemeUtils.tintAllIcons(menu, mAct);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //String imamen = "error";
        //Mensajes.MyCustomAlertDialogMapa(imamen, getActivity());

        mMap = googleMap;
        mAct = getActivity();
        // data = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA);
        MapsInitializer.initialize(mAct);


        //permisos
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getContext(), "Lo siento sin el permiso no podra acceder a esta seccion", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PETICION_PERMISO_LOCALIZACION);
            }

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .commit();

        } else {
            LocationManager locationManager = (LocationManager) mAct.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            if (locationManager != null) {

                FirstMapFragment.this.mMap.setMyLocationEnabled(true);
                //ubicacion actual
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                if(location != null){
                    double latitude = location.getLatitude();
                    double longitud = location.getLongitude();
                    actual = new LatLng(latitude, longitud);
                    CameraPosition cameraPosition = CameraPosition.builder().target(actual).zoom(10).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
                else{
                    //Toast.makeText(getContext(), "Lo siento sin el permiso no podra acceder a esta seccion.", Toast.LENGTH_LONG).show();

                    float lat11 = Float.parseFloat("18.7934761");
                    float lng11 = Float.parseFloat("-97.1941593");
                    float lat2 = Float.parseFloat(String.valueOf(latitude));
                    float lng2 = Float.parseFloat(String.valueOf(longitud));
                    float valor = distFrom(lat11, lng11, lat2, lng2);
                    int a = (int) valor;
                    String s1 = Distance(a);
                    Log.e("valor sin activacion del gps", s1);
                    actual = new LatLng(lat11, lng11);
                    CameraPosition cameraPosition = CameraPosition.builder().target(actual).zoom(10).build();
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(true);

                //double latitude = 18.79134;
                //double longitud = -97-19332;


                //ubicaciones individuales

                float lat11 = Float.parseFloat("18.7934761");
                float lng11 = Float.parseFloat("-97.1941593");
                float lat2 = Float.parseFloat(String.valueOf(latitude));
                float lng2 = Float.parseFloat(String.valueOf(longitud));
                float valor = distFrom(lat11, lng11, lat2, lng2);
                int a = (int) valor;
                String s1 = Distance(a);
                Log.e("valor float", s1);

                LatLng latLng1 = new LatLng(lat11, lng11);
                MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1).title("Servicio Cuautlapan Doncellas").snippet("K.m. 53.5 Carret. Tehuacán-Ver S/N, Col. Paseo Nuevo  Teléfonos: (272)72-6-83-49  (272)72-6-78-53  e-mail: gabygrd72@hotmail.com   " + "Distancia: " + s1).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions1);

                //--------------------------------------------------------------------------------------------------------------------------
                float lat12 = Float.parseFloat("18.83797");
                float lng12 = Float.parseFloat("-96.8202478");
                float valor2 = distFrom(lat12, lng12, lat2, lng2);
                int a2 = (int) valor2;
                String s2 = Distance(a2);
                Log.e("valor float", s2);

                LatLng latLng2 = new LatLng(lat12, lng12);
                MarkerOptions markerOptions2 = new MarkerOptions().position(latLng2).title("Energéticos de Cordoba Matriz").snippet("Carret. Fed. Cordoba-Ver S/N, Desviación a Omealca  Teléfono: (278)73-8-80-99  e-mail: reynaar@hotmail.com  " + "Distancia: " + s2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions2);


                //--------------------------------------------------------------------------------------------------------------------------
                float lat13 = Float.parseFloat("18.885481");
                float lng13 = Float.parseFloat("-96.963532");
                float valor3 = distFrom(lat13, lng13, lat2, lng2);
                int a3 = (int) valor3;
                String s3 = Distance(a3);
                Log.e("valor float", s3);

                LatLng latLng3 = new LatLng(lat13, lng13);
                MarkerOptions markerOptions3 = new MarkerOptions().position(latLng3).title("Combustibles y Servicios Esmeralda").snippet("Autopista México – Veracruz, Km. 291 Col. El Dorado  Teléfonos: (271)71-6-23-44 (271)73-6-17-97  e-mail: guadalupe_5642@outlook.es  " + "Distancia: " + s3).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions3);


                //--------------------------------------------------------------------------------------------------------------------------
                float lat14 = Float.parseFloat("18.6257727");
                float lng14 = Float.parseFloat("-97.4047722");
                float valor4 = distFrom(lat14, lng14, lat2, lng2);
                int a4 = (int) valor4;
                String s4 = Distance(a4);
                Log.e("valor float", s4);

                LatLng latLng4 = new LatLng(lat14, lng14);
                MarkerOptions markerOptions4 = new MarkerOptions().position(latLng4).title("Servicio Cuautlapan Chapulco").snippet("Carretera Fed. Tehuacán – Orizaba Km. 14.5 S/N  Teléfonos: (238)37-1-42-12 (238)37-1-42-13  e-mail: joseluisgears@hotmail.com  " + "Distancia: " + s4).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions4);

                //--------------------------------------------------------------------------------------------------------------------------
                float lat15 = Float.parseFloat("18.5436831");
                float lng15 = Float.parseFloat("-97.1987151");
                float valor5 = distFrom(lat15, lng15, lat2, lng2);
                int a5 = (int) valor5;
                String s5 = Distance(a5);
                Log.e("valor float", s5);

                LatLng latLng5 = new LatLng(lat15, lng15);
                MarkerOptions markerOptions5 = new MarkerOptions().position(latLng5).title("Energéticos Santa María del Monte").snippet("Predio Atzómpa S/N.  Santa Ma. del Monte  Teléfono: (236)37-4-92-39   e-mail: enc_stama@hotmail.com   " + "Distancia: " + s5).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions5);

                //ROJO--------------------------------------------------------------------------------------------------------------------------
                float lat16 = Float.parseFloat("18.8271456");
                float lng16 = Float.parseFloat("-97.1608651");
                float valor6 = distFrom(lat16, lng16, lat2, lng2);
                int a6 = (int) valor6;
                String s6 = Distance(a6);
                Log.e("valor float", s6);

                LatLng latLng6 = new LatLng(lat16, lng16);
                MarkerOptions markerOptions6 = new MarkerOptions().position(latLng6).title("Energéticos de Cordoba Nogales").snippet("Av. Juárez No. 125, Col. Aurora  Teléfono:  (272)72-7-50-42  e-mail: cajanogales@hotmail.com   " + "Distancia: " + s6).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions6);

                //--------------------------------------------------------------------------------------------------------------------------
                float lat17 = Float.parseFloat("18.8748058");
                float lng17 = Float.parseFloat("-97.0266527");
                float valor7 = distFrom(lat17, lng17, lat2, lng2);
                int a7 = (int) valor7;
                String s7 = Distance(a7);
                Log.e("valor float", s7);

                LatLng latLng7 = new LatLng(lat17, lng17);
                MarkerOptions markerOptions7 = new MarkerOptions().position(latLng7).title("Servicio Cuautlapan Matriz").snippet("Km. 325 Carret. Fed. México - Veracruz S/N  Teléfono: (271)71-3-10-68 (271)71-3-10-04  e-mail: enc_scu_0673@hotmail.com   " + "Distancia: " + s7).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions7);

                //VERDE--------------------------------------------------------------------------------------------------------------------------
                float lat18 = Float.parseFloat("18.4645378");
                float lng18 = Float.parseFloat("-97.4095184");
                float valor8 = distFrom(lat18, lng18, lat2, lng2);
                int a8 = (int) valor8;
                String s8 = Distance(a8);
                Log.e("valor float", s8);

                LatLng latLng8 = new LatLng(lat18, lng18);
                MarkerOptions markerOptions8 = new MarkerOptions().position(latLng8).title("Servicio Cuatlapan Tehuacán").snippet("Calz. Adolfo López Mateos No. 2400, Fracc. Zona  Teléfono: (238)38-2-86-32 al 34 Ext. 103  e-mail: gasolineria.ete@outlook.com   " + "Distancia: " + s8).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions8);

                //------------------------------------------------------------------------------------------------------------------------------
                float lat19 = Float.parseFloat("18.441289");
                float lng19 = Float.parseFloat("-97.380482");
                float valor9 = distFrom(lat19, lng19, lat2, lng2);
                int a9 = (int) valor9;
                String s9 = Distance(a9);
                Log.e("valor float", s9);

                LatLng latLng9 = new LatLng(lat19, lng19);
                MarkerOptions markerOptions9 = new MarkerOptions().position(latLng9).title("Gasolinería Ele").snippet("Av. De la Juventud No. 2906, Col. Humilladero  Teléfono: (238)38-3-95-40  e-mail: gasolineria_ele@hotmail.com   " + "Distancia: " + s9).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions9);

                //------------------------------------------------------------------------------------------------------------------------------
                float lat110 = Float.parseFloat("19.04267");
                float lng110 = Float.parseFloat("-98.1710557");
                float valor10 = distFrom(lat110, lng110, lat2, lng2);
                int a10 = (int) valor10;
                String s10 = Distance(a10);
                Log.e("valor float", s10);

                LatLng latLng10 = new LatLng(lat110, lng110);
                MarkerOptions markerOptions10 = new MarkerOptions().position(latLng10).title("Energéticos Solé").snippet("Calle 12 Oriente No. 3215, Col. Resurgimiento  Teléfono: (222)2-34-53-86  e-mail: monse_enc_sole@hotmail.com   " + "Distancia: " + s10).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions10);

                //------------------------------------------------------------------------------------------------------------------------------
                float lat111 = Float.parseFloat("19.0536327");
                float lng111 = Float.parseFloat("-98.2403712");
                float valor11 = distFrom(lat111, lng111, lat2, lng2);
                int a11 = (int) valor11;
                String s11 = Distance(a11);
                Log.e("valor float", s11);

                LatLng latLng11 = new LatLng(lat111, lng111);
                MarkerOptions markerOptions11 = new MarkerOptions().position(latLng11).title("Gasolinera Zavaleta").snippet("Calle 55 Sur No. 2119, Col. Ampliación Reforma  Teléfonos: (222)68-84-322 y (222)68-84-327  e-mail: gasolinerazavaleta@gmail.com   " + "Distancia: " + s11).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions11);

                //AMARILLO------------------------------------------------------------------------------------------------------------------------------
                float lat112 = Float.parseFloat("20.0081882");
                float lng112 = Float.parseFloat("-97.8547039");
                float valor12 = distFrom(lat112, lng112, lat2, lng2);
                int a12 = (int) valor12;
                String s12 = Distance(a12);
                Log.e("valor float", s12);

                LatLng latLng12 = new LatLng(lat112, lng112);
                MarkerOptions markerOptions12 = new MarkerOptions().position(latLng12).title("Energéticos Ahuacatlan").snippet("Libramiento Bicentenario 46, Col. Centro  Teléfono: (764)76-3-32-62  e-mail: encargadaahuacatlan@gmail.com   " + "Distancia: " + s12).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions12);

                //-------------------------------------------------------------------------------------------------------------------------------------
                float lat113 = Float.parseFloat("19.0475763");
                float lng113 = Float.parseFloat("-98.17322");
                float valor13 = distFrom(lat113, lng113, lat2, lng2);
                int a13 = (int) valor13;
                String s13 = Distance(a13);
                Log.e("valor float", s13);

                LatLng latLng13 = new LatLng(lat113, lng113);
                MarkerOptions markerOptions13 = new MarkerOptions().position(latLng13).title("Servicio Alfa Bravo Coca").snippet("Boulevard Xonaca, No. 3216, Col. Vista Hermosa  Teléfono: (272)72-7-50-42  e-mail: servicioalfabravococa@gmail.com  " + "Distancia: " + s13).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions13);

                //-------------------------------------------------------------------------------------------------------------------------------------
                float lat114 = Float.parseFloat("16.7788925");
                float lng114 = Float.parseFloat("-96.6735754");
                float valor14 = distFrom(lat114, lng114, lat2, lng2);
                int a14 = (int) valor14;
                String s14 = Distance(a14);
                Log.e("valor float", s14);

                LatLng latLng14 = new LatLng(lat114, lng114);
                MarkerOptions markerOptions14 = new MarkerOptions().position(latLng14).title("Litro Exacto").snippet("Carretera Oaxaca Puerto Angel KM 32  Teléfono: (52)95-11-18-33-16  e-mail: litroexactoocotlan@hotmail.com  " + "Distancia: " + s14).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker = mMap.addMarker(markerOptions14);


                InfoWindowsCustom customInfoWindow = new InfoWindowsCustom(mAct);
                FirstMapFragment.this.mMap.setInfoWindowAdapter(customInfoWindow);

                //FirstMapFragment.this.mMap.setInfoWindowAdapter(new InfoWindowsCustom(mAct));

       /* String url = obtenerDireccionesURL(cali, destino);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);*/
            } else {
                Toast.makeText(getContext(), "Ubicación no encontrada", Toast.LENGTH_LONG).show();
            }

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_eliminar:

                PreferencesRendimiento.removePreferenceBoolean(getContext(), PreferencesRendimiento.PREFERENCE_REND);

                String mensa = "Se eliminaron correctamente";
                String imamen = "activo";
                Mensajes.MyCustomAlertDialog(mensa, imamen, getContext());

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //3958.75 miles or 6371.0 kilometers
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public static String Distance(int distance){
        if(distance >= 1000){
            int k = distance / 1000;
            int m = distance - (k*1000);
            m = m / 100;
            return String.valueOf(k) + (m>0?("."+String.valueOf(m)):"") + " Km" +(k>1?"s":"");
        } else {
            return String.valueOf(distance) + " metro"+(distance==1?"":"s");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(LOGTAG, "permiso concedido");
            }
            } else {
                //Permiso denegado:
                Log.e(LOGTAG, "Permiso denegado");
            }
        }

    /*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mAct = getActivity();
        Helper.isOnlineShowDialog(mAct);
        data = this.getArguments().getStringArray(MainActivity.FRAGMENT_DATA);
        MapsInitializer.initialize(mAct);
                if (ContextCompat.checkSelfPermission(mAct, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
                    FirstMapFragment.this.mMap.setMyLocationEnabled(true);

        LatLng UCA = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(UCA)
                .title("YOUR TITLE")
                .snippet("Primer ministro: Shinzō Abe")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))).showInfoWindow();

        // Markers
        LatLng colombia = new LatLng(4.6,-74.08);
        mMap.addMarker(new MarkerOptions()
                .position(colombia)
                .title("Colombia")
        );

        // Cámara
        mMap.moveCamera(CameraUpdateFactory.newLatLng(colombia));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(UCA,17));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                return true;
            }
        });
    }
*/

}