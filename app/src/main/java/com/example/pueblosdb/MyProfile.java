package com.example.pueblosdb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MyProfile extends AppCompatActivity {

    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private TextView tv1, tv2, tv3;
    private String Email, Password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        Email = mAuth.getCurrentUser().getEmail();
        //Password = mAuth.getCurrentUser().getPassword();
        tv1 = findViewById(R.id.etemailvisualizer);
        tv2 = findViewById(R.id.etnamevisualizer);
        tv3 = findViewById(R.id.etsurnamevisualizer);
    }

    public void viewData(View view) {
        //comunero.verInformacion(db, Email);
        tv1.setText(Email);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        tv2.setText(prefs.getString("name", "No hay datos"));
        tv3.setText(prefs.getString("surname", "No hay datos"));
    }
}