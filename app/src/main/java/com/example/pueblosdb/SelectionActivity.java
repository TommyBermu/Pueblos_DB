package com.example.pueblosdb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pueblosdb.clases.Cargo;
import com.example.pueblosdb.clases.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SelectionActivity extends AppCompatActivity {
    private String name, surname, Email;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_selection);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getIntent().getExtras();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Email = mAuth.getCurrentUser().getEmail();
        name = bundle.getString("Nombres");
        surname = bundle.getString("Apellidos");
    }

    public void Member(View view) {
        createUser(Cargo.COMUNERO);
    }

    public void NonMember(View view) {
        createUser(Cargo.EXTERNO);
    }

    public void createUser(Cargo cargo){
        db.collection("users").document(Email).set(new User(name, surname, cargo));
        salir();
    }

    private void salir() {
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent auth = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(auth);
    }
}