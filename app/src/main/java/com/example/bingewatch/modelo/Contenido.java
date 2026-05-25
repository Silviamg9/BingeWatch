package com.example.bingewatch.modelo;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contenido implements Serializable{

    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String titulo;

    @SerializedName("showType")
    public String type;

    @SerializedName("overview")
    private String sinopsis;

    @SerializedName("status")
    private StatusInfo status;

    @SerializedName("imageSet")
    public ImageSet imageSet;

    @SerializedName("streamingOptions")
    public StreamingOptions streamingOptions;

    @SerializedName("seasons")
    private List<Season> seasons;

    public String estado;
    public int tempActual;
    public int epiActual;
    private int totalTemporadas;

    public Contenido() {
    }

    public Contenido(String id, String titulo, String type, String posterUrl, String sinopsis) {
        this.id = id;
        this.titulo = titulo;
        this.type = type;
        this.sinopsis = sinopsis;
        this.estado = "Pendiente";
        this.tempActual = 1;
        this.epiActual = 1;
    }

    public String getId() {

        return id;
    }
    public void setId(String id) {

        this.id = id;
    }
    public String getTitulo() {

        return titulo;
    }
    public void setTitulo(String titulo) {

        this.titulo = titulo;
    }
    public String getTipo() {

        return type;
    }
    public void setTipo(String tipo) {

        this.type = tipo;
    }
    public String getSinopsis() {

        return sinopsis;
    }
    public void setSinopsis(String sinopsis) {

        this.sinopsis = sinopsis;
    }
    public int getTotalTemporadas() {

        return totalTemporadas;
    }
    public void setTotalTemporadas(int totalTemporadas) {

        this.totalTemporadas = totalTemporadas;
    }
    public int getEpiActual() {

        return epiActual;
    }
    public void setEpiActual(int epiActual) {

        this.epiActual = epiActual;
    }
    public int getTempActual() {

        return tempActual;
    }
    public void setTempActual(int tempActual) {
        this.tempActual = tempActual;
    }
    public String getEstado() {

        return estado;
    }
    public void setEstado(String estado) {

        this.estado = estado;
    }

    // --- MÉTODOS AUXILIARES ---

    public String getUrlBackdrop() {
        if (imageSet != null && imageSet.backdrop != null && imageSet.backdrop.w1280 != null) {
            return imageSet.backdrop.w1280;
        }
        return null;
    }

    public String getUrlImagenReal() {
        if (imageSet != null && imageSet.verticalPoster != null && imageSet.verticalPoster.w360 != null) {
            return imageSet.verticalPoster.w360;
        }
        return null;
    }

    public String getEstadoAmigable() {
        if (status != null && status.id != null) {
            if (status.id.equalsIgnoreCase("ended")) return "Finalizada";
            if (status.id.equalsIgnoreCase("returning")) return "En emisión";
        }
        return (type != null && type.equalsIgnoreCase("movie")) ? "Película" : "Serie";
    }

    public List<Episodio> getListaEpisodios() {
        List<Episodio> todosLosEpisodios = new ArrayList<>();
        if (seasons != null) {
            for (int i = 0; i < seasons.size(); i++) {
                Season s = seasons.get(i);
                if (s.episodes != null) {
                    for (int j = 0; j < s.episodes.size(); j++) {
                        Episodio e = s.episodes.get(j);

                        e.setTemporada(i + 1);
                        e.setNumeroEpisodio(j + 1);

                        todosLosEpisodios.add(e);
                    }
                }
            }
        }
        return todosLosEpisodios;
    }

    public static class Season implements Serializable {
        @SerializedName("episodes")
        public List<Episodio> episodes;
    }

    // --- CLASES AUXILIARES ---

    public static class StatusInfo implements Serializable {
        @SerializedName("id")
        public String id;
    }

    public static class StreamingOptions implements Serializable {
        @SerializedName("es")
        public List<StreamingServiceInfo> spain;
    }

    public static class StreamingServiceInfo implements Serializable {
        @SerializedName("service")
        public ServiceDetail service;
    }

    public static class ServiceDetail implements Serializable {
        @SerializedName("id")
        public String id;
    }

    public static class ImageSet implements Serializable {
        @SerializedName("backdrop")
        public Backdrop backdrop;

        @SerializedName("verticalPoster")
        public VerticalPoster verticalPoster;
    }

    public static class Backdrop implements Serializable {
        @SerializedName("w1280")
        public String w1280;
    }
    public static class VerticalPoster implements Serializable {
        @SerializedName("w360")
        public String w360;
    }
}
