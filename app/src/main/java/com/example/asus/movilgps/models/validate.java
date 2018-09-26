package com.example.asus.movilgps.models;

/**
 * Created by ASUS on 14/06/2018.
 */

public class validate {
    private Integer id;
    private String nombre;
    private Integer tipo;
    private int pregunta_resp;
    private Integer encuesta2;
    private String tipo_dato;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public int getPregunta_resp() {
        return pregunta_resp;
    }

    public void setPregunta_resp(int pregunta_resp) {
        this.pregunta_resp = pregunta_resp;
    }

    public Integer getEncuesta2() {
        return encuesta2;
    }

    public void setEncuesta2(Integer encuesta2) {
        this.encuesta2 = encuesta2;
    }

    public String getTipo_dato() {
        return tipo_dato;
    }

    public void setTipo_dato(String tipo_dato) {
        this.tipo_dato = tipo_dato;
    }
}
