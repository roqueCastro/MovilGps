package com.example.asus.movilgps.models;

import android.content.Intent;

/**
 * Created by ASUS on 12/06/2018.
 */

public class Respuesta {
    private Integer id_resp;
    private String nombre_resp;
    private String tipo_pregunta;
    private String tipo_dato;



    public Integer getId_resp() {
        return id_resp;
    }

    public void setId_resp(Integer id_resp) {
        this.id_resp = id_resp;
    }

    public String getNombre_resp() {
        return nombre_resp;
    }

    public void setNombre_resp(String nombre_resp) {
        this.nombre_resp = nombre_resp;
    }

    public String getTipo_pregunta() {
        return tipo_pregunta;
    }

    public void setTipo_pregunta(String tipo_pregunta) {
        this.tipo_pregunta = tipo_pregunta;
    }
    public String getTipo_dato() {
        return tipo_dato;
    }

    public void setTipo_dato(String tipo_dato) {
        this.tipo_dato = tipo_dato;
    }
}
