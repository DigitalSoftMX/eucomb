package com.oest.usereucomb;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;

import androidx.fragment.app.Fragment;

import com.oest.usereucomb.drawer.NavItem;
import com.oest.usereucomb.drawer.SimpleMenu;
import com.oest.usereucomb.drawer.SimpleSubMenu;
import com.oest.usereucomb.providers.CustomIntent;
import com.oest.usereucomb.providers.Provider;
import com.oest.usereucomb.providers.canjes.ui.CanjesFragment;
import com.oest.usereucomb.providers.canjeshistorial.ui.CanjesHistorialFragment;
import com.oest.usereucomb.providers.estadocuenta.ui.EstadoCuentaFragment;
import com.oest.usereucomb.providers.facturacion.ui.FacturacionFragment;
import com.oest.usereucomb.providers.login.ui.CerrarFragment;
import com.oest.usereucomb.providers.login.ui.DatosFacturaFragment;
import com.oest.usereucomb.providers.login.ui.LoginFragment;
import com.oest.usereucomb.providers.login.ui.PerfilFragment;
import com.oest.usereucomb.providers.maps.PrincipalMapaFragment;
import com.oest.usereucomb.providers.membresia.ui.MembresiaFragment;
import com.oest.usereucomb.providers.politicas.PoliticasFragment;
import com.oest.usereucomb.providers.premios.ui.PrincipalPremiosFragment;
import com.oest.usereucomb.providers.qr.EscanerFragment;
import com.oest.usereucomb.providers.maps.MapsFragment;
import com.oest.usereucomb.providers.qr.PrincipalQR;
import com.oest.usereucomb.providers.rendimiento.HistorialRendimientoFragment;
import com.oest.usereucomb.providers.rendimiento.PrincipalRendimientoFragment;
import com.oest.usereucomb.providers.rss.ui.RssFragment;
import com.oest.usereucomb.providers.vales.ui.PrincipalValeFragment;
import com.oest.usereucomb.providers.videos.ui.VideosFragment;
import com.oest.usereucomb.util.Helper;
import com.oest.usereucomb.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This file is part of the Universal template
 * For license information, please check the LICENSE
 * file in the root of this project
 *
 * @author Sherdle
 * Copyright 2018
 */

/**
 * Async task class to get json by making HTTP call
 */
public class ConfigParser extends AsyncTask<Void, Void, Void> {

    //Instance variables
    private String sourceLocation;
    private Activity context;
    private SimpleMenu menu;
    private CallBack callback;

    private boolean facedException;

    private static JSONArray jsonMenu = null;

    //Cache settings
    private static String CACHE_FILE = "menuCache.srl";
    final long MAX_FILE_AGE = 1000 * 60 * 60 * 24 * 2;

