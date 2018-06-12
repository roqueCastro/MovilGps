package com.example.asus.movilgps.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.example.asus.movilgps.adapters.PreguntaAdapte;
import com.example.asus.movilgps.models.Pregunta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreguntasActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener, AdapterView.OnItemClickListener{

    ArrayList<String> listaPreguntas;
    ProgressDialog progreso;
    Context context;
   // ArrayList<Encuestas> encuestass;
   ArrayList<Pregunta> preguntas;
   private PreguntaAdapte adapter;
   ListView listView;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        request = Volley.newRequestQueue(getApplicationContext());
        context = PreguntasActivity.this;
        listView = findViewById(R.id.listViewPreguntas);
        preguntas = new ArrayList<>();

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
                    Toast.makeText(context,"Registro Exitoso ", Toast.LENGTH_SHORT).show();
                    cargarWebServicePreguntas(idEncuesta);
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

    private void cargarWebServicePreguntas(String idEncuesta) {

        progreso= new ProgressDialog(context);
        progreso.setMessage("Cargando preguntas..");
        progreso.show();

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONConsultaPreguntas.php?id_encuesta="+idEncuesta+"";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        progreso.dismiss();
        Pregunta pregunta = null;


        JSONArray json = response.optJSONArray("pregunta");

        try {
            for(int i=0;i<json.length(); i++){
                pregunta = new Pregunta();
                JSONObject jsonObject = null;

                jsonObject = json.getJSONObject(i);
                if(jsonObject.optInt("id_pgta")==0){
                    Toast.makeText(getApplicationContext(),"no existe en la bd" , Toast.LENGTH_SHORT).show();
                }else{
                    pregunta.setId_pre(jsonObject.optInt("id_pgta"));
                    pregunta.setNombre_pre(jsonObject.optString("nomb_pgta"));
                    pregunta.setTipo_pre(jsonObject.optInt("tipo_pregunta"));
                    preguntas.add(pregunta);

                }
            }
            progreso.hide();

            adapter  = new PreguntaAdapte(this, preguntas, R.layout.list_view_pregunta_item);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);
            //envio getContext

            //recyclerUser.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"No se a podido tener conexion con el servidor " , Toast.LENGTH_SHORT).show();
            progreso.hide();

        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.dismiss();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String id_pre = String.valueOf(preguntas.get(position).getId_pre());

        Intent intent = new Intent();
        startActivity(intent);
    }
}
