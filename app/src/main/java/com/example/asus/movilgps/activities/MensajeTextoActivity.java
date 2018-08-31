package com.example.asus.movilgps.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.movilgps.R;

public class MensajeTextoActivity extends AppCompatActivity {

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;
    TextView tel;
    EditText sms;
    ImageButton envioSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje_texto);
        tel = findViewById(R.id.TextViewTelefono);
        sms = findViewById(R.id.EditTextSms);
        envioSms = findViewById(R.id.ImageButtonSms);
    }

    public void EnviarMensaje(View view){
        String numT = tel.getText().toString();
        String datEnvio = sms.getText().toString();
        if (numT != null || datEnvio != null){

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

        }else{
            Toast.makeText(getApplicationContext()," Verifica si llego el numero telefonico o escribir un sms!.", Toast.LENGTH_SHORT).show();
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
}
