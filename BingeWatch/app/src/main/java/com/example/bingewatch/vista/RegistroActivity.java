package com.example.bingewatch.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bingewatch.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;


public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText etNombreUsuario, etEmail, etContraseña;
    private MaterialButton btnCrearCuenta;
    private FirebaseAuth autenticacionFirebase;
    private DatabaseReference referenciaBaseDatos;

    private final String URL_FIREBASE = "https://bingewatch-76938-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // 1. Inicializar Firebase
        autenticacionFirebase = FirebaseAuth.getInstance();
        referenciaBaseDatos = FirebaseDatabase.getInstance(URL_FIREBASE).getReference();

        // 2. Vincular vistas
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etEmail = findViewById(R.id.etEmail);
        etContraseña = findViewById(R.id.etContraseña);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        btnCrearCuenta.setOnClickListener(v -> {
            Sonido.reproducirClic();
            registrarUsuario();
        });
    }

    private void registrarUsuario() {
        String nombreUsuario = etNombreUsuario.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contraseña = etContraseña.getText().toString().trim();

        if (nombreUsuario.isEmpty() || email.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contraseña.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Crear usuario
        autenticacionFirebase.createUserWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, tarea -> {
                    if (tarea.isSuccessful() && autenticacionFirebase.getCurrentUser() != null) {
                        String idUsuario = autenticacionFirebase.getCurrentUser().getUid();
                        guardarDatosUsuario(idUsuario, nombreUsuario, email);
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error: " + (tarea.getException() != null ? tarea.getException().getMessage() : "Fallo de registro"), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosUsuario(String idUsuario, String nombre, String email) {
        // Creamos un mapa con la información
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre_usuario", nombre);
        datosUsuario.put("email", email);

        //Todo usuario nuevo empieza con el candado echado (false)
        datosUsuario.put("esPremium", false);

        referenciaBaseDatos.child("usuarios").child(idUsuario).setValue(datosUsuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistroActivity.this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();

                    Intent irAlLogin = new Intent(RegistroActivity.this, LoginActivity.class);
                    startActivity(irAlLogin);
                    finish();
                })


                .addOnFailureListener(e -> {
                    Toast.makeText(RegistroActivity.this, "Error al guardar perfil: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
