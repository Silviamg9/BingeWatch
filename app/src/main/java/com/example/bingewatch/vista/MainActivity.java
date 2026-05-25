package com.example.bingewatch.vista;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.bingewatch.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacionFirebase;
    private BottomNavigationView btnNavegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicializar Firebase y verificar sesión
        autenticacionFirebase = FirebaseAuth.getInstance();
        if (autenticacionFirebase.getCurrentUser() == null) {
            irAlLogin();
            return;
        }

        // 2. Referenciar vistas
        btnNavegacion = findViewById(R.id.btnNavigacion);

        // 3. Cargar fragmento inicial por defecto
        if (savedInstanceState == null) {
            cambiarFragmento(new SeriesFragment());
        }

        /// 4. Lógica del BottomNavigationView (Navegación principal)
        btnNavegacion.setOnItemSelectedListener(item -> {
            Fragment fragmentoSeleccionado = null;
            int id = item.getItemId();

            // Aquí solo decidimos qué sección principal cargar
            if (id == R.id.nav_series) {
                fragmentoSeleccionado = new SeriesFragment();
            } else if (id == R.id.nav_movies) {
                fragmentoSeleccionado = new MoviesFragment();
            } else if (id == R.id.nav_explore) {
                fragmentoSeleccionado = new ExploreFragment();
            } else if (id == R.id.nav_upcoming) {
                fragmentoSeleccionado = new EstrenoFragment();
            } else if (id == R.id.nav_profile) {
                fragmentoSeleccionado = new PerfilFragment();
            }

            if (fragmentoSeleccionado != null) {
                cambiarFragmento(fragmentoSeleccionado);
            }
            return true;
        });
    }

    // Metodo auxiliar para no repetir código de fragmentos
    private void cambiarFragmento(Fragment fragmento) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedorFragmento, fragmento)
                .commit();
    }

    private void irAlLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}