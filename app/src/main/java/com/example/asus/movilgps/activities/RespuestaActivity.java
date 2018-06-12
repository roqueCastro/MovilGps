package com.example.asus.movilgps.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.asus.movilgps.R;
import com.example.asus.movilgps.Utilidades.Utilidades_Request;
import com.example.asus.movilgps.adapters.RespuestaAdapte;
import com.example.asus.movilgps.models.Respuesta;

import org.json.JSONObject;

import java.util.ArrayList;

public class RespuestaActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener, AdapterView.OnItemClickListener {

    ProgressDialog progreso;
    Context context;
    // ArrayList<Encuestas> encuestass;
    ArrayList<Respuesta> respuestas;
    private RespuestaAdapte adapter;
    ListView listView;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta);

        request = Volley.newRequestQueue(getApplicationContext());
        context = RespuestaActivity.this;
        listView = findViewById(R.id.listViewRespuestas);
        respuestas = new ArrayList<>();

        String idPregunta = getIntent().getStringExtra("id_pre");
        cargarWebService(idPregunta);
    }

    private void cargarWebService(String idPregunta) {
        progreso= new ProgressDialog(context);
        progreso.setMessage("Cargando respuestas..");
        progreso.show();

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONConsultaRespuestas.php?id_pregunta="+idPregunta+"";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(context, response.toString() ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
