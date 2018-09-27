package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import java.util.Date;

import io.realm.RealmObject;

public class Evento extends RealmObject {

    private int id;
    private Date fecha;
    private String latitud;
    private String longitud;
    private int encuesta;
    private int usuario;
    private String imagen;

    public Evento(){

    }

    public Evento(String latitud, String longitud, int encuesta, int usuario, String imagen) {
        this.id = MyApplication.EventoID.incrementAndGet();
        this.fecha = new Date();
        this.latitud = latitud;
        this.longitud = longitud;
        this.encuesta = encuesta;
        this.usuario = usuario;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public int getEncuesta() {
        return encuesta;
    }

    public void setEncuesta(int encuesta) {
        this.encuesta = encuesta;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
