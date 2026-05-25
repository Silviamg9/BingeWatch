package com.example.bingewatch.adaptador;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bingewatch.R;
import com.example.bingewatch.modelo.Episodio;
import com.example.bingewatch.modelo.Temporada;
import java.util.List;

public class TemporadaAdapter extends RecyclerView.Adapter<TemporadaAdapter.ViewHolder> {

    private List<Temporada> listaTemporadas;
    private String idSerie;

    public TemporadaAdapter(List<Temporada> listaTemporadas, String idSerie) {
        this.listaTemporadas = listaTemporadas;
        this.idSerie = idSerie;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temporada, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Temporada temporadaActual = listaTemporadas.get(position);

        holder.tvNombreTemp.setText("Temporada " + temporadaActual.getNumero());

        // 1. Configuramos el adaptador de episodios
        List<Episodio> episodiosDeEstaTemp = temporadaActual.getEpisodios();

        EpisodioDetalleAdapter adaptadorEpisodios = new EpisodioDetalleAdapter(episodiosDeEstaTemp, idSerie);

        holder.rvEpisodios.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvEpisodios.setAdapter(adaptadorEpisodios);

        // 2. Forzamos el estado inicial a GONE para que no haya huecos
        holder.rvEpisodios.setVisibility(View.GONE);
        holder.ivFlecha.setRotation(0f);

        // 3. Ponemos el clic específicamente en el layoutHeader
        holder.layoutHeader.setOnClickListener(v -> {
            if (holder.rvEpisodios.getVisibility() == View.GONE) {
                holder.rvEpisodios.setVisibility(View.VISIBLE);
                holder.ivFlecha.setRotation(180f);
                Log.d("DESPLIEGUE", "Abriendo Temporada " + temporadaActual.getNumero());
            } else {
                holder.rvEpisodios.setVisibility(View.GONE);
                holder.ivFlecha.setRotation(0f);
                Log.d("DESPLIEGUE", "Cerrando Temporada " + temporadaActual.getNumero());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTemporadas != null ? listaTemporadas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreTemp;
        ImageView ivFlecha;
        RecyclerView rvEpisodios;
        View layoutHeader; // Capturar el clic correctamente

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTemp = itemView.findViewById(R.id.tvNombreTemporada);
            ivFlecha = itemView.findViewById(R.id.ivFlecha);
            rvEpisodios = itemView.findViewById(R.id.rvEpisodiosInterno);
            // Referenciamos el contenedor del título y flecha
            layoutHeader = itemView.findViewById(R.id.layoutHeaderTemporada);
        }
    }
}