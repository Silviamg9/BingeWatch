package com.example.bingewatch.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bingewatch.R;
import com.example.bingewatch.adaptador.ContenidoAdapter;
import com.example.bingewatch.modelo.Contenido;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {
    private RecyclerView rvResultadosPeliculas;
    private ContenidoAdapter adaptadorContenido;
    private List<Contenido> listaPelis;
    private TabLayout pestañaPeliculas;
    private final String DB_URL = "https://bingewatch-76938-default-rtdb.europe-west1.firebasedatabase.app/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        pestañaPeliculas = view.findViewById(R.id.pestañaPeliculas);
        rvResultadosPeliculas = view.findViewById(R.id.rvResultadosPeliculas);
        rvResultadosPeliculas.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPelis = new ArrayList<>();
        adaptadorContenido = new ContenidoAdapter(listaPelis);
        rvResultadosPeliculas.setAdapter(adaptadorContenido);

        pestañaPeliculas.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab pestaña) { cargarDatos(); }
            @Override public void onTabUnselected(TabLayout.Tab pestaña) {}
            @Override public void onTabReselected(TabLayout.Tab pestaña) {}
        });

        cargarDatos();
        return view;
    }

    private void cargarDatos() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance("https://bingewatch-76938-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("seguimiento").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot capturaDatos) {
                        listaPelis.clear();

                        int posicionPestaña = pestañaPeliculas.getSelectedTabPosition();
                        String estadoFiltro = "";

                        switch (posicionPestaña) {
                            case 0: estadoFiltro = "Sin Comenzar"; break;
                            case 1: estadoFiltro = "Viendo"; break;
                            case 2: estadoFiltro = "Terminada"; break;
                            case 3: estadoFiltro = "Abandonada"; break;
                        }

                        for (DataSnapshot ds : capturaDatos.getChildren()) {
                            Contenido pelicula = ds.getValue(Contenido.class);
                            if (pelicula != null) {
                                String tipo = (pelicula.getTipo() != null) ? pelicula.getTipo().toLowerCase() : "";
                                String estadoActual = (pelicula.getEstado() != null) ? pelicula.getEstado() : "Pendiente";

                                if (tipo.contains("movie")) {
                                    if (estadoActual.equalsIgnoreCase(estadoFiltro)) {
                                        listaPelis.add(pelicula);
                                    }
                                }
                            }
                        }
                        adaptadorContenido.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }
}