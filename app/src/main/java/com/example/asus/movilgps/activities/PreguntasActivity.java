package com.example.asus.movilgps.activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.asus.movilgps.adapters.RespuestaAdapter;
import com.example.asus.movilgps.models.Evento;
import com.example.asus.movilgps.models.Pregunta;
import com.example.asus.movilgps.models.Respuesta;
import com.example.asus.movilgps.models.Resultado;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static java.sql.Types.NUMERIC;

public class PreguntasActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

   ProgressDialog progreso;
   Context context;
   private PreguntaAdapte adapter;

   //    Realm
   private Realm realm;
   private RealmResults<Pregunta> preguntas, preguntasEstado;
   private RealmResults<Respuesta> respuestas;
   private RealmResults<Resultado> resultados;
   private RealmResults<Evento> eventos;


   ListView listView;
   private int idEvento;
   private int idEncuesta;
   Spinner spinnerRespuestas;
   private String resultado = "";

   int positionR = 0;



    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    AlertDialog dialog;
    private RespuestaAdapter adapterR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preguntas);

        realm = Realm.getDefaultInstance();

        request = Volley.newRequestQueue(getApplicationContext());
        context = PreguntasActivity.this;
        listView = findViewById(R.id.listViewPreguntas);
        listView.setOnItemClickListener(this);

        idEvento = getIntent().getExtras().getInt("evento");
        idEncuesta = getIntent().getExtras().getInt("idEncuesta");
        preguntas = realm.where(Pregunta.class).equalTo("encuesta2", idEncuesta).findAll();

        //cargarWebServicePreguntas(idEncuesta);
        obtenerList();

    }

    private void obtenerList() {
        adapter= new PreguntaAdapte(this, preguntas,R.layout.list_view_pregunta_item);
        listView.setAdapter(adapter);
    }

    private void cargarWebServiceRegistro_Coo_Ima(final String latitude, final String longitude, final String idEncuesta, final String imagen, final int idEven, final Evento evento) {
        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONRegistroEvento.php?";
        resultados = realm.where(Resultado.class).findAll();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.trim().equalsIgnoreCase("Noregistra")){
                    Toast.makeText(getApplicationContext(), "Servidor lento error al enviar datos", Toast.LENGTH_SHORT).show();
                }else if(response.trim().equalsIgnoreCase("ErrorBaseDatos")){
                    Toast.makeText(getApplicationContext(), "Servidor lento error al enviar datos", Toast.LENGTH_SHORT).show();
                }else{
                    String idEvento= response.toString();
                    JSONObject jsonObject = new JSONObject();


                    for (int i = 0; i<resultados.size(); i++){
                        try {
                            jsonObject.put("evento", idEvento);
                            jsonObject.put("resultado", resultados.get(i).getResultado());
                            jsonObject.put("respuesta", resultados.get(i).getRespuesta());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    deleteEvento(evento);
                    Toast.makeText(getApplicationContext(), "Hecho", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Servidor lento error al enviar datos", Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> paramentros = new HashMap<>();
                paramentros.put("encuesta", idEncuesta);
                paramentros.put("cx",latitude);
                paramentros.put("cy",longitude);
                paramentros.put("imagen", imagen);
                return paramentros;
            }
        };
        request.add(stringRequest);
    }

    /*----------  REALM   -----------*/
    //INSERT
    private void insertResultado(int evento, String rsta, int respuesta) {
        realm.beginTransaction();
        Resultado resultado = new Resultado(evento,rsta,respuesta);
        realm.copyToRealm(resultado);
        realm.commitTransaction();
    }
    private void updatePreguntaEstado(int estado, Pregunta pregunta) {
        realm.beginTransaction();
        pregunta.setEstado(estado);
        realm.copyToRealmOrUpdate(pregunta);
        realm.commitTransaction();
    }

    //DELETE
    private void deleteEvento(Evento evento) {

        realm.beginTransaction();
        evento.deleteFromRealm();
        realm.commitTransaction();

    }

    private void showAlertSpinnerRespuestas(String title, final int tipo_pre, final Pregunta pregunta) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        final View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_spinner_respuestas, null);
        builder.setView(viewInflated);

        final EditText EditRespuesta = (EditText)viewInflated.findViewById(R.id.editTextRespuesta);
        if(tipo_pre==1){

        }else{
            EditRespuesta.setVisibility(View.INVISIBLE);
            spinnerRespuestas = viewInflated.findViewById(R.id.spinnerSeleRespuesta);
            spinnerRespuestas.setVisibility(View.VISIBLE);
            adapterR= new RespuestaAdapter(this,respuestas,R.layout.spinner_view_encuesta);
            spinnerRespuestas.setAdapter(adapterR);

            spinnerRespuestas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(respuestas.get(position).getTipo_dato().equals("numerico")){
                        EditRespuesta.setVisibility(View.VISIBLE);
                        EditRespuesta.setInputType(NUMERIC);
                    }else if (respuestas.get(position).getTipo_dato().equals("multiple")){
                        EditRespuesta.setVisibility(View.INVISIBLE);
                    }else if(respuestas.get(position).getTipo_dato().equals("texto")){
                        EditRespuesta.setVisibility(View.VISIBLE);
                    }
                    positionR=position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if(tipo_pre==1){
                    String resul = EditRespuesta.getText().toString().trim();
                    if(resul.length() > 0){
                        insertResultado(idEvento,resul,respuestas.get(0).getId_resp());
                        Toast.makeText(getApplicationContext(), "Registro exitoso.", Toast.LENGTH_LONG).show();
                        updatePreguntaEstado(1, pregunta);
                        obtenerList();
                    }else{
                        Toast.makeText(getApplicationContext(), "Tienes que escribir una respuesta", Toast.LENGTH_LONG).show();
                    }
                }else if(tipo_pre==2){
                    String resul = String.valueOf(respuestas.get(positionR).getId_resp());
                    insertResultado(idEvento,resul,respuestas.get(positionR).getId_resp());
                    Toast.makeText(getApplicationContext(), "Registro exitoso.", Toast.LENGTH_LONG).show();
                    updatePreguntaEstado(1, pregunta);
                    obtenerList();
                }
                preguntasEstado = realm.where(Pregunta.class).equalTo("estado",0).equalTo("encuesta2", idEncuesta).findAll();
                if(preguntasEstado.size()==0){
                    eventos = realm.where(Evento.class).findAll();

                    for(int i=0; i<eventos.size(); i++){
                        String idEncuesta = String.valueOf(eventos.get(i).getEncuesta());
                        cargarWebServiceRegistro_Coo_Ima(eventos.get(i).getLatitud(),
                                eventos.get(i).getLongitud(),
                                idEncuesta,
                                eventos.get(i).getImagen(), eventos.get(i).getId(), eventos.get(i));
                    }
                    Intent i = new Intent(PreguntasActivity.this, UltimaActivity.class);
                    startActivity(i);
                    finish();
                }

            }

        });

        dialog = builder.create();
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void obtenerlistRespuesta(String pregunta, int position) {
       /* listaRespuestas = new ArrayList<String>();

        listaRespuestas.add("Seleccione una respuesta");
        for(int i=0; i<respuestas.size(); i++) {
            listaRespuestas.add(*//*respuestas.get(i).getId_resp() + " - " + *//*respuestas.get(i).getNombre_resp());
        }
       showAlertSpinnerRespuestas(pregunta ,position);*/
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(preguntas.get(position).getEstado() != 1){
            int idPre = preguntas.get(position).getId_pregunta();
            String pregunta = String.valueOf(preguntas.get(position).getNombre_pre());
            respuestas = realm.where(Respuesta.class).equalTo("pregunta", idPre).findAll();

            if(respuestas.size() != 0){
                showAlertSpinnerRespuestas(pregunta, preguntas.get(position).getTipo_pre(), preguntas.get(position));
            }else{
                Toast.makeText(getApplicationContext(), "No tiene respuestas!!!!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Esta pregunta ya esta solucionada!!!!", Toast.LENGTH_SHORT).show();
        }

    }
}
