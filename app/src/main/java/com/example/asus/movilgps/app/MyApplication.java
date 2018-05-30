package com.example.asus.movilgps.app;

import android.app.Application;

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

    @Override
    public void onCreate() {
        super.onCreate();

        setUpRealmConfig();

        Realm realm = Realm.getDefaultInstance();
        UsuarioID = getIdByTable(realm, usuario.class);
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
