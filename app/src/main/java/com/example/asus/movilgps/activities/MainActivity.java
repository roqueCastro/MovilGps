package com.example.asus.movilgps.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.asus.movilgps.adapters.EncuestaAdapter;
import com.example.asus.movilgps.models.Contacto;
import com.example.asus.movilgps.models.Encuesta;
import com.example.asus.movilgps.models.Encuestas;
import com.example.asus.movilgps.models.Pregunta;
import com.example.asus.movilgps.models.Respuesta;
import com.example.asus.movilgps.models.validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.LogRecord;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.FileProvider.getUriForFile;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>,
        Response.ErrorListener{

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    private static final int COD_SELECIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;

    private final String carpeta_raiz="AppSig/";
    private final String ruta_imagen=carpeta_raiz+"imagenes";
    String path;
    String msj;
    Bitmap bitmap;
    int permissionCheck;
    int timeMensAler;
    int selec=0;

    final long PERIODO = 60000; // 1 minuto
    private Handler handler;
    private Runnable runnable;

    TextView latitude,longitude,direccion, mensajeCon;
    Spinner spinner;
    Button btnEnvio;
    ProgressDialog progreso;
    Context context;
    FloatingActionButton gps;
    ImageView foto;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    ArrayList<validate> validates;
    private EncuestaAdapter adapter;
    StringRequest stringRequest;

//    Realm
    private Realm realm;
    private RealmResults<Contacto> contactos;
    private RealmResults<Encuesta> encuestas;
    private RealmResults<Pregunta> preguntas;
    private RealmResults<Respuesta> respuestas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      BD realm
        realm = Realm.getDefaultInstance();
        contactos = realm.where(Contacto.class).findAll();
        encuestas = realm.where(Encuesta.class).findAll();
        preguntas = realm.where(Pregunta.class).findAll();
        respuestas = realm.where(Respuesta.class).findAll();


        gps = findViewById(R.id.fabGps);
        latitude = findViewById(R.id.TextLatitud);
        longitude = findViewById(R.id.TextLongitud);
        direccion = findViewById(R.id.TextDireccion);
        spinner = findViewById(R.id.spinnerSeleEncu);
        btnEnvio = findViewById(R.id.btn_Enviar);
        foto = findViewById(R.id.foto);
        mensajeCon = findViewById(R.id.TextViewMensajeConfirmacion);

        context = MainActivity.this;
        request = Volley.newRequestQueue(getApplicationContext());
        btnEnvio.setEnabled(false);
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        obtenerList();

        cargarwebservice();
        locationStart();
        if(validaPermisosCamara()){
            foto.setEnabled(true);
        }else{
            foto.setEnabled(false);
        }

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                } else {
                    locationStart();
                }
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertFoto();
            }
        });

        btnEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationStart();


                if(selec != 0){

                    String idEncuesta = String.valueOf(selec);
                    String lat = latitude.getText().toString();
                    String lon = longitude.getText().toString();

                    if(lat != "" && lon != ""){
                        if(bitmap != null){
                            cargarWebServiceRegistro_Coo_Ima(lat, lon, idEncuesta);
                        }else{
                            msj = "Tienes que tomar una foto";
                            timeMensAler=5000;
                            mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorAccent));
                            mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                            mensajeAlertaTextView(msj,timeMensAler);
                        }

                    }else{
                        msj = "Espera mientras carga las coordenadas";
                        timeMensAler=5000;
                        mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorAccent));
                        mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                        mensajeAlertaTextView(msj,timeMensAler);
                    }

                }else{
                    msj = "Tienes que seleccionar un evento";
                    timeMensAler=3000;
                    mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorAccent));
                    mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                    mensajeAlertaTextView(msj,timeMensAler);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selec=encuestas.get(position).getId_encuesta();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    /*---------------------MENSAJES DE ALERTAS PARA EL SISTEMA---------------------*/
    private void mensajeAlertaTextView(String msj, int timeMensAler) {
        mensajeCon.setText(msj);
        mensajeCon.setVisibility(View.VISIBLE);
        esconderMensaje(timeMensAler);
    }

    private void esconderMensaje(int timeMensAler) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                       mensajeCon.setVisibility(View.INVISIBLE);
                    }
                }, timeMensAler);
    }

    /*----------------ALL WEBSERVICE-----------------------*/

    private void cargarwebservice() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "WSConsultaEncuestas.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                btnEnvio.setEnabled(true);

                validate validate = null;

                JSONArray json = response.optJSONArray("encuesta");

                try {
                    if(encuestas.size() == 0){
                        for (int i = 0; i < json.length(); i++) {

                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);
                            insertEncuesta(jsonObject.optInt("id_encuesta"), jsonObject.optString("nomb_encta"));
                        }
                    }else{
                        validates = new ArrayList<>();
                        for (int i = 0; i < json.length(); i++) {

                            validate = new validate();
                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);

                            validate.setId(jsonObject.optInt("id_encuesta"));
                            validate.setNombre(jsonObject.optString("nomb_encta"));
                            validates.add(validate);
                        }

                        if(validates.size() != encuestas.size()){
                            if(validates.size() > encuestas.size()){
                                int numero_agregar= validates.size()-encuestas.size();
                                int numero_encuestas= encuestas.size();

                                progreso = new ProgressDialog(context);
                                progreso.setMessage("Agregando datos...");
                                progreso.show();

                                for (int i=0; i<numero_agregar; i++){
                                    //Toast.makeText(getApplicationContext(), "Hay que agregar el s: "+ validates.get(numero_encuestas+i).getId().toString() + " - " + validates.get(numero_encuestas+i).getNombre(), Toast.LENGTH_SHORT).show();
                                    insertEncuesta(validates.get(numero_encuestas+i).getId(), validates.get(numero_encuestas+i).getNombre());
                                }
                                progreso.dismiss();
                                Toast.makeText(getApplicationContext(), "Se agregaron  "+ String.valueOf(numero_agregar)+ " encuestas.", Toast.LENGTH_SHORT).show();
                            }else if(validates.size() < encuestas.size()){
                                int numero_eliminar= encuestas.size()-validates.size();

                                for(int i=0; i<numero_eliminar; i++){
                                    deleteEncuesta(encuestas.get(i));
                                }

                                Toast.makeText(getApplicationContext(), "Se Eliminaron  "+ String.valueOf(numero_eliminar)+ " encuestas.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        for (int i=0; i<encuestas.size(); i++){
                            if(encuestas.get(i).getId_encuesta() != validates.get(i).getId()){
                                updateEncuesta(validates.get(i).getId(), validates.get(i).getNombre(), encuestas.get(i));
                            }else if(encuestas.get(i).getNombre_encuesta() != validates.get(i).getNombre()){
                                updateEncuesta(validates.get(i).getId(), validates.get(i).getNombre(), encuestas.get(i));
                            }
                        }
                    }

                    obtenerList();
                    cargarwebserviceAllPreguntas();

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                msj = "Error no hay conexion con la base de datos";
                timeMensAler=5000;
                mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.white));
                mensajeAlertaTextView(msj,timeMensAler);
                btnEnvio.setEnabled(false);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void cargarwebserviceAllPreguntas() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "WSConsultaAllPreguntas.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                btnEnvio.setEnabled(true);

                validate validate = null;

                JSONArray json = response.optJSONArray("preguntaAll");

                try {
                    if(preguntas.size() == 0){
                        for (int i = 0; i < json.length(); i++) {

                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);
                            insertPregunta(jsonObject.optInt("id_pgta"),
                                    jsonObject.optString("nomb_pgta"),
                                    jsonObject.optInt("tipo_pregunta"),
                                    jsonObject.optInt("encuesta2"));
                        }
                    }
                    else{
                        validates = new ArrayList<>();
                        for (int i = 0; i < json.length(); i++) {

                            validate = new validate();
                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);

                            validate.setId(jsonObject.optInt("id_pgta"));
                            validate.setNombre(jsonObject.optString("nomb_pgta"));
                            validate.setTipo(jsonObject.optInt("tipo_pregunta"));
                            validate.setEncuesta2(jsonObject.optInt("encuesta2"));
                            validates.add(validate);
                        }

                        if(validates.size() != preguntas.size()){
                            if(validates.size() > preguntas.size()){
                                int numero_agregar= validates.size()-preguntas.size();
                                int numero_encuestas= preguntas.size();

                                for (int i=0; i<numero_agregar; i++){
                                    //Toast.makeText(getApplicationContext(), "Hay que agregar el s: "+ validates.get(numero_encuestas+i).getId().toString() + " - " + validates.get(numero_encuestas+i).getNombre(), Toast.LENGTH_SHORT).show();
                                    insertPregunta(validates.get(numero_encuestas+i).getId(),
                                            validates.get(numero_encuestas+i).getNombre(),
                                            validates.get(numero_encuestas+i).getTipo(),
                                            validates.get(numero_encuestas+i).getEncuesta2());
                                }
                                Toast.makeText(getApplicationContext(), "Se agregaron  "+ String.valueOf(numero_agregar)+ " preguntas.", Toast.LENGTH_SHORT).show();
                            }else if(validates.size() < preguntas.size()){
                                int numero_eliminar= preguntas.size()-validates.size();

                                for(int i=0; i<numero_eliminar; i++){
                                    deletePregunta(preguntas.get(i));
                                }

                                Toast.makeText(getApplicationContext(), "Se Eliminaron  "+ String.valueOf(numero_eliminar)+ " preguntas.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        for (int i=0; i<preguntas.size(); i++){
                            if(preguntas.get(i).getId_pregunta() != validates.get(i).getId()){
                                updatePregunta(validates.get(i).getId(),
                                        validates.get(i).getNombre(),
                                        validates.get(i).getTipo(),
                                        validates.get(i).getEncuesta2(),
                                        preguntas.get(i));
                            }else if(preguntas.get(i).getNombre_pre() != validates.get(i).getNombre()){
                                updatePregunta(validates.get(i).getId(),
                                        validates.get(i).getNombre(),
                                        validates.get(i).getTipo(),
                                        validates.get(i).getEncuesta2(),
                                        preguntas.get(i));
                            }
                        }
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }

                cargarwebserviceAllRespuestas();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                msj = "Error no hay conexion con la base de datos";
                timeMensAler=5000;
                mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.white));
                mensajeAlertaTextView(msj,timeMensAler);
                btnEnvio.setEnabled(false);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void cargarwebserviceAllRespuestas() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "WSConsultaAllRespuesta.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();

                btnEnvio.setEnabled(true);

                validate validate = null;

                JSONArray json = response.optJSONArray("respuestaAll");

                try {
                    if(respuestas.size() == 0){
                        for (int i = 0; i < json.length(); i++) {

                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);
                            insertRespuesta(jsonObject.optInt("id_rpta"),
                                    jsonObject.optString("nomb_rpta"),
                                    jsonObject.optInt("pregunta"),
                                    jsonObject.optString("tipo_dato"));
                        }
                    }
                    else{
                        validates = new ArrayList<>();
                        for (int i = 0; i < json.length(); i++) {

                            validate = new validate();
                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);

                            validate.setId(jsonObject.optInt("id_rpta"));
                            validate.setNombre(jsonObject.optString("nomb_rpta"));
                            validate.setPregunta_resp(jsonObject.optInt("pregunta"));
                            validate.setTipo_dato(jsonObject.optString("tipo_dato"));
                            validates.add(validate);
                        }

                        if(validates.size() != respuestas.size()){
                            if(validates.size() > respuestas.size()){
                                int numero_agregar= validates.size()-respuestas.size();
                                int numero_encuestas= respuestas.size();

                                for (int i=0; i<numero_agregar; i++){
                                    //Toast.makeText(getApplicationContext(), "Hay que agregar el s: "+ validates.get(numero_encuestas+i).getId().toString() + " - " + validates.get(numero_encuestas+i).getNombre(), Toast.LENGTH_SHORT).show();
                                    insertRespuesta(validates.get(numero_encuestas+i).getId(),
                                            validates.get(numero_encuestas+i).getNombre(),
                                            validates.get(numero_encuestas+i).getPregunta_resp(),
                                            validates.get(numero_encuestas+i).getTipo_dato());
                                }
                                Toast.makeText(getApplicationContext(), "Se agregaron  "+ String.valueOf(numero_agregar)+ " preguntas.", Toast.LENGTH_SHORT).show();
                            }else if(validates.size() < respuestas.size()){
                                int numero_eliminar= respuestas.size()-validates.size();

                                for(int i=0; i<numero_eliminar; i++){
                                    deleteRespuesta(respuestas.get(i));
                                }

                                Toast.makeText(getApplicationContext(), "Se Eliminaron  "+ String.valueOf(numero_eliminar)+ " preguntas.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        for (int i=0; i<respuestas.size(); i++){
                            if(respuestas.get(i).getId_resp() != validates.get(i).getId()){
                                updateRespuesta(validates.get(i).getId(),
                                        validates.get(i).getNombre(),
                                        validates.get(i).getPregunta_resp(),
                                        validates.get(i).getTipo_dato(),
                                        respuestas.get(i));
                            }else if(respuestas.get(i).getNom_resp() != validates.get(i).getNombre()){
                                updateRespuesta(validates.get(i).getId(),
                                        validates.get(i).getNombre(),
                                        validates.get(i).getPregunta_resp(),
                                        validates.get(i).getTipo_dato(),
                                        respuestas.get(i));
                            }
                        }
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                msj = "Error no hay conexion con la base de datos";
                timeMensAler=5000;
                mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.white));
                mensajeAlertaTextView(msj,timeMensAler);
                btnEnvio.setEnabled(false);
            }
        });
        request.add(jsonObjectRequest);
    }

    private void cargarWebServiceContacto(String idEncuesta) {

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONConsultaContactos.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error al cargar los contactos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray json = response.optJSONArray("contacto");

       if(contactos.size()!=0){

       }
        try {

            for (int i = 0; i < json.length(); i++) {
                JSONObject jsonObject = null;

                jsonObject = json.getJSONObject(i);

                if (jsonObject.optInt("id_contacto") == 0) {
                    Toast.makeText(getApplicationContext(), "vacio", Toast.LENGTH_SHORT).show();
                } else {
                    insertContacto(jsonObject.optString("nomb_encta"),jsonObject.optString("telefono"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"No se a podido tener conexion con el servidor " , Toast.LENGTH_SHORT).show();
            progreso.hide();
        }
    }

    private void cargarWebServiceRegistro_Coo_Ima(final String latitude, final String longitude, final String idEncuesta) {

        msj = "Registrando...";
        timeMensAler=4000;
        mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorAccent));
        mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
        mensajeAlertaTextView(msj,timeMensAler);

        progreso= new ProgressDialog(context);
        progreso.setMessage("Enviando datos..");
        btnEnvio.setEnabled(false);
        progreso.show();

        String url = Utilidades_Request.HTTP+Utilidades_Request.IP+Utilidades_Request.CARPETA+"wsJSONRegistroEvento.php?";

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progreso.hide();
                btnEnvio.setEnabled(true);
                if(response.trim().equalsIgnoreCase("Noregistra")){
                    msj = "Error no registro...";
                    timeMensAler=4000;
                    mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                    mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.white));
                    mensajeAlertaTextView(msj,timeMensAler);
                }else if(response.trim().equalsIgnoreCase("ErrorBaseDatos")){
                    msj = "Error en la Insercion...";
                    timeMensAler=4000;
                    mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                    mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.white));
                    mensajeAlertaTextView(msj,timeMensAler);
                }else{
                    cargarWebServiceContacto(idEncuesta);
                    int idEnc = Integer.parseInt(idEncuesta);
                    String idEvento= response.toString();
                    Intent i = new Intent(MainActivity.this, PreguntasActivity.class);
                    i.putExtra("idEncuesta", idEnc);
                    i.putExtra("evento", idEvento);
                    startActivity(i);
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.hide();
                msj = "Servidor lento error al enviar datos";
                timeMensAler=5000;
                mensajeCon.setBackgroundColor(mensajeCon.getContext().getResources().getColor(R.color.colorPrimaryDark));
                mensajeCon.setTextColor(mensajeCon.getContext().getResources().getColor(R.color.white));
                mensajeAlertaTextView(msj,timeMensAler);
                Log.i("Error", error.toString());
                btnEnvio.setEnabled(true);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                String imagen =convertirImgString(bitmap);

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

    /*-------------CRUD REALM----------------*/

    //DELETE ALL
    private void deleteAll() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    private void deleteEncuesta(Encuesta encuesta) {

        realm.beginTransaction();
        encuesta.deleteFromRealm();
        realm.commitTransaction();

    }

    private void deletePregunta(Pregunta pregunta) {

        realm.beginTransaction();
        pregunta.deleteFromRealm();
        realm.commitTransaction();

    }
    private void deleteRespuesta(Respuesta respuesta) {

        realm.beginTransaction();
        respuesta.deleteFromRealm();
        realm.commitTransaction();

    }

    //INSERT

    private void insertContacto(String encuesta, String telefono) {
        realm.beginTransaction();
        Contacto contacto = new Contacto(encuesta,telefono);
        realm.copyToRealm(contacto);
        realm.commitTransaction();
    }

    private void  insertEncuesta(int idEnc, String nombEn){
        realm.beginTransaction();
        Encuesta encuesta = new Encuesta(idEnc,nombEn);
        realm.copyToRealm(encuesta);
        realm.commitTransaction();
    }

    private void  insertPregunta(int id_pre, String nomb_pre,int tipo, int encuesta){
        realm.beginTransaction();
        Pregunta pregunta = new Pregunta(id_pre,nomb_pre,tipo,encuesta);
        realm.copyToRealm(pregunta);
        realm.commitTransaction();
    }

    private void  insertRespuesta(int id_resp, String nomb_resp,int pregunta, String tipo_dato){
        realm.beginTransaction();
        Respuesta respuesta = new Respuesta(id_resp,nomb_resp,pregunta,tipo_dato);
        realm.copyToRealm(respuesta);
        realm.commitTransaction();
    }

    //UPDATE

    private void updateEncuesta(int id, String nom, Encuesta encuesta) {
        realm.beginTransaction();
        encuesta.setId_encuesta(id);
        encuesta.setNombre_encuesta(nom);
        realm.copyToRealmOrUpdate(encuesta);
        realm.commitTransaction();
    }
    private void updatePregunta(int id, String nom, int tipo, int encuesta2, Pregunta pregunta) {
        realm.beginTransaction();
        pregunta.setId_pregunta(id);
        pregunta.setNombre_pre(nom);
        pregunta.setTipo_pre(tipo);
        pregunta.setEncuesta2(encuesta2);
        realm.copyToRealmOrUpdate(pregunta);
        realm.commitTransaction();
    }
    private void updateRespuesta(int id, String nom, int pregunta, String tipo_dato, Respuesta respuesta) {
        realm.beginTransaction();
        respuesta.setId_resp(id);
        respuesta.setNom_resp(nom);
        respuesta.setPregunta(pregunta);
        respuesta.setTipo_dato(tipo_dato);
        realm.copyToRealmOrUpdate(respuesta);
        realm.commitTransaction();
    }

    /*-------------LOCATION GPS----------------*/

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        gpsEnaDis();
        onstartGos(mlocManager,Local);
    }



    // CLASE LOCATION
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        /*public MainActivity getMainActivity() {
            return mainActivity;
        }*/

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            String lat = String.valueOf(loc.getLatitude());
            String lon = String.valueOf(loc.getLongitude());
            latitude.setText(lat);
            longitude.setText(lon);

      //      this.mainActivity.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "Activo",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

    }


    //Obtiene Direccion
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    String calle = DirCalle.getAddressLine(0);
                    direccion.setText(calle);
                    gps.setEnabled(true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private boolean validaPermisosCamara()
    {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M) {
            return true;
        }

        if((checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)) {
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))){
            cargarDialogoRecomendation();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
        }

        return false;
    }

    private void cargarDialogoRecomendation() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Permisos desactivados..");
        dialog.setMessage("Debes aceptar los permisos CAMERA y MEMORIA");

        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });
        dialog.show();
    }

    private void showAlertFoto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Elige una opcion");
        final CharSequence[] opciones = {"Tomar Foto","Elegir Galeria","Cancelar"};

        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (opciones[i].equals("Tomar Foto")) {
                    try {
                        abrirCamara();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(opciones[i].equals("Elegir Galeria")){

                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/");
                        startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECIONA);
                }else{
                    dialog.dismiss();
                }

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void abrirCamara() throws IOException {

        File fileImagen = new File(Environment.getExternalStorageDirectory(), ruta_imagen);
        boolean isCreada= fileImagen.exists();
        String nombreImagen = "";

        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen = (System.currentTimeMillis()/10000)+".jpg";
        }

        path = Environment.getExternalStorageDirectory()+
                File.separator+ruta_imagen+File.separator+nombreImagen;

        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities = getApplicationContext().getPackageName()+".provider";
            Uri imageUri = FileProvider.getUriForFile(MainActivity.this,
                    authorities,
                   imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }

        startActivityForResult(intent, COD_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==COD_FOTO || requestCode==COD_SELECIONA)  {
            if(resultCode != 0){
                switch (requestCode){
                    case COD_SELECIONA:
                        Uri miPath = data.getData();

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), miPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case COD_FOTO:

                        MediaScannerConnection.scanFile(this, new String[]{path}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("Ruta de almacenamiento", "Path: " + path);
                                    }
                                });

                        bitmap = BitmapFactory.decodeFile(path);

                        break;
                }

                bitmap=redimensionarImagen(bitmap, 350, 420);


                foto.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        if(ancho>anchoNuevo || alto>altoNuevo){
            float escalaAncho = anchoNuevo/ancho;
            float escalaAlto = altoNuevo/alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);

            return Bitmap.createBitmap(bitmap,0,0, ancho, alto, matrix, true);
        }else{
            return bitmap;
        }
    }



    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
    }



    private void obtenerList() {
        adapter= new EncuestaAdapter(this,encuestas,R.layout.spinner_view_encuesta);
        spinner.setAdapter(adapter);
    }

    private void gpsEnaDis() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            showAlertGPS("ACTIVAR", "GPS");
        }
    }



    // permisos de ubicacion
    private void onstartGos(LocationManager mlocManager, Localizacion Local) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }

        if (requestCode == 100) {
            if (grantResults.length==2 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                foto.setEnabled(true);
            }else{
                Toast.makeText(context, " No se activaron permisos de CAMARA", Toast.LENGTH_SHORT).show();
                cargarDialogoRecomendation();
            }
        }
    }

    private void solicitarPermisoManual() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permisos de camara Manuales");
        final CharSequence[] opciones = {"Si","No"};

        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (opciones[i].equals("Si")) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }





    /**    Dialogs ALERT SALIR **/
    private void showSalir(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                MainActivity.super.finish();
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showAlertGPS(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        builder.setPositiveButton("Activar gps", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.upload:
                cargarwebservice();
                // startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            //super.onBackPressed();
            showSalir("Quieres salir?", "Deseas dejar esta aplicacion");

            return;
        }else {
            Toast.makeText(this, "Presionar dos veces para salir", Toast.LENGTH_SHORT).show();
          // cargarwebservice();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }
}
