package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ASUS on 31/08/2018.
 */

public class Contacto extends RealmObject {

    @PrimaryKey
    private  int id;
    @Required
    private String encuesta;
    @Required
    private  String telefono;

    public Contacto(){
    }

    public Contacto(String encuesta, String telefono) {
        this.id = MyApplication.ContactoID.incrementAndGet();
        this.encuesta = encuesta;
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public String getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(String encuesta) {
        this.encuesta = encuesta;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
