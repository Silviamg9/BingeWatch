package com.example.bingewatch.vista;

import static android.app.Activity.RESULT_OK;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.bingewatch.R;
import com.example.bingewatch.modelo.Contenido;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PerfilFragment extends Fragment{

    private FirebaseAuth autenticadorFirebase;
    private TextView tvNombreUsuario;
    private TextView tvContadorPeliculas, tvContadorSeries, tvContadorEpisodios;
    private MaterialButton btnCerrarSesion, btnModoPremium;
    private ShapeableImageView ivPerfilUsuario;
    private ActivityResultLauncher<Intent> lanzadorCamara;
    private ActivityResultLauncher<String> lanzadorGaleria;
    private final String DB_URL = "https://bingewatch-76938-default-rtdb.europe-west1.firebasedatabase.app/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // 1. Inicializar Firebase y componentes
        autenticadorFirebase = FirebaseAuth.getInstance();
        FirebaseUser usuarioActual = autenticadorFirebase.getCurrentUser();

        ivPerfilUsuario = view.findViewById(R.id.ivPerfilUsuario);
        tvNombreUsuario = view.findViewById(R.id.tvNombreUsuario);
        tvContadorPeliculas = view.findViewById(R.id.tvContadorPeliculas);
        tvContadorSeries = view.findViewById(R.id.tvContadorSeries);
        tvContadorEpisodios = view.findViewById(R.id.tvContadorEpisodios);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnModoPremium = view.findViewById(R.id.btnModoPremium);

        // 2. Configurar lanzadores de imágenes
        configurarLanzadoresImagen();

        // 3. Cargar la foto local inmediatamente al construir la vista
        recuperarFotoPerfilLocal();

        ivPerfilUsuario.setOnClickListener(v -> {
            Sonido.reproducirClic();
            mostrarOpcionesSelector();
        });

        if (usuarioActual != null) {
            tvNombreUsuario.setText("Cargando...");
            cargarNombreRealUsuario(usuarioActual.getUid());
            cargarEstadisticasUsuario(usuarioActual.getUid());
        }

        btnCerrarSesion.setOnClickListener(v -> {
            Sonido.reproducirClic();
            autenticadorFirebase.signOut();
            Toast.makeText(getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intentoNavegacion = new Intent(getActivity(), LoginActivity.class);
            intentoNavegacion.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentoNavegacion);
        });

        return view;
    }

    private void cargarNombreRealUsuario(String uidUsuario) {
        FirebaseDatabase.getInstance(DB_URL)
                .getReference("usuarios")
                .child(uidUsuario)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String nombreDetectado = "";
                            if (snapshot.hasChild("nombre_usuario") && snapshot.child("nombre_usuario").getValue() != null) {
                                nombreDetectado = snapshot.child("nombre_usuario").getValue(String.class);
                            } else if (snapshot.hasChild("nombre") && snapshot.child("nombre").getValue() != null) {
                                nombreDetectado = snapshot.child("nombre").getValue(String.class);
                            }

                            if (nombreDetectado != null && !nombreDetectado.trim().isEmpty()) {
                                tvNombreUsuario.setText(nombreDetectado);
                            } else {
                                tvNombreUsuario.setText("Usuario BingeWatch");
                            }

                            boolean esPremium = false;
                            if (snapshot.hasChild("esPremium") && snapshot.child("esPremium").getValue() != null) {
                                esPremium = snapshot.child("esPremium").getValue(Boolean.class);
                            }

                            if (esPremium) {
                                tvNombreUsuario.setText(tvNombreUsuario.getText() + " ✨ (PRO)");
                                if (btnModoPremium != null) {
                                    btnModoPremium.setText("Cuenta Premium Activa");
                                    btnModoPremium.setEnabled(false);
                                }
                            } else {
                                if (btnModoPremium != null) {
                                    btnModoPremium.setText("Pasarse a Premium 🔒");
                                    btnModoPremium.setOnClickListener(v -> {
                                        Sonido.reproducirClic();
                                        mostrarDialogoHacersePremium(uidUsuario);
                                    });
                                }
                            }
                        } else {
                            tvNombreUsuario.setText("Usuario BingeWatch");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvNombreUsuario.setText("Usuario");
                    }
                });
    }

    private void mostrarDialogoHacersePremium(String uidUsuario) {
        AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(requireContext());
        constructorDialogo.setTitle("Desbloquear Binge Watch Premium");
        constructorDialogo.setMessage("Por solo 1,49€ al mes desbloqueas funciones exclusivas, eliminas la publicidad y apoyas el desarrollo de la app.");

        constructorDialogo.setPositiveButton("Aceptar", (dialogo, posicion) -> {
            FirebaseDatabase.getInstance(DB_URL)
                    .getReference("usuarios")
                    .child(uidUsuario)
                    .child("esPremium")
                    .setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "¡Gracias por tu compra! Cuenta actualizada a PRO ✨", Toast.LENGTH_LONG).show();
                    });
        });

        constructorDialogo.setNegativeButton("Cancelar", (dialogo, posicion) -> dialogo.dismiss());
        constructorDialogo.show();
    }

    // Metodo que procesa y guarda la foto antes de que el móvil reinicie la app
    private void configurarLanzadoresImagen() {
        lanzadorCamara = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK && resultado.getData() != null) {
                        Bundle extras = resultado.getData().getExtras();
                        Bitmap mapaBitsFoto = (Bitmap) extras.get("data");
                        if (mapaBitsFoto != null) {
                            ivPerfilUsuario.setImageBitmap(mapaBitsFoto);
                            guardarFotoPerfilLocal(mapaBitsFoto);
                        }
                    }
                }
        );

        lanzadorGaleria = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uriImagen -> {
                    if (uriImagen != null) {
                        try {
                            // 1. Abrimos el flujo de la foto seleccionada
                            InputStream flujoEntrada = requireContext().getContentResolver().openInputStream(uriImagen);
                            Bitmap mapaBitsSeleccionado = BitmapFactory.decodeStream(flujoEntrada);

                            if (mapaBitsSeleccionado != null) {
                                // 2. Pintamos en la interfaz inmediatamente
                                ivPerfilUsuario.setImageBitmap(mapaBitsSeleccionado);

                                // 3. Forzamos el guardado local en el acto
                                guardarFotoPerfilLocal(mapaBitsSeleccionado);

                                // 4. Notificamos al usuario de que se ha procesado con éxito
                                Toast.makeText(getContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            recuperarFotoPerfilLocal();
                        }
                    } else {
                        recuperarFotoPerfilLocal();
                    }
                }
        );
    }

    private void mostrarOpcionesSelector() {
        String[] opciones = {"Hacer foto con cámara", "Elegir de la galería", "Cancelar"};
        AlertDialog.Builder constructorDialogo = new AlertDialog.Builder(requireContext());
        constructorDialogo.setTitle("Cambiar foto de perfil");
        constructorDialogo.setItems(opciones, (dialogo, posicion) -> {
            if (posicion == 0) {
                Intent intentoCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                lanzadorCamara.launch(intentoCamara);
            } else if (posicion == 1) {
                lanzadorGaleria.launch("image/*");
            } else {
                dialogo.dismiss();
            }
        });
        constructorDialogo.show();
    }

    private void guardarFotoPerfilLocal(Bitmap mapaBits) {
        if (mapaBits == null) return;
        try {
            ByteArrayOutputStream flujoSalidaBytes = new ByteArrayOutputStream();
            mapaBits.compress(Bitmap.CompressFormat.JPEG, 70, flujoSalidaBytes);
            byte[] arrayBytes = flujoSalidaBytes.toByteArray();
            String stringBase64 = Base64.encodeToString(arrayBytes, Base64.DEFAULT);

            FirebaseUser usuario = autenticadorFirebase.getCurrentUser();
            Context contexto = getContext() != null ? getContext() : getActivity();

            if (usuario != null && contexto != null) {
                SharedPreferences preferencias = contexto.getSharedPreferences("PreferenciasPerfil", Context.MODE_PRIVATE);
                preferencias.edit().putString("foto_" + usuario.getUid(), stringBase64).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recuperarFotoPerfilLocal() {
        try {
            FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
            Context contexto = getContext() != null ? getContext() : getActivity();

            if (usuario != null && contexto != null) {
                SharedPreferences preferencias = contexto.getSharedPreferences("PreferenciasPerfil", Context.MODE_PRIVATE);
                String stringBase64 = preferencias.getString("foto_" + usuario.getUid(), null);
                if (stringBase64 != null) {
                    byte[] arrayBytesDecodificados = Base64.decode(stringBase64, Base64.DEFAULT);
                    Bitmap mapaBitsRecuperado = BitmapFactory.decodeByteArray(arrayBytesDecodificados, 0, arrayBytesDecodificados.length);
                    if (mapaBitsRecuperado != null && ivPerfilUsuario != null) {
                        ivPerfilUsuario.setImageBitmap(mapaBitsRecuperado);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarEstadisticasUsuario(String uidUsuario) {
        FirebaseDatabase.getInstance(DB_URL)
                .getReference("seguimiento")
                .child(uidUsuario)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot capturaDatos) {
                        int totalPeliculas = 0;
                        int totalSeries = 0;
                        int totalEpisodiosVistos = 0;

                        for (DataSnapshot snapshotContenido : capturaDatos.getChildren()) {
                            Contenido item = snapshotContenido.getValue(Contenido.class);
                            if (item != null) {
                                String tipo = (item.getTipo() != null) ? item.getTipo().toLowerCase() : "";
                                if (tipo.contains("movie")) {
                                    totalPeliculas++;
                                } else if (tipo.contains("tv") || tipo.contains("series") || tipo.contains("show")) {
                                    totalSeries++;
                                }
                                if (snapshotContenido.hasChild("episodiosVistos")) {
                                    long cantidadEpisodios = snapshotContenido.child("episodiosVistos").getChildrenCount();
                                    totalEpisodiosVistos += (int) cantidadEpisodios;
                                }
                            }
                        }
                        tvContadorPeliculas.setText(String.valueOf(totalPeliculas));
                        tvContadorSeries.setText(String.valueOf(totalSeries));
                        tvContadorEpisodios.setText(String.valueOf(totalEpisodiosVistos));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError errorServidor) {
                        tvContadorPeliculas.setText("0");
                        tvContadorSeries.setText("0");
                        tvContadorEpisodios.setText("0");
                    }
                });
    }
}