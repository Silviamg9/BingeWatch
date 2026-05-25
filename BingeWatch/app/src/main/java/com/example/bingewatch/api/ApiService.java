package com.example.bingewatch.api;

import com.example.bingewatch.modelo.Contenido;

import java.util.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("shows/search/title")
    Call<List<Contenido>> buscarContenido(
            @Query("title") String title,
            @Query("country") String country,
            @Query("show_type") String show_type,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost
    );

    @GET("shows/{id}")
    Call<Contenido> obtenerDetallesSerie(
            @Path("id") String serieId,
            @Header("x-rapidapi-key") String apiKey,
            @Header("x-rapidapi-host") String apiHost,
            @Query("country") String country
    );
}