package com.example.bingewatch.modelo;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Episodio implements Serializable {
    @SerializedName("title")
    private String titulo;
    @SerializedName("episode")
    private int numeroEpisodio;
    @SerializedName("season")
    private int temporada;
    @SerializedName("overview")
    private String sinopsis;
    @SerializedName("imageSet")
    private ImageSet imageSet;

    public Episodio() {}

    public Episodio(String id, int num, int temp, String tit, String sin) {
        this.numeroEpisodio = num;
        this.temporada = temp;
        this.titulo = tit;
        this.sinopsis = sin;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }
    public void setNumeroEpisodio(int numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }
    public int getTemporada() {
        return temporada;
    }
    public void setTemporada(int temporada) {
        this.temporada = temporada;
    }
    public String getTitulo() {
        return titulo != null ? titulo : "Sin título";
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getSinopsis() {
        return sinopsis != null ? sinopsis : "Sin sinopsis.";
    }
    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }
    public void setImageSet(ImageSet imageSet) {
        this.imageSet = imageSet;
    }
    public String getUrlImagen() {
        if (imageSet != null && imageSet.horizontalPoster != null) {
            return imageSet.horizontalPoster.w360;
        }
        return null;
    }

    public static class ImageSet implements Serializable {
        @SerializedName("horizontalPoster") public HorizontalPoster horizontalPoster;
    }

    public static class HorizontalPoster implements Serializable {
        @SerializedName("w360") public String w360;
    }
}
