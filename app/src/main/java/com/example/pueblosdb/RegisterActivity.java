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
import com.example.pueblosdb.clases.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText tv1, tv2, tv3, tv4, tv5;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv1 = findViewById(R.id.nombres);
        tv2 = findViewById(R.id.apellidos);
        tv3 = findViewById(R.id.correo);
        tv4 = findViewById(R.id.password_created);
        tv5 = findViewById(R.id.password_created_confirmed);

        mAuth = FirebaseAuth.getInstance();
    }

    public void createAccount(View view) {
        String Email = tv3.getText().toString();
        String name = tv1.getText().toString();
        String surname = tv2.getText().toString();
        try {
            String password;
            if (tv4.getText().toString().equals(tv5.getText().toString()))
                password = tv4.getText().toString();
            else throw new IllegalArgumentException("Las contraseñas no coinciden");

            if (Email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            //se crea el usuario
            mAuth.createUserWithEmailAndPassword(Email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign up success
                        Log.d(TAG, "createUserWithEmail:success");
                        //se crea el documento donde van a estar los datos del usuario
                        db.collection("users").document(Email).set(new Comunero(name, surname, "comunero"));
                        //ir a la home activity
                        Intent home = new Intent(RegisterActivity.this, HomeActivity.class);
                        startActivity(home);
                    } else {
                        // If sign up fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "createUserWithEmail:failure", e);
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
