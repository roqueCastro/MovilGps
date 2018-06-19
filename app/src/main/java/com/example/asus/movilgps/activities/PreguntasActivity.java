package com.example.asus.movilgps.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.lang.UCharacter;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.asus.movilgps.models.Encuestas;
import com.example.asus.movilgps.models.Pregunta;
import com.example.asus.movilgps.models.Respuesta;
import com.example.asus.movilgps.models.validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.NumericType.NUMERIC;

public class PreguntasActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener, AdapterView.OnItemClickListener{

    ProgressDialog progreso;
    Context context;
   // ArrayList<Encuestas> encuestass;
   ArrayList<Pregunta> preguntas;
   ArrayList<Respuesta> respuestas;
   ArrayList<validate> validates;
    ArrayList<String> listaRespuestas;
   private PreguntaAdapte adapter;

   ListView listView;
   private String idEvento;
   private String idEncuesta;
   private String resultado = "";
   Spinner spinnerPreguntas;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    StringRequest stringRequest;

    AlertDialog dialog;

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
        idEncuesta = getIntent().getStringExtra("idEncuesta");

        cargarWebService(Latitude, Longitude, idEncuesta);

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
                   // Toast.makeText(context,"Registro Exitoso "+response.toString(), Toast.LENGTH_SHORT).show();
                    idEvento= response.toString();
                    cargarWebServicePreguntas(idEncuesta);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                Toast.makeText(context,"Ocurrio un error en el servidor " + error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("Error", error.toString());
                Intent intent = new Intent(PreguntasActivity.this, MainActivity.class);
                startActivity(intent);
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
        validate validate = null;


        JSONArray json = response.optJSONArray("pregunta");

        try {
            if(preguntas.size() == 0){
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
                        pregunta.setEstado(0);
                        preguntas.add(pregunta);
                    }
                }
            }/*else {
                validates = new ArrayList<>();
                for (int i = 0; i < json.length(); i++) {

                    validate = new validate();
                    JSONObject jsonObject = null;
                    jsonObject = json.getJSONObject(i);

                    validate.setId(jsonObject.optInt("id_pgta"));
                    validate.setNombre(jsonObject.optString("nomb_pgta"));
                    validate.setTipo(jsonObject.optInt("tipo_pregunta"));
                    validates.add(validate);
                }

                Toast.makeText(getApplicationContext(), "Listo validate ", Toast.LENGTH_SHORT).show();
                if (validates.size() != preguntas.size()) {
                    if (validates.size() > preguntas.size()) {
                        int numero_agregar = validates.size() - preguntas.size();
                        int numero_preguntas = preguntas.size();

                        Toast.makeText(getApplicationContext(), "faltan  " + String.valueOf(numero_agregar) + " por agregar.", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < numero_agregar; i++) {
                            //Toast.makeText(getApplicationContext(), "Hay que agregar el s: "+ validates.get(numero_encuestas+i).getId().toString() + " - " + validates.get(numero_encuestas+i).getNombre(), Toast.LENGTH_SHORT).show();
                            pregunta = new Pregunta();
                            pregunta.setId_pre(validates.get(numero_preguntas + i).getId());
                            pregunta.setNombre_pre(validates.get(numero_preguntas + i).getNombre());
                            pregunta.setTipo_pre(validates.get(numero_preguntas + i).getTipo());
                            pregunta.setEstado(0);
                            preguntas.add(pregunta);
                        }
                    }

                }
            }*/

            progreso.hide();

            adapter  = new PreguntaAdapte(this, preguntas, R.layout.list_view_pregunta_item);
            //Toast.makeText(context, adapter.toString(), Toast.LENGTH_SHORT).show();

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(this);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showAlertSpinnerRespuestas(String title, String id_pre, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_spinner_respuestas, null);
        builder.setView(viewInflated);

        spinnerPreguntas = viewInflated.findViewById(R.id.spinnerSeleRespuesta);
        final EditText EditRespuesta = (EditText)viewInflated.findViewById(R.id.editTextRespuesta);

        EditRespuesta.setVisibility(View.INVISIBLE);
        spinnerPreguntas.setVisibility(View.INVISIBLE);

        final int tipo =  preguntas.get(position).getTipo_pre();
        if(tipo==1){
            spinnerPreguntas.setVisibility(View.VISIBLE);
            EditRespuesta.setVisibility(View.VISIBLE);
            EditRespuesta.setInputType(NUMERIC);
            cargarWebServiceRespuestas(id_pre);
        }else{
            spinnerPreguntas.setVisibility(View.VISIBLE);
            cargarWebServiceRespuestas(id_pre);
        }


        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tipo==1){
                    if(EditRespuesta.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "Ingresa en el campo!!", Toast.LENGTH_SHORT).show();
                    }else if (spinnerPreguntas.getSelectedItemPosition()==0) {
                        Toast.makeText(getApplicationContext(), "Seleccione la respuesta a enviar ", Toast.LENGTH_SHORT).show();
                    }else{
                        int selec = spinnerPreguntas.getSelectedItemPosition();
                        String idRespuesta = String.valueOf(respuestas.get(selec-1).getId_resp());
                        resultado=EditRespuesta.getText().toString();
                        cargarWebServiceRegistroResultado(idRespuesta,idEvento, position);
                    }
                }else{
                    int selec = spinnerPreguntas.getSelectedItemPosition();
                    if(selec  != 0){
                        String idRespuesta = String.valueOf(respuestas.get(selec-1).getId_resp());
                        cargarWebServiceRegistroResultado(idRespuesta,idEvento, position);
                    }else{
                        Toast.makeText(getApplicationContext(), "Debes seleccionar una respuesta.!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });

        dialog = builder.create();
        dialog.show();
    }

    private void cargarWebServiceRegistroResultado(final String idRespuesta, final String idEvento, final int position) {
        progreso= new ProgressDialog(context);
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

                    if(preguntas.size()==0){
                        Intent intent = new Intent(PreguntasActivity.this, UltimaActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        cargarWebServicePreguntas(idEncuesta);
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
        request.add(stringRequest);
    }

    private void cargarWebServiceRespuestas(String id_pre) {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "wsJSONConsultaRespuestas.php?id_pregunta="+id_pre;

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                respuestas = new ArrayList<>();
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
                                respuestas.add(respuesta);
                            }

                        }

                        obtenerlistRespuesta();

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Error webservice. \n"+
                        "No hay conexion con la base de datos.", Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonObjectRequest);
    }

    private void obtenerlistRespuesta() {
        listaRespuestas = new ArrayList<String>();

        listaRespuestas.add("Seleccione una respuesta");
        for(int i=0; i<respuestas.size(); i++) {
            listaRespuestas.add(/*respuestas.get(i).getId_resp() + " - " + */respuestas.get(i).getNombre_resp());
        }

        ArrayAdapter<CharSequence> adapter =  new ArrayAdapter
                (this,android.R.layout.simple_spinner_item,listaRespuestas);
        spinnerPreguntas.setAdapter(adapter);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String id_pre = String.valueOf(preguntas.get(position).getId_pre());
        String pregunta = String.valueOf(preguntas.get(position).getNombre_pre());

        showAlertSpinnerRespuestas(pregunta, id_pre, position);
    }
}
