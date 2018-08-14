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
import com.example.asus.movilgps.models.Encuestas;
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

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.support.v4.content.FileProvider.getUriForFile;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    private static final int COD_SELECIONA = 10;
    private static final int COD_FOTO = 20;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1 ;

    private final String carpeta_raiz="misImagenes/";
    private final String ruta_imagen=carpeta_raiz+"misFotos";
    String path;
    Bitmap bitmap;
    int permissionCheck;

    final long PERIODO = 60000; // 1 minuto
    private Handler handler;
    private Runnable runnable;

    TextView latitude,longitude,direccion;
    Spinner spinner;
    Button btnEnvio;
    ProgressDialog progreso;
    Context context;
    FloatingActionButton gps;
    ImageView foto;


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    ArrayList<String> listaEventos;
    ArrayList<Encuestas> encuestass;
    ArrayList<validate> validates;
    ArrayAdapter<CharSequence> adapter;
    StringRequest stringRequest;

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
        foto = findViewById(R.id.foto);

        context = MainActivity.this;
        encuestass= new ArrayList<>();
        request = Volley.newRequestQueue(getApplicationContext());
        btnEnvio.setEnabled(false);
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

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

                final int selec=spinner.getSelectedItemPosition();
                if(selec!= 0){

                    String idEncuesta = encuestass.get(selec-1).getId_encuesta().toString();
                    String lat = latitude.getText().toString();
                    String lon = longitude.getText().toString();

                    if(lat != "" && lon != ""){
                        if(bitmap != null){
                            cargarWebServiceRegistro_Coo_Ima(lat, lon, idEncuesta);
                        }else{
                            Toast.makeText(context, "Tienes que tomar una foto!..", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(context, "Espera a que cargue las coordenadas", Toast.LENGTH_SHORT).show();
                    }

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

    /*----------------ALL WEBSERVICE-----------------------*/

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

    private void cargarWebServiceRegistro_Coo_Ima(final String latitude, final String longitude, final String idEncuesta) {

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
                }else{
                    String idEvento= response.toString();
                    Intent i = new Intent(MainActivity.this, PreguntasActivity.class);
                    i.putExtra("idEncuesta", idEncuesta);
                    i.putExtra("evento", idEvento);
                    startActivity(i);
                    finish();
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
                bitmap=redimensionarImagen(bitmap, 350, 400);
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
        listaEventos = new ArrayList<String>();

        listaEventos.add("Seleccione tipo encuesta");
        for(int i=0; i<encuestass.size(); i++) {
            listaEventos.add(/*encuestass.get(i).getId_encuesta() + " - " +*/ encuestass.get(i).getNombre_encuesta());
        }

        adapter= new ArrayAdapter(this, android.R.layout.simple_spinner_item,listaEventos);
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
