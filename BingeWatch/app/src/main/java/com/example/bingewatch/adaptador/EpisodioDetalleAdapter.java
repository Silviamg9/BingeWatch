package com.example.bingewatch.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bingewatch.R;
import com.example.bingewatch.modelo.Episodio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class EpisodioDetalleAdapter extends RecyclerView.Adapter<EpisodioDetalleAdapter.ViewHolder> {

    private List<Episodio> listaEpisodios;
    private String idSerie;
    private String urlFirebase = "https://bingewatch-76938-default-rtdb.europe-west1.firebasedatabase.app/";
    private String idUsuario;

    public EpisodioDetalleAdapter(List<Episodio> listaEpisodios, String idSerie) {
        this.listaEpisodios = listaEpisodios;
        this.idSerie = idSerie;
        this.idUsuario = FirebaseAuth.getInstance().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_episodio_detalle, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Episodio capitulo = listaEpisodios.get(position);

        holder.tvTitulo.setText("E" + capitulo.getNumeroEpisodio() + " - " + capitulo.getTitulo()); // Mostramos el formato E1 - Título
        holder.tvSinopsis.setText(capitulo.getSinopsis());
        holder.cbVisto.setOnCheckedChangeListener(null);
        holder.cbVisto.setChecked(false);

        if (idUsuario != null && idSerie != null) {
            // Construimos la ruta única para este episodio exacto en la base de datos
            String rutaEpisodio = "seguimiento/" + idUsuario + "/" + idSerie + "/episodiosVistos/T" + capitulo.getTemporada() + "_E" + capitulo.getNumeroEpisodio();
            DatabaseReference referenciaFirebase = FirebaseDatabase.getInstance(urlFirebase).getReference(rutaEpisodio);

            //Comprobamos en tiempo real si el usuario ya vio este capítulo para dejarlo marcado
            referenciaFirebase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot capturaDatos) {
                    if (capturaDatos.exists() && Boolean.TRUE.equals(capturaDatos.getValue(Boolean.class))) {
                        holder.cbVisto.setChecked(true);
                    } else {
                        holder.cbVisto.setChecked(false);
                    }
                }
                @Override public void onCancelled(@NonNull DatabaseError error) {}
            });

            //Escuchamos cuando el usuario pulsa físicamente el CheckBox para guardar o borrar de Firebase
            holder.cbVisto.setOnCheckedChangeListener((botónView, estaMarcado) -> {
                if (estaMarcado) {
                    referenciaFirebase.setValue(true); // Guarda que está visto
                } else {
                    referenciaFirebase.removeValue(); // Elimina el registro si se desmarca
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaEpisodios != null ? listaEpisodios.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvSinopsis;
        CheckBox cbVisto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvEpisodioNumeroTitulo);
            tvSinopsis = itemView.findViewById(R.id.tvEpisodioSinopsis);
            cbVisto = itemView.findViewById(R.id.cbVisto);
        }
    }
}