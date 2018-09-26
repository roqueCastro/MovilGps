package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Respuesta extends RealmObject {

    @PrimaryKey
    private int id;
    private int id_resp;
    private String nom_resp;
    private int pregunta;
    private String tipo_dato;

    public Respuesta(int id_resp, String nom_resp, int pregunta, String tipo_dato) {
        this.id = MyApplication.RespuestaID.incrementAndGet();
        this.id_resp = id_resp;
        this.nom_resp = nom_resp;
        this.pregunta = pregunta;
        this.tipo_dato = tipo_dato;
    }
    public Respuesta(){

    }

    public int getId() {
        return id;
    }

    public int getId_resp() {
        return id_resp;
    }

    public void setId_resp(int id_resp) {
        this.id_resp = id_resp;
    }

    public String getNom_resp() {
        return nom_resp;
    }

    public void setNom_resp(String nom_resp) {
        this.nom_resp = nom_resp;
    }

    public int getPregunta() {
        return pregunta;
    }

    public void setPregunta(int pregunta) {
        this.pregunta = pregunta;
    }

    public String getTipo_dato() {
        return tipo_dato;
    }

    public void setTipo_dato(String tipo_dato) {
        this.tipo_dato = tipo_dato;
    }
}
