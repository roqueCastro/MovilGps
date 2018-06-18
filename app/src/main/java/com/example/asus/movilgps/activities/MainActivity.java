package com.example.asus.movilgps.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.asus.movilgps.R;
import com.example.asus.movilgps.Utilidades.Utilidades_Request;
import com.example.asus.movilgps.models.Encuestas;
import com.example.asus.movilgps.models.validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    final long PERIODO = 60000; // 1 minuto
    private Handler handler;
    private Runnable runnable;

    TextView latitude,longitude,direccion;
    Spinner spinner;
    Button btnEnvio;
    ProgressDialog progreso;
    Context context;
    FloatingActionButton gps;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    ArrayList<String> listaEventos;
    ArrayList<Encuestas> encuestass;
    ArrayList<validate> validates;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = findViewById(R.id.fabGps);
        latitude = findViewById(R.id.TextLatitud);
        longitude = findViewById(R.id.TextLongitud);
        direccion = findViewById(R.id.TextDireccion);
        spinner = findViewById(R.id.spinnerSeleEncu);
        btnEnvio = findViewById(R.id.btn_Enviar);

        context = MainActivity.this;
        encuestass= new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());
        btnEnvio.setEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
        cargarwebservice();

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationStart();
                gps.setEnabled(false);
            }
        });

        btnEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationStart();

                final int selec=spinner.getSelectedItemPosition();
                if(selec!= 0){
                        progreso = new ProgressDialog(context);
                        progreso.setMessage("Cargando coordenadas..");
                        progreso.show();
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {

                                        String idEncuesta = encuestass.get(selec-1).getId_encuesta().toString();
                                        String lat = latitude.getText().toString();
                                        String lon = longitude.getText().toString();


                                        Intent i = new Intent(MainActivity.this, PreguntasActivity.class);
                                        i.putExtra("latitud", lat);
                                        i.putExtra("longitud", lon);
                                        i.putExtra("idEncuesta", idEncuesta);
                                        startActivity(i);

                                        progreso.dismiss();
                                    }
                                }, 3000);
                    }else{
                        Toast.makeText(context, "Tienes que seleccionar un evento", Toast.LENGTH_SHORT).show();
                        spinner.setFocusable(true);
                    }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void cargarwebservice() {
        String url = Utilidades_Request.HTTP + Utilidades_Request.IP + Utilidades_Request.CARPETA + "WSConsultaEncuestas.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
               // Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                btnEnvio.setEnabled(true);
                 Encuestas encuestas = null;
                 validate validate = null;

                JSONArray json = response.optJSONArray("encuesta");

                try {
                    if(encuestass.size() == 0){
                        for (int i = 0; i < json.length(); i++) {

                            encuestas = new Encuestas();
                            JSONObject jsonObject = null;
                            jsonObject = json.getJSONObject(i);

                            encuestas.setId_encuesta(jsonObject.optInt("id_encuesta"));
                            encuestas.setNombre_encuesta(jsonObject.optString("nomb_encta"));
                            encuestass.add(encuestas);
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

                        if(validates.size()!=encuestass.size()){
                            if(validates.size()>encuestass.size()){
                                int numero_agregar= validates.size()-encuestass.size();
                                int numero_encuestas= encuestass.size();

                                progreso = new ProgressDialog(context);
                                progreso.setMessage("Agregando datos...");
                                progreso.show();

                                for (int i=0; i<numero_agregar; i++){
                                    //Toast.makeText(getApplicationContext(), "Hay que agregar el s: "+ validates.get(numero_encuestas+i).getId().toString() + " - " + validates.get(numero_encuestas+i).getNombre(), Toast.LENGTH_SHORT).show();
                                    encuestas = new Encuestas();
                                    encuestas.setId_encuesta(validates.get(numero_encuestas+i).getId());
                                    encuestas.setNombre_encuesta(validates.get(numero_encuestas+i).getNombre());
                                    encuestass.add(encuestas);
                                }
                                progreso.dismiss();
                                Toast.makeText(getApplicationContext(), "Se agregaron  "+ String.valueOf(numero_agregar)+ " encuestas.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    obtenerList();

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Error webservice. \n"+
                        "No hay conexion con la base de datos.", Toast.LENGTH_SHORT).show();
                btnEnvio.setEnabled(false);
            }
        });
            request.add(jsonObjectRequest);
    }

    private void obtenerList() {
        listaEventos = new ArrayList<String>();

        listaEventos.add("Seleccione tipo encuesta");
        for(int i=0; i<encuestass.size(); i++) {
            listaEventos.add(/*encuestass.get(i).getId_encuesta() + " - " +*/ encuestass.get(i).getNombre_encuesta());
        }

        adapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,listaEventos);
        spinner.setAdapter(adapter);
    }

    private void gpsEnaDis() {
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            showAlertGPS("ACTIVAR", "GPS");
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        gpsEnaDis();
        onstartGos(mlocManager,Local);
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
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


    // CLASE LOCATION
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public MainActivity getMainActivity() {
            return mainActivity;
        }

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

            this.mainActivity.setLocation(loc);
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
