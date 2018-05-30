package com.example.asus.movilgps.models;

import com.example.asus.movilgps.app.MyApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by ASUS on 20/04/2018.
 */

public class usuario extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String nombre;
    @Required
    private String apellido;
    @Required
    private String telefono;
    @Required
    private String correo;
    @Required
    private String pass;
    @Required
    private String login;

    public usuario() {

    }

    public usuario(String nombre, String apellido,String telefono, String correo, String pass, String login) {
        this.id = MyApplication.UsuarioID.incrementAndGet();
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.pass = pass;
        this.login = login;
    }

    public int getId() {
        return id;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
