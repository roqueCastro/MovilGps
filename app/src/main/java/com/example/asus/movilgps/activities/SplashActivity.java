package com.example.asus.movilgps.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.movilgps.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    public  static int conexion = 0;
    private static  final long SPLASH_SCREEN_DELAY = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SharedPreferences prefe=getApplicationContext().getSharedPreferences("datos",Context.MODE_PRIVATE);
        String splash =  prefe.getString("login","");

        if(splash != ""){
            Intent o = new Intent().setClass(SplashActivity.this,MainActivity.class);
            startActivity(o);
            finish();
        }
        SharedPreferences preferencias=getApplicationContext().getSharedPreferences("datos",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferencias.edit();
        editor.putString("login", "1");
        editor.commit();

         if(compruebaOnlineNet()==false) {
            conexion=1;
        }else{
             conexion=0;
         }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(conexion==1){
                    Toast.makeText(getApplicationContext(), "No tienes conexion a internet", Toast.LENGTH_SHORT).show();
                }else{
                    Intent o = new Intent().setClass(SplashActivity.this,MainActivity.class);
                    startActivity(o);
                    finish();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }



    public Boolean compruebaOnlineNet() {
        boolean conex = false;

        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =con.getActiveNetworkInfo();

        if(networkInfo!=null && networkInfo.isConnected()){
            conex=true;
        }
        return conex;
    }
}
