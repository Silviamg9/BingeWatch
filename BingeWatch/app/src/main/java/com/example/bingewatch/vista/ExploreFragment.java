package com.example.bingewatch.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    private RecyclerView rvExplorarResultados;
    private ContenidoAdapter adaptadorContenido;
    private TextInputEditText etBuscar;
    private List<Contenido> listaResultados = new ArrayList<>();

    private final String API_KEY = "24c326bda1msh4dfece522d83772p1aff39jsn99b6609f9c28";
    private final String API_HOST = "streaming-availability.p.rapidapi.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explore, container, false);

        etBuscar = view.findViewById(R.id.etBuscar);
        rvExplorarResultados = view.findViewById(R.id.rvExplorarResultados);
        ImageButton btnBuscarLupa = view.findViewById(R.id.btnBuscarLupa);

        rvExplorarResultados.setLayoutManager(new LinearLayoutManager(getContext()));

        btnBuscarLupa.setOnClickListener(v -> {
            String textoConsulta = etBuscar.getText().toString().trim();
            if (!textoConsulta.isEmpty()) {
                buscarEnAPI(textoConsulta);
            }
        });

        return view;
    }

    private void buscarEnAPI(String query) {
        ApiService serviceApi = ApiClient.getApiService();
        serviceApi.buscarContenido(query, "es", null, API_KEY, API_HOST)
                .enqueue(new Callback<List<Contenido>>() {
                    @Override
                    public void onResponse(Call<List<Contenido>> call, Response<List<Contenido>> respuestaServidor) {
                        if (respuestaServidor.isSuccessful() && respuestaServidor.body() != null) {
                            listaResultados.clear();
                            listaResultados.addAll(respuestaServidor.body());
                            adaptadorContenido = new ContenidoAdapter(listaResultados);
                            rvExplorarResultados.setAdapter(adaptadorContenido);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Contenido>> call, Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.toast_network_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}