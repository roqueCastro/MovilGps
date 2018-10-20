package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ASUS on 01/06/2018.
 */

public class Pregunta extends RealmObject {

    @PrimaryKey
    private  int id;

    private int id_pregunta;

    private String nombre_pre;

    private int tipo_pre;

    private int encuesta2;

    private int estado;

    public Pregunta() {

    }

    public Pregunta(int id_pregunta, String nombre_pre, int tipo_pre, int encuesta2, int estado) {
        this.id = MyApplication.PreguntaID.incrementAndGet();
        this.id_pregunta = id_pregunta;
        this.nombre_pre = nombre_pre;
        this.tipo_pre = tipo_pre;
        this.encuesta2 = encuesta2;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public String getNombre_pre() {
        return nombre_pre;
    }

    public void setNombre_pre(String nombre_pre) {
        this.nombre_pre = nombre_pre;
    }

    public int getTipo_pre() {
        return tipo_pre;
    }

    public void setTipo_pre(int tipo_pre) {
        this.tipo_pre = tipo_pre;
    }

    public int getEncuesta2() {
        return encuesta2;
    }

    public void setEncuesta2(int encuesta2) {
        this.encuesta2 = encuesta2;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
