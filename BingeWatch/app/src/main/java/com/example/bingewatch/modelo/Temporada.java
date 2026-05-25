package com.example.bingewatch.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Temporada implements Serializable {
    private int numero;
    private List<Episodio> episodios;
    private boolean expandida = false;

    public Temporada(int numero, List<Episodio> episodios) {
        this.numero = numero;
        this.episodios = new ArrayList<>(episodios);
    }

    public int getNumero() {

        return numero;
    }
    public List<Episodio> getEpisodios() {

        return episodios;
    }
    public boolean isExpandida() {

        return expandida;
    }
    public void setExpandida(boolean expandida) {

        this.expandida = expandida;
    }
}
