package com.example.bingewatch.modelo;

public class Usuario {

    private String uid;
    private String nombre;
    private String email;
    private boolean esPremium;

    public Usuario() {}

    public Usuario(String uid, String nombre, String email,boolean esPremium) {
        this.uid = uid;
        this.nombre = nombre;
        this.email = email;
        this.esPremium = esPremium;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {

        this.uid = uid;
    }
    public String getNombre() {

        return nombre;
    }
    public void setNombre(String nombre) {

        this.nombre = nombre;
    }
    public String getEmail() {

        return email;
    }
    public void setEmail(String email) {

        this.email = email;
    }
    public boolean isEsPremium() {

        return esPremium;
    }
    public void setEsPremium(boolean esPremium) {

        this.esPremium = esPremium;
    }
}
