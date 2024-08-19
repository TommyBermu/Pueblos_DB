package com.example.pueblosdb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pueblosdb.clases.Comunero;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class GoogleRegisterActivity extends AppCompatActivity {

    private EditText tv1, tv2;
    private FirebaseAuth mAuth;
    private String Email;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_google_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv1 = findViewById(R.id.nombre);
        tv2 = findViewById(R.id.apellido);
        mAuth = FirebaseAuth.getInstance();
        Email = mAuth.getCurrentUser().getEmail();
    }

    public void createDocument(View view){
        try {
            String name = tv1.getText().toString();
            String surname = tv2.getText().toString();

            if (name.isEmpty() || surname.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            //se crea el usuario el docuemnto en la base de datos
            db.collection("users").document(Email).set(new Comunero(name, surname, "comunero"));
            Intent home = new Intent(GoogleRegisterActivity.this, HomeActivity.class);
            startActivity(home);

        } catch (IllegalArgumentException e) {
            Log.w("EmailPassword", "createDocument: failure", e);
            Toast.makeText(GoogleRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}