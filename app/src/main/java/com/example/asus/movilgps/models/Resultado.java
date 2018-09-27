package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import io.realm.RealmObject;

public class Resultado extends RealmObject {
    private int id;
    private int evento;
    private String resultado;
    private int respuesta;

    public Resultado() {

    }

    public Resultado(int evento, String resultado, int respuesta) {
        this.id = MyApplication.ResultadoID.incrementAndGet();
        this.evento = evento;
        this.resultado = resultado;
        this.respuesta = respuesta;
    }

    public int getId() {
        return id;
    }

    public int getEvento() {
        return evento;
    }

    public void setEvento(int evento) {
        this.evento = evento;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public int getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(int respuesta) {
        this.respuesta = respuesta;
    }
}
