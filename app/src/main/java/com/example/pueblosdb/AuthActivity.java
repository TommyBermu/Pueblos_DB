package com.example.pueblosdb;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {
    SharedPreferences prefs;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //quita el SplashScreen y pone el de la app
        setTheme(R.style.Theme_PueblosDB);

        prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified() && prefs.getString("email", null) != null){
            Intent home = new Intent(this, MainActivity.class);
            startActivity(home);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        // pone el Navigation Bar de color azul oscuro y el Status Bar de color beige
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_beige));
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LogInFragment()).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified() && prefs.getString("email", null) != null){
            Intent home = new Intent(this, MainActivity.class);
            startActivity(home);
        }
    }
}