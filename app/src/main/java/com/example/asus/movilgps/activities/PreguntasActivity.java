package com.example.asus.movilgps.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asus.movilgps.R;
import com.example.asus.movilgps.Utilidades.Utilidades_Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreguntasActivity extends AppCompatActivity {

    ArrayList<String> listaPreguntas;
    ProgressDialog progreso;
    Context context;
   // ArrayList<Encuestas> encuestass;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        request = Volley.newRequestQueue(getApplicationContext());
        context = PreguntasActivity.this;

        String Latitude = getIntent().getStringExtra("latitud");
        String Longitude = getIntent().getStringExtra("longitud");
        String idEncuesta = getIntent().getStringExtra("idEncuesta");

        cargarWebService(Latitude, Longitude, idEncuesta);

        /*progreso = new ProgressDialog(context);
        progreso.setTitle("Enviando coordenadas");
        progreso.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progreso.dismiss();
                    }
                }, 3000);*/
    }

    private void cargarWebService(final String latitude, final String longitude, final String idEncuesta) {

        progreso= new ProgressDialog(context);
        progreso.setMessage("Enviando datos..");
        progreso.show();

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONRegistroEvento.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    Toast.makeText(context,"No registro ocurrio un error ", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(PreguntasActivity.this, MainActivity.class);
                    startActivity(intent);

                }else{
                    Toast.makeText(context,response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                Toast.makeText(context,"Ocurrio un error en el servidor " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("encuesta", idEncuesta);
                paramentros.put("cx",latitude);
                paramentros.put("cy",longitude);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

}
