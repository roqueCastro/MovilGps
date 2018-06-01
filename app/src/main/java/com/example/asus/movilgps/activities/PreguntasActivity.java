package com.example.asus.movilgps.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.asus.movilgps.R;

import java.util.ArrayList;

public class PreguntasActivity extends AppCompatActivity {

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    ArrayList<String> listaPreguntas;
   // ArrayList<Encuestas> encuestass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        String Latitude = getIntent().getStringExtra("latitud");
        String Longitude = getIntent().getStringExtra("longitud");
        String idEncuesta = getIntent().getStringExtra("idEncuesta");
        Toast.makeText(getApplicationContext(), idEncuesta, Toast.LENGTH_SHORT).show();

    }
}