    public ConfigParser(String sourceLocation, SimpleMenu menu, Activity context, CallBack callback){
        this.sourceLocation = sourceLocation;
        this.context = context;
        this.menu = menu;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... args) {

        if (jsonMenu == null)
            Log.e("INFO", "null.");
        try {
                //Get the JSON
                if (sourceLocation.contains("http")) {
                    jsonMenu = getJSONFromCache();
                    if (getJSONFromCache() == null) {
                        Log.v("INFO", "Loading Menu Config from url.");
                        String jsonStr = Helper.getDataFromUrl(sourceLocation);
                        jsonMenu = new JSONArray(jsonStr);
                        saveJSONToCache(jsonStr);
                    } else {
                        Log.v("INFO", "Loading Menu Config from cache.");
                    }
                } else {
                    String jsonStr = Helper.loadJSONFromAsset(context, sourceLocation);
                    jsonMenu = new JSONArray(jsonStr);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        if (jsonMenu  != null) {
            Log.e("INFO", "no null.");

            final JSONArray jsonMenuFinal = jsonMenu;
            //Adding menu items must happen on UIthread
            context.runOnUiThread(new Runnable() { public void run() {
                try {
                    SimpleSubMenu subMenu = null;

                    // looping through all menu items
                    for (int i = 0; i < jsonMenuFinal.length(); i++) {
                        JSONObject jsonMenuItem = jsonMenuFinal.getJSONObject(i);

                        String menuTitle = jsonMenuItem.getString("title");
                        //Parse the drawable if there is one
                        int menuDrawableResource = 0;
                        if (jsonMenuItem.has("drawable") &&
                                jsonMenuItem.getString("drawable") != null
                                && !jsonMenuItem.getString("drawable").isEmpty()
                                && !jsonMenuItem.getString("drawable").equals("0"))
                            menuDrawableResource = getDrawableByName(context, jsonMenuItem.getString("drawable"));

                        //If this menu item has a submenu
                        if (jsonMenuItem.has("submenu")
                                && jsonMenuItem.getString("submenu") != null
                                && !jsonMenuItem.getString("submenu").isEmpty()){
                            String menuSubMenu = jsonMenuItem.getString("submenu");

                            //If the submenu doesn't exist yet, create it
                            if (subMenu == null || !subMenu.getSubMenuTitle().equals(menuSubMenu))
                                subMenu = new SimpleSubMenu(menu, menuSubMenu);
                        } else {
                            subMenu = null;
                        }

                        //If this menu item requires iap
                        boolean requiresIap = false;
                        if (jsonMenuItem.has("iap")
                                && jsonMenuItem.getBoolean("iap")){
                            requiresIap = true;
                        }

                        List<NavItem> menuTabs = new ArrayList<NavItem>();

                        JSONArray jsonTabs = jsonMenuItem.getJSONArray("tabs");

                        for (int j = 0; j < jsonTabs.length(); j++){
                            JSONObject jsonTab = jsonTabs.getJSONObject(j);

                            menuTabs.add(navItemFromJSON(context, jsonTab));
                        }

                        //If this item belongs in a submenu, add it to the submenu, else add it to the top menu
                        if (subMenu != null)
                            subMenu.add(menuTitle, menuDrawableResource, menuTabs, requiresIap);
                        else
                            menu.add(menuTitle, menuDrawableResource, menuTabs, requiresIap);
                    }


                } catch (final JSONException e) {
                    e.printStackTrace();
                    Log.e("INFO", "JSON was invalid");
                    facedException = true;
                }

            } }); //end of runOnUIThread
        } else {
            Log.e("INFO", "JSON Could not be retrieved");
            facedException = true;
        }

        return null;
    }

    public static NavItem navItemFromJSON(Context context, JSONObject jsonTab) throws JSONException{
        String tabTitle = jsonTab.getString("title");
        String tabProvider = jsonTab.getString("provider");
        JSONArray args =  jsonTab.getJSONArray("arguments");

        List<String> list = new ArrayList<String>();
        for(int i = 0; i < args.length(); i++){
            list.add(args.getString(i));
        }

        //Parse the type
        Class<? extends Fragment> tabClass = null;
        if (tabProvider.equals(Provider.RSS))
            tabClass = RssFragment.class;
        else if (tabProvider.equals(Provider.YOUTUBE) || tabProvider.equals(Provider.WORDPRESS_VIDEO) || tabProvider.equals(Provider.VIMEO))
            tabClass = VideosFragment.class;
        else if (tabProvider.equals(Provider.MAPS))
            tabClass = MapsFragment.class;
        else if (tabProvider.equals(Provider.CUSTOM))
            tabClass = CustomIntent.class;
        else if (tabProvider.equals("estadocuenta"))
            tabClass = EstadoCuentaFragment.class;
        else if (tabProvider.equals("escanear"))
            tabClass = PrincipalQR.class;
        else if (tabProvider.equals("mapa"))
            tabClass = PrincipalMapaFragment.class;
        else if (tabProvider.equals("datosfactura"))
            tabClass = DatosFacturaFragment.class;
        else if (tabProvider.equals("perfil"))
            tabClass = PerfilFragment.class;
        else if (tabProvider.equals("cerrar"))
            tabClass = CerrarFragment.class;
        else if (tabProvider.equals("facturacion"))
            tabClass = FacturacionFragment.class;
        else if (tabProvider.equals("membresia"))
            tabClass = MembresiaFragment.class;
        else if (tabProvider.equals("politicas"))
            tabClass = PoliticasFragment.class;
        else if (tabProvider.equals("vales"))
            tabClass = PrincipalValeFragment.class;
        else if (tabProvider.equals("premios"))
            tabClass = PrincipalPremiosFragment.class;
        else if (tabProvider.equals("canjes"))
            tabClass = CanjesFragment.class;
        else if (tabProvider.equals("canjehistorial"))
            tabClass = CanjesHistorialFragment.class;
        else if (tabProvider.equals("rendimiento"))
            tabClass = PrincipalRendimientoFragment.class;
        else if (tabProvider.equals("rendimientohistorial"))
            tabClass = HistorialRendimientoFragment.class;
        else if (tabProvider.equals("login"))
            tabClass = LoginFragment.class;
        else
            throw new RuntimeException("Invalid type specified for tab");

        NavItem item = new NavItem(tabTitle, tabClass, tabProvider, list.toArray(new String[0]));

        //Add the image if present
        if (jsonTab.has("image")
                && !jsonTab.isNull("image")
                && !jsonTab.getString("image").isEmpty()){
            int menuDrawableResource = 0;

            if (!jsonTab.getString("image").startsWith("http"))
                item.setTabIcon(getDrawableByName(context, jsonTab.getString("image")));
            else
                item.setCategoryImageUrl(jsonTab.getString("image"));
        }

        return item;
    }

    @Override
    protected void onPostExecute(Void args) {
        if (callback != null)
            callback.configLoaded(facedException);
    }

    private static int getDrawableByName(Context context, String name){
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier(name, "drawable",
                context.getPackageName());
        return resourceId;
    }

    public interface CallBack {
        void configLoaded(boolean success);
    }


    public void saveJSONToCache(String json){
        // Instantiate a JSON object from the request response
        try {
            // Save the JSONObject
            ObjectOutput out = null;

            out = new ObjectOutputStream(new FileOutputStream(new File(context.getCacheDir(),"")+ CACHE_FILE));

            out.writeObject( json );
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONArray getJSONFromCache(){
        // Load in an object
        try {
            ObjectInputStream in = null;
            File cacheFile = new File(new File(context.getCacheDir(),"")+ CACHE_FILE);
            in = new ObjectInputStream(new FileInputStream(cacheFile));
            String jsonArrayRaw = (String) in.readObject();
            in.close();

            //If the cache is not outdated
            if (cacheFile.lastModified()+ MAX_FILE_AGE > System.currentTimeMillis())
                return new JSONArray(jsonArrayRaw);
            else
                return null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

}
