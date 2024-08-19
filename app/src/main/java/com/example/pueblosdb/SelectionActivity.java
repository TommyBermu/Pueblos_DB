package com.example.pueblosdb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pueblosdb.clases.User;
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
        createUser("Comunero");
    }

    public void NonMember(View view) {
        createUser("Externo");
    }

    public void createUser(String cargo){
        db.collection("users").document(Email).set(new User(name, surname, cargo));
        Intent auth = new Intent(this, AuthActivity.class);
        startActivity(auth);
    }
}