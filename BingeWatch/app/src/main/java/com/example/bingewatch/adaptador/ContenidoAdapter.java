package com.example.bingewatch.adaptador;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bingewatch.R;
import com.example.bingewatch.modelo.Contenido;
import com.example.bingewatch.vista.DetalleActivity;
import java.util.List;

public class ContenidoAdapter extends RecyclerView.Adapter<ContenidoAdapter.ViewHolder> {

    private List<Contenido> listaContenido;

    public ContenidoAdapter(List<Contenido> listaContenido) {

        this.listaContenido = listaContenido;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_serie, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contenido c = listaContenido.get(position);
        if (c == null) return;

        holder.tvTitulo.setText(c.getTitulo() != null ? c.getTitulo() : "Sin título");
        holder.tvTipo.setText(c.getEstadoAmigable());

        if (c.getTempActual() <= 0) {
            holder.tvProgreso.setVisibility(View.GONE);
        } else {
            holder.tvProgreso.setVisibility(View.VISIBLE);
            holder.tvProgreso.setText("T" + c.getTempActual() + " | E" + c.getEpiActual());
        }

        Glide.with(holder.itemView.getContext())
                .load(c.getUrlImagenReal())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> {
            v.playSoundEffect(android.view.SoundEffectConstants.CLICK);

            Intent intent = new Intent(v.getContext(), DetalleActivity.class);
            intent.putExtra("serie_seleccionada", c);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaContenido != null ? listaContenido.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitulo, tvProgreso, tvTipo;

        public ViewHolder(@NonNull View v) {
            super(v);
            ivPoster = v.findViewById(R.id.ivSerie);
            tvTitulo = v.findViewById(R.id.tvTitulo);
            tvProgreso = v.findViewById(R.id.tvProgreso);
            tvTipo = v.findViewById(R.id.tvTipoLabel);
        }
    }
}