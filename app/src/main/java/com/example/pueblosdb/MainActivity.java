package com.example.pueblosdb;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.content.Intent;
import android.widget.*;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText tv1;
    private TextInputLayout tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv1 = findViewById(R.id.user);
        tv2 = findViewById(R.id.password_container);
    }

    public void Enter(View view) {
        String User = tv1.getText().toString();
        String Password = Objects.requireNonNull(tv2.getEditText()).getText().toString();
        Toast.makeText(this, User, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, Password, Toast.LENGTH_SHORT).show();
        Intent home = new Intent(this, HomeActivity.class);
        startActivity(home);
    }

    public void Register(View view) {
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }
}