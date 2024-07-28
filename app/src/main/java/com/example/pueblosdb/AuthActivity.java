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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private EditText tv1;
    private TextInputLayout tv2;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        setTheme(R.style.Theme_PueblosDB);
        if (mAuth.getCurrentUser() != null) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv1 = findViewById(R.id.email);
        tv2 = findViewById(R.id.password_container);

        //firebase
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("Message", "Integración de Firebase Completa!");
        mFirebaseAnalytics.logEvent("InitScreen", bundle);
    }

    public void SignIn(View view) {
        try {
            String Email = tv1.getText().toString();
            String Password = Objects.requireNonNull(tv2.getEditText()).getText().toString();

            if (Email.isEmpty() || Password.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success");
                        //ir a la home activity
                        Intent home = new Intent(AuthActivity.this, HomeActivity.class);
                        startActivity(home);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(AuthActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "signInWithEmail:failure", e);
            Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void SignUp(View view) {
        Intent selection = new Intent(this, SelectionActivity.class);
        startActivity(selection);
    }
}