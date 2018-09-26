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
import com.example.asus.movilgps.models.Contacto;
import com.example.asus.movilgps.models.Pregunta;
import com.example.asus.movilgps.models.Respuesta;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PreguntasActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

   ProgressDialog progreso;
   Context context;
   private PreguntaAdapte adapter;

   //    Realm
   private Realm realm;
   private RealmResults<Pregunta> preguntas;


   ListView listView;
   private String idEvento;
   private int idEncuesta;
   private String resultado = "";
   Spinner spinnerRespuestas;
   private TextView nom_pre_abie;
   int valorRta;
   int posision = 900000;
   String Latitude;
   String Longitude;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    AlertDialog dialog;


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

    private void showAlertSpinnerRespuestas(String title, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        final View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_spinner_respuestas, null);
        builder.setView(viewInflated);

        spinnerRespuestas = viewInflated.findViewById(R.id.spinnerSeleRespuesta);
        final EditText EditRespuesta = (EditText)viewInflated.findViewById(R.id.editTextRespuesta);

       // ArrayAdapter<CharSequence> adapter =  new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaRespuestas);
        spinnerRespuestas.setAdapter(adapter);

        spinnerRespuestas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* if(position!=0) {

                    EditRespuesta.setVisibility(viewInflated.VISIBLE);
                    int posisionRespuesta = position - 1;
                   // String tipoRespuesta = respuestas.get(posisionRespuesta).getTipo_dato();


                    if(tipoRespuesta.equals("numerico")){
                        EditRespuesta.setInputType(NUMERIC);
                    }else if (tipoRespuesta.equals("multiple")){
                        EditRespuesta.setText(respuestas.get(posisionRespuesta).getNombre_resp());
                        EditRespuesta.setEnabled(false);
                    }else if(tipoRespuesta.equals("texto")){

                    }

                }else{
                    EditRespuesta.setVisibility(viewInflated.INVISIBLE);
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                /*if (spinnerRespuestas.getSelectedItemPosition() != 0) {

                    int pos = spinnerRespuestas.getSelectedItemPosition() - 1;
                    String tipo = respuestas.get(pos).getTipo_dato();

                    if (tipo.equals("numerico") || tipo.equals("texto")) {

                        if (EditRespuesta.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Ingresa en el campo!!", Toast.LENGTH_SHORT).show();
                        } else {
                            String idRespuesta = String.valueOf(respuestas.get(pos).getId_resp());
                            resultado = EditRespuesta.getText().toString();
                            cargarWebServiceRegistroResultado(idRespuesta, idEvento, position);
                        }

                    } else if (tipo.equals("multiple")) {

                        String idRespuesta = String.valueOf(respuestas.get(pos).getId_resp());
                        cargarWebServiceRegistroResultado(idRespuesta, idEvento, position);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Selecciona alguna respuesta", Toast.LENGTH_SHORT).show();
                }*/
            }

        });

        dialog = builder.create();
        dialog.show();
    }

    private void cargarWebServiceRegistroResultado(final String idRespuesta, final String idEvento, final int position) {
        /*progreso= new ProgressDialog(context);
        progreso.setMessage("Registrando respuesta..");
        progreso.show();

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONRegistroResultado.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();


                if(response.trim().equalsIgnoreCase("Noregistra")){
                    Toast.makeText(context,"No registro ocurrio un error ", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(context,"Registro Exitoso. ", Toast.LENGTH_SHORT).show();
                    preguntas.remove(position);
                    resultado="";
                    if(preguntas.size()==0){
                        Intent intent = new Intent(PreguntasActivity.this, UltimaActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        //cargarWebServicePreguntas(idEncuesta);
                    }
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
                paramentros.put("idEvento", idEvento);
                paramentros.put("resultado",resultado);
                paramentros.put("idRespuesta",idRespuesta);
                return paramentros;
            }
        };
        request.add(stringRequest);*/
    }

    private void cargarWebServiceRespuestas(String idPre, final int position, final String pregunta) {
        progreso= new ProgressDialog(context);
        progreso.setMessage("Cargando respuestas..");
        progreso.show();

        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "wsJSONConsultaRespuestas.php?id_pregunta="+idPre;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                progreso.dismiss();
               // respuestas = new ArrayList<>();
                Respuesta respuesta = null;

                JSONArray json = response.optJSONArray("respuesta");

                try {
                        for (int i = 0; i < json.length(); i++) {

                            respuesta = new Respuesta();
                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);

                            if(jsonObject.optInt("id_rpta")!=0){
                                respuesta.setId_resp(jsonObject.optInt("id_rpta"));
                                respuesta.setNombre_resp(jsonObject.optString("nomb_rpta"));
                                respuesta.setTipo_pregunta(jsonObject.optString("tipo_pregunta"));
                                respuesta.setTipo_dato(jsonObject.optString("tipo_dato"));
                                //respuestas.add(respuesta);
                            }

                        }
                        obtenerlistRespuesta(pregunta,position);


                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.dismiss();
                Toast.makeText(context, "Error webservice. \n"+
                        "No hay conexion con la base de datos.", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
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
        Toast.makeText(context, "clic "+preguntas.get(position).getNombre_pre(), Toast.LENGTH_SHORT).show();

       // String id_pre = String.valueOf(preguntas.get(position).getId_pre());
       // String pregunta = String.valueOf(preguntas.get(position).getNombre_pre());

        //showAlertSpinnerRespuestas(pregunta, id_pre, position);
        /*if(posision != position){
            posision=position;
            cargarWebServiceRespuestas(id_pre, position, pregunta);
        }*/
        //cargarWebServiceRespuestas(id_pre, position, pregunta);
    }
}
