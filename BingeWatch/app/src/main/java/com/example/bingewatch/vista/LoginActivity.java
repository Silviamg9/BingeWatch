package com.example.bingewatch.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bingewatch.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etContraseña;
    private MaterialButton btnLogin, btnRegistrar;
    private FirebaseAuth autenticacionFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        com.google.firebase.FirebaseApp.initializeApp(this);

        autenticacionFirebase = FirebaseAuth.getInstance();

        // Guardar la sesion iniciada
        if (autenticacionFirebase.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etContraseña = findViewById(R.id.etContraseña);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnLogin.setOnClickListener(v -> {
            Sonido.reproducirClic(); // <- Línea de sonido
            iniciarSesion();
        });

        btnRegistrar.setOnClickListener(v -> {
            Sonido.reproducirClic(); // <- Línea de sonido
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }

    private void iniciarSesion() {
        String email = etEmail.getText().toString().trim();
        String contraseña = etContraseña.getText().toString().trim();

        if (email.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        autenticacionFirebase.signInWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, tarea -> {
                    if (tarea.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Error: " + tarea.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
