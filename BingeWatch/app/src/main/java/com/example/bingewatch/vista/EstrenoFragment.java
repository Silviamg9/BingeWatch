package com.example.bingewatch.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bingewatch.R;
import com.example.bingewatch.api.ApiClient;
import com.example.bingewatch.api.ApiService;
import com.example.bingewatch.adaptador.ContenidoAdapter;
import com.example.bingewatch.modelo.Contenido;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EstrenoFragment extends Fragment{

    private RecyclerView rvProximosEstrenos;
    private ContenidoAdapter adaptadorContenido;
    private TabLayout pestañasFiltro;
    private List<Contenido> listaCompletaApi = new ArrayList<>();
    private List<Contenido> listaFiltradaUI = new ArrayList<>();
    private final String API_KEY = "24c326bda1msh4dfece522d83772p1aff39jsn99b6609f9c28";
    private final String API_HOST = "streaming-availability.p.rapidapi.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_series, container, false);

        pestañasFiltro = view.findViewById(R.id.pestañaSeries);
        pestañasFiltro.removeAllTabs();
        pestañasFiltro.addTab(pestañasFiltro.newTab().setText(getString(R.string.estreno_serie).toUpperCase()));
        pestañasFiltro.addTab(pestañasFiltro.newTab().setText(getString(R.string.estreno_movies).toUpperCase()));

        // Centrar los textos
        pestañasFiltro.setTabMode(TabLayout.MODE_FIXED);
        pestañasFiltro.setTabGravity(TabLayout.GRAVITY_CENTER);

        rvProximosEstrenos = view.findViewById(R.id.rvResultadosSeries);
        rvProximosEstrenos.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorContenido = new ContenidoAdapter(listaFiltradaUI);
        rvProximosEstrenos.setAdapter(adaptadorContenido);
        pestañasFiltro.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab pestaña) {
                Sonido.reproducirClic();
                aplicarFiltroLocal();
            }
            @Override public void onTabUnselected(TabLayout.Tab pestaña) {}
            @Override public void onTabReselected(TabLayout.Tab pestaña) {}
        });

        // Descargar datos reales de internet
        obtenerEstrenosDesdeAPI();

        return view;
    }

    // Metodo para consultar los nuevos lanzamientos directamente al servidor de RapidAPI
    private void obtenerEstrenosDesdeAPI() {
        ApiService servicioApi = ApiClient.getApiService();

        // Ejecutamos una consulta de tendencias o novedades
        servicioApi.buscarContenido("2026", "es", null, API_KEY, API_HOST)
                .enqueue(new Callback<List<Contenido>>() {
                    @Override
                    public void onResponse(Call<List<Contenido>> call, Response<List<Contenido>> respuestaServidor) {
                        if (respuestaServidor.isSuccessful() && respuestaServidor.body() != null) {
                            listaCompletaApi.clear();
                            listaCompletaApi.addAll(respuestaServidor.body());
                            aplicarFiltroLocal();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.toast_no_upcoming_found), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Contenido>> call, Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.toast_error_network_upcoming), Toast.LENGTH_SHORT).show();                    }
                });
    }

    // Metodo que separa el contenido descargado basándose en la pestaña de la barra superior
    private void aplicarFiltroLocal() {
        listaFiltradaUI.clear();
        int posicionPestaña = pestañasFiltro.getSelectedTabPosition();

        for (Contenido item : listaCompletaApi) {
            String tipo = (item.getTipo() != null) ? item.getTipo().toLowerCase() : "";

            if (posicionPestaña == 0) {
                // Pestaña SERIES: Filtra solo las que sea show, tv o series
                if ((tipo.contains("tv") || tipo.contains("series") || tipo.contains("show")) && !tipo.contains("movie")) {
                    listaFiltradaUI.add(item);
                }
            } else {
                // Pestaña PELÍCULAS: Filtra solo las películas
                if (tipo.contains("movie")) {
                    listaFiltradaUI.add(item);
                }
            }
        }
        adaptadorContenido.notifyDataSetChanged();
    }
}