package com.example.bingewatch.vista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bingewatch.R;
import com.example.bingewatch.adaptador.TemporadaAdapter;
import com.example.bingewatch.api.ApiClient;
import com.example.bingewatch.modelo.Contenido;
import com.example.bingewatch.modelo.Episodio;
import com.example.bingewatch.modelo.Temporada;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleActivity extends AppCompatActivity {

    private ImageView imgFondo, imgMiniaturaTrailer;
    private TextView tvTitulo, tvSinopsis, tvInfoAnioEstado;
    private MaterialButton btnAccion;
    private LinearLayout panelLogos, panelInfo, panelTemporadas;
    private TabLayout pestañasDetalle;
    private RecyclerView rvTemporadas;
    private Spinner spinnerEstado;
    private Contenido contenidoSeleccionado;
    private String idParaSeguimiento = ""; // Clave segura de salvaguarda para Firebase y adaptadores

    // CONFIGURACIÓN API Y FIREBASE
    private final String API_KEY = "24c326bda1msh4dfece522d83772p1aff39jsn99b6609f9c28";
    private final String API_HOST = "streaming-availability.p.rapidapi.com";
    private final String FIREBASE_URL = "https://bingewatch-76938-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Toolbar barraHerramientas = findViewById(R.id.barraHerramientas);
        setSupportActionBar(barraHerramientas);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        vincularVistas();

        contenidoSeleccionado = (Contenido) getIntent().getSerializableExtra("serie_seleccionada");

        if (contenidoSeleccionado != null) {

            // Si el ID de la API es nulo, creamos uno único con el título
            if (contenidoSeleccionado.id != null && !contenidoSeleccionado.id.isEmpty()) {
                idParaSeguimiento = contenidoSeleccionado.id;
            } else {
                idParaSeguimiento = contenidoSeleccionado.getTitulo().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            }

            actualizarInterfaz(contenidoSeleccionado);
            comprobarSiYaEstaEnLista(idParaSeguimiento);

            if (!"movie".equalsIgnoreCase(contenidoSeleccionado.getTipo())) {
                // Para la llamada de red mantenemos el parámetro original que espera la API externa
                obtenerEpisodiosReales(contenidoSeleccionado.id);
            }

            configurarPestañas(contenidoSeleccionado);
            configurarSpinner(contenidoSeleccionado);

            // Añadido sonido al botón
            btnAccion.setOnClickListener(v -> {
                Sonido.reproducirClic();
                actualizarFirebase(contenidoSeleccionado);
                marcarBotonComoGuardado();
            });

            // Añadido sonido al pulsar sobre el trailer
            findViewById(R.id.trailer).setOnClickListener(v -> {
                Sonido.reproducirClic();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=trailer " + contenidoSeleccionado.getTitulo())));
            });
        }
    }

    private void vincularVistas() {
        imgFondo = findViewById(R.id.imgDetalleFondo);
        imgMiniaturaTrailer = findViewById(R.id.imgMiniaturaTrailer);
        tvTitulo = findViewById(R.id.tvDetalleTitulo);
        tvSinopsis = findViewById(R.id.tvDetalleSinopsis);
        tvInfoAnioEstado = findViewById(R.id.tvInfoAnioEstado);
        btnAccion = findViewById(R.id.btnAccionPrincipal);
        panelLogos = findViewById(R.id.panelLogosPlataformas);
        pestañasDetalle = findViewById(R.id.pestañasDetalle);
        panelInfo = findViewById(R.id.panelInfoDetalle);
        panelTemporadas = findViewById(R.id.panelSeccionTemporadas);
        spinnerEstado = findViewById(R.id.spinnerEstado);

        rvTemporadas = findViewById(R.id.rvTemporadasDetalle);
        rvTemporadas.setLayoutManager(new LinearLayoutManager(this));
        rvTemporadas.setNestedScrollingEnabled(false);
    }

    private void configurarSpinner(Contenido contenido) {
        // Creamos un adaptador para el Spinner usando el array de strings
        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(this, R.array.estados_seguimiento, R.layout.item_spinner_blanco);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerEstado.setAdapter(adaptador);
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View vista, int posicion, long id) {
                // Añadido sonido al cambiar de elemento en el Spinner
                if (vista != null) {
                    Sonido.reproducirClic();
                }

                String nuevoEstado = parent.getItemAtPosition(posicion).toString();
                // Comprobamos si el botón está desactivado (lo que significa que ya está guardado)
                // Solo se actualiza si el botón ya dice que está en la lista
                if (!btnAccion.isEnabled() || btnAccion.getText().toString().equalsIgnoreCase(getString(R.string.btn_already_saved))) {
                    actualizarEstadoEnFirebase(contenido.id, nuevoEstado);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void comprobarSiYaEstaEnLista(String idContenido) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        DatabaseReference referenciaBaseDatos = FirebaseDatabase.getInstance(FIREBASE_URL).getReference("seguimiento").child(uid).child(idContenido);
        referenciaBaseDatos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot capturaDatos) {
                if (capturaDatos.exists()) {
                    marcarBotonComoGuardado();

                    // Recuperar el estado guardado para poner el Spinner en su sitio
                    String estadoGuardado = capturaDatos.child("estado").getValue(String.class);
                    if (estadoGuardado != null) {
                        ArrayAdapter adaptadorLocal = (ArrayAdapter) spinnerEstado.getAdapter();
                        int posicionEstado = adaptadorLocal.getPosition(estadoGuardado);
                        spinnerEstado.setSelection(posicionEstado);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError errorServidor) {}
        });
    }

    private void marcarBotonComoGuardado() {
        btnAccion.setText(getString(R.string.btn_already_saved));
        btnAccion.setEnabled(false);
        btnAccion.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
    }

    private void actualizarFirebase(Contenido c) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            // Guardamos el objeto completo
            FirebaseDatabase.getInstance(FIREBASE_URL).getReference()
                    .child("seguimiento").child(uid).child(c.id).setValue(c);
        }
    }

    private void actualizarEstadoEnFirebase(String idContenido, String nuevoEstado) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance(FIREBASE_URL).getReference("seguimiento")
                .child(uid).child(idContenido).child("estado").setValue(nuevoEstado);
    }

    private void mostrarPlataformas(Contenido contenido) {
        if (panelLogos == null) return;
        panelLogos.removeAllViews();
        HashSet<Integer> marcasRegistradas = new HashSet<>();
        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(120, 120);
        parametros.setMargins(0, 0, 20, 0);

        if (contenido.streamingOptions != null && contenido.streamingOptions.spain != null) {
            for (Contenido.StreamingServiceInfo info : contenido.streamingOptions.spain) {
                if (info.service != null && info.service.id != null) {
                    String id = info.service.id.toLowerCase();
                    int resId = 0;
                    if (id.contains("netflix")) resId = R.drawable.logo_netflix;
                    else if (id.contains("amazon") || id.contains("prime")) resId = R.drawable.logo_amazon;
                    else if (id.contains("disney")) resId = R.drawable.logo_disney;
                    else if (id.contains("hbo")) resId = R.drawable.logo_hbo;
                    else if (id.contains("movistar")) resId = R.drawable.logo_movistar;
                    else if (id.contains("atres")) resId = R.drawable.logo_atresplayer;

                    if (resId != 0 && !marcasRegistradas.contains(resId)) {
                        ImageView imagenLogo = new ImageView(this);
                        imagenLogo.setLayoutParams(parametros);
                        imagenLogo.setImageResource(resId);
                        imagenLogo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        panelLogos.addView(imagenLogo);
                        marcasRegistradas.add(resId);
                    }
                }
            }
        }
    }

    private void obtenerEpisodiosReales(String idSerie) {
        ApiClient.getApiService().obtenerDetallesSerie(idSerie, API_KEY, API_HOST, "es")
                .enqueue(new Callback<Contenido>() {
                    @Override
                    public void onResponse(Call<Contenido> call, Response<Contenido> respuestaServidor) {
                        if (respuestaServidor.isSuccessful() && respuestaServidor.body() != null) {
                            List<Episodio> episodios = respuestaServidor.body().getListaEpisodios();
                            if (episodios != null && !episodios.isEmpty()) {
                                configurarListaTemporadas(episodios);
                            }
                        }
                    }
                    @Override public void onFailure(Call<Contenido> call, Throwable t) {}
                });
    }

    private void configurarListaTemporadas(List<Episodio> episodiosRecibidos) {
        Map<Integer, List<Episodio>> mapaTemporadas = new TreeMap<>();
        for (Episodio capitulo : episodiosRecibidos) {
            int numeroTemporada = Math.max(capitulo.getTemporada(), 1);
            if (!mapaTemporadas.containsKey(numeroTemporada)) {
                mapaTemporadas.put(numeroTemporada, new ArrayList<>());
            }
            mapaTemporadas.get(numeroTemporada).add(capitulo);
        }
        List<Temporada> listaFinal = new ArrayList<>();
        for (Map.Entry<Integer, List<Episodio>> entry : mapaTemporadas.entrySet()) {
            listaFinal.add(new Temporada(entry.getKey(), entry.getValue()));
        }

        if (contenidoSeleccionado != null && contenidoSeleccionado.id != null) {
            rvTemporadas.setAdapter(new TemporadaAdapter(listaFinal, contenidoSeleccionado.id));
        } else {
            Toast.makeText(this, getString(R.string.toast_error_id_unavailable), Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarInterfaz(Contenido contenido) {
        tvTitulo.setText(contenido.getTitulo());
        tvSinopsis.setText(contenido.getSinopsis());
        tvInfoAnioEstado.setText(contenido.getEstadoAmigable());
        Glide.with(this).load(contenido.getUrlBackdrop()).into(imgFondo);
        Glide.with(this).load(contenido.getUrlImagenReal()).into(imgMiniaturaTrailer);
        mostrarPlataformas(contenido);
    }

    private void configurarPestañas(Contenido contenido) {

        // Centrar los textos
        pestañasDetalle.setTabMode(TabLayout.MODE_FIXED);
        pestañasDetalle.setTabGravity(TabLayout.GRAVITY_FILL);

        // Si es una película ("movie"), eliminamos físicamente la pestaña de episodios de la barra
        if ("movie".equalsIgnoreCase(contenido.getTipo())) {
            if (pestañasDetalle.getTabCount() > 1) {
                pestañasDetalle.removeTabAt(1);
            }
        }

        pestañasDetalle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab pestaña) {
                // Añadido sonido al cambiar de pestaña
                Sonido.reproducirClic();

                if (pestaña.getPosition() == 0) {
                    panelInfo.setVisibility(View.VISIBLE);
                    panelTemporadas.setVisibility(View.GONE);
                    btnAccion.setVisibility(View.VISIBLE);
                } else {
                    panelInfo.setVisibility(View.GONE);
                    panelTemporadas.setVisibility(View.VISIBLE);
                    btnAccion.setVisibility(View.GONE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab pestaña) {}
            @Override public void onTabReselected(TabLayout.Tab pestaña) {}
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}