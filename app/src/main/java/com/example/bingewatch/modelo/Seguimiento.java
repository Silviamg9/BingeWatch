package com.example.bingewatch.modelo;

import java.io.Serializable;

public class Seguimiento implements Serializable{

    private String idContenido;
    private String estado;
    private int tempActual;
    private int epiActual;

    public Seguimiento() {}

    public Seguimiento(String idContenido, String estado) {
        this.idContenido = idContenido;
        this.estado = estado;
        this.tempActual = 1;
        this.epiActual = 1;
    }

    public String getIdContenido() {

        return idContenido;
    }
    public void setIdContenido(String idContenido) {

        this.idContenido = idContenido;
    }
    public String getEstado() {

        return estado;
    }
    public void setEstado(String estado) {

        this.estado = estado;
    }
    public int getTempActual() {

        return tempActual;
    }
    public void setTempActual(int tempActual) {

        this.tempActual = tempActual;
    }
    public int getEpiActual() {

        return epiActual;
    }
    public void setEpiActual(int epiActual) {

        this.epiActual = epiActual;
    }
}
