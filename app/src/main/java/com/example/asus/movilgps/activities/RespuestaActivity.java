package com.example.asus.movilgps.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.models.Respuesta;

import java.util.ArrayList;

public class RespuestaActivity extends AppCompatActivity {

    ProgressDialog progreso;
    Context context;
    // ArrayList<Encuestas> encuestass;
    ArrayList<Respuesta> respuestas;
    private PreguntaAdapte adapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta);

        String idPregunta = getIntent().getStringExtra("id_pre");
        Toast.makeText(getApplicationContext(), idPregunta, Toast.LENGTH_SHORT).show();
    }
}
