package com.example.asus.movilgps.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.adapters.ContactoAdapter;
import com.example.asus.movilgps.models.Contacto;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MensajeTextoActivity extends AppCompatActivity{

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    TextView tel;
    EditText sms;
    ImageButton envioSms;
    Spinner spinnerCont;
//    Realm
    private Realm realm;
    private RealmResults<Contacto> contactos;
    private ContactoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_texto);

//        Inicializar real
        realm =Realm.getDefaultInstance();
        contactos = realm.where(Contacto.class).findAll();

//        Telefono
        tel = findViewById(R.id.TextViewTelefono);
        sms = findViewById(R.id.EditTextSms);
        envioSms = findViewById(R.id.ImageButtonSms);
        spinnerCont = findViewById(R.id.SpinnerNumTel);

        adapter= new ContactoAdapter(this,contactos,R.layout.spinner_view_contacto);
        spinnerCont.setAdapter(adapter);

        spinnerCont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               tel.setText(contactos.get(position).getTelefono().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void EnviarMensaje(View view){
        String numT = tel.getText().toString();
        String datEnvio = sms.getText().toString();

        try {
            int  permissionCheck = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.SEND_SMS);
            if(permissionCheck != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "No tienes permiso para enviar mensaje de texto", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 225);
            }

            SmsManager mensj = SmsManager.getDefault();
            mensj.sendTextMessage(numT, numT, datEnvio,null, null);
            Toast.makeText(getApplicationContext()," Mensaje enviado!", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(getApplicationContext()," Mensaje no enviado. Verifica permisos o datos!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
                MensajeTextoActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancelar", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void llamar(String numero) {
        Intent intent=null;
        intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(numero));
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MensajeTextoActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 105);
        } else {
            startActivity(intent);
            //finish();
        }

    }




    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            //super.onBackPressed();
            showSalir("Quieres salir?", "Deseas dejar esta aplicacion");

            return;
        }else {
            Toast.makeText(this, "Presionar dos veces para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_sms, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.phone:
                String numero = "tel:"+tel.getText().toString();

                llamar(numero);
                //cargarwebservice();
                // startActivity(getIntent());
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
