package com.example.asus.movilgps.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.movilgps.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {

    private View rootView;
    private TextView _linkMap;


    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*// Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        if (!compruebaRed(getContext())) {
            Toast.makeText(getContext(),"Necesaria conexión a internet ", Toast.LENGTH_SHORT).show();
            showInfoAlertDateMovil();
        }else if(compruebaOnlineNet()==false) {
            Toast.makeText(getContext(),"No tiene datos", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(),"Activo", Toast.LENGTH_SHORT).show();
        }

       *//* _linkMap = (TextView) rootView.findViewById(R.id.pasar);*//*
        _linkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                *//*Fragment fragment = new MapFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);

                // Commit a la transacción
                transaction.commit();*//*
            }
        });
*/
        return rootView;
    }


    private void showInfoAlertDateMovil() {
        new AlertDialog.Builder(getContext())
                .setTitle("ACTIVAR Datos Moviles ")
                .setMessage("Desavilitados")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    public Boolean compruebaOnlineNet() {
        boolean conex = false;

        ConnectivityManager con = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =con.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected()){
            conex=true;
        }
        return conex;
    }


    public static boolean compruebaRed(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

}