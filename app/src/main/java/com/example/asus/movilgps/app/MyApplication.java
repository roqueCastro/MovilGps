package com.example.asus.movilgps.app;

import android.app.Application;

import com.example.asus.movilgps.models.Contacto;
import com.example.asus.movilgps.models.Encuesta;
import com.example.asus.movilgps.models.Evento;
import com.example.asus.movilgps.models.Pregunta;
import com.example.asus.movilgps.models.Respuesta;
import com.example.asus.movilgps.models.Resultado;
import com.example.asus.movilgps.models.usuario;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by ASUS on 20/04/2018.
 */

public class MyApplication extends Application {

    public static AtomicInteger UsuarioID = new AtomicInteger();
    public static AtomicInteger ContactoID = new AtomicInteger();
    public static AtomicInteger EncuestaID = new AtomicInteger();
    public static AtomicInteger PreguntaID = new AtomicInteger();
    public static AtomicInteger RespuestaID = new AtomicInteger();
    public static AtomicInteger EventoID = new AtomicInteger();
    public static AtomicInteger ResultadoID = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();

        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        UsuarioID = getIdByTable(realm, usuario.class);
        ContactoID = getIdByTable(realm, Contacto.class);
        EncuestaID = getIdByTable(realm, Encuesta.class);
        PreguntaID = getIdByTable(realm, Pregunta.class);
        RespuestaID = getIdByTable(realm, Respuesta.class);
        EventoID = getIdByTable(realm, Evento.class);
        ResultadoID = getIdByTable(realm, Resultado.class);
        realm.close();

    }

    private void  setUpRealmConfig() {
        Realm.init(getApplicationContext());

        RealmConfiguration config= new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();

        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();
    }

}
