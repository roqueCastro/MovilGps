package com.example.asus.movilgps.activities;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.asus.movilgps.adapters.PreguntaAdapte;
import com.example.asus.movilgps.adapters.RespuestaAdapter;
import com.example.asus.movilgps.models.Pregunta;
import com.example.asus.movilgps.models.Respuesta;
import com.example.asus.movilgps.models.Resultado;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.sql.Types.NUMERIC;

public class PreguntasActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

   ProgressDialog progreso;
   Context context;
   private PreguntaAdapte adapter;

   //    Realm
   private Realm realm;
   private RealmResults<Pregunta> preguntas;
   private RealmResults<Respuesta> respuestas;


   ListView listView;
   private String idEvento;
   private int idEncuesta;
   private String resultado = "";
   Spinner spinnerRespuestas;
   int posision = 900000;



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

        idEvento = getIntent().getStringExtra("evento");
        idEncuesta = getIntent().getExtras().getInt("idEncuesta");
        preguntas = realm.where(Pregunta.class).equalTo("encuesta2", idEncuesta).findAll();

        //cargarWebServicePreguntas(idEncuesta);
        obtenerList();

    }

    private void obtenerList() {
        adapter= new PreguntaAdapte(this, preguntas,R.layout.list_view_pregunta_item);
        listView.setAdapter(adapter);
    }

    private void insertResultado(int evento, String rsta, int respuesta) {
        realm.beginTransaction();
        Resultado resultado = new Resultado(evento,rsta,respuesta);
        realm.copyToRealm(resultado);
        realm.commitTransaction();
    }

    private void showAlertSpinnerRespuestas(String title, final int tipo_pre) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        final View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_spinner_respuestas, null);
        builder.setView(viewInflated);

        final EditText EditRespuesta = (EditText)viewInflated.findViewById(R.id.editTextRespuesta);
        if(tipo_pre==1){

        }else{
            EditRespuesta.setVisibility(View.INVISIBLE);
            spinnerRespuestas = viewInflated.findViewById(R.id.spinnerSeleRespuesta);
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
                        //  insertResultado(idEvento,resul,);
                    }else{
                        Toast.makeText(getApplicationContext(), "Tienes que escribir una respuesta", Toast.LENGTH_LONG).show();
                    }
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

        int idPre = preguntas.get(position).getId_pregunta();
        String pregunta = String.valueOf(preguntas.get(position).getNombre_pre());
        respuestas = realm.where(Respuesta.class).equalTo("pregunta", idPre).findAll();

        if(respuestas.size() != 0){
            showAlertSpinnerRespuestas(pregunta, preguntas.get(position).getTipo_pre());
        }else{
            Toast.makeText(getApplicationContext(), "No tiene respuestas!!!!", Toast.LENGTH_SHORT).show();
        }

    }
}
