package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by roque on 17/09/2018.
 */

public class Encuesta extends RealmObject {
    @PrimaryKey
    private int id;

    private int id_encuesta;

    private String nombre_encuesta;



    public Encuesta(int id_encuesta, String nombre_encuesta) {
        this.id = MyApplication.EncuestaID.incrementAndGet();
        this.id_encuesta = id_encuesta;
        this.nombre_encuesta = nombre_encuesta;
    }

    public Encuesta(){

    }

    public int getId() {
        return id;
    }

    public int getId_encuesta() {
        return id_encuesta;
    }

    public void setId_encuesta(int id_encuesta) {
        this.id_encuesta = id_encuesta;
    }

    public String getNombre_encuesta() {
        return nombre_encuesta;
    }

    public void setNombre_encuesta(String nombre_encuesta) {
        this.nombre_encuesta = nombre_encuesta;
    }
}
