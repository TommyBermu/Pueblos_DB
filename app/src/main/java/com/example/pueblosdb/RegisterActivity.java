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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText tv1, tv2, tv3, tv4, tv5;
    private static final String TAG = "EmailPassword";

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
        ///
        tv1 = findViewById(R.id.nombres);
        tv2 = findViewById(R.id.apellidos);
        tv3 = findViewById(R.id.correo);
        tv4 = findViewById(R.id.password_created);
        tv5 = findViewById(R.id.password_created_confirmed);
    }

    public void createAccount(View view) {
        String Email = tv3.getText().toString();
        String name = tv1.getText().toString();
        String surname = tv2.getText().toString();
        String password;
        try {
            if (tv4.getText().toString().equals(tv5.getText().toString()))
                 password = tv4.getText().toString();
            else throw new IllegalArgumentException("Las contraseñas no coinciden");

            if (Email.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            //se crea el usuario
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign up success
                        Log.d(TAG, "createUserWithEmail:success");

                        //enviar un código de verificación al email
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.sendEmailVerification();
                        Toast.makeText(RegisterActivity.this, "Verifique su correo electrónico con el link enviado al correo proporcionado y luego inicie sesión", Toast.LENGTH_LONG).show();

                        //se crea el usuario el docuemnto en la base de datos
                        Intent selection = new Intent(RegisterActivity.this, SelectionActivity.class);
                        selection.putExtra("Nombres", name);
                        selection.putExtra("Apellidos", surname);
                        startActivity(selection);
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
