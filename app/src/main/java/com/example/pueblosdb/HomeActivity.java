package com.example.pueblosdb;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.*;

import com.example.pueblosdb.clases.Comunero;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;


public class HomeActivity extends AppCompatActivity {

    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private TextView tv1, tv2, tv3;
    private final Comunero comunero = new Comunero();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        tv1 = findViewById(R.id.emailvisualizer);
        tv2 = findViewById(R.id.namevisualizer);
        tv3 = findViewById(R.id.surnamevisualizer);

        tv1.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
    }

    public void viewData(View view) {
        comunero.verInformacion(db);
    }

    public void logOut(View view) {
        mAuth.signOut();
        Intent auth = new Intent(this, AuthActivity.class);
        startActivity(auth);
    }
}