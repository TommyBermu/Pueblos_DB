package com.example.pueblosdb;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AppRegisterActivity extends AppCompatActivity {

    private EditText tv1, tv2;

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
    }

    public void setAtributes(View view){
        try {
            String name = tv1.getText().toString();
            String surname = tv2.getText().toString();
            if (name.isEmpty() || surname.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            //se crea el usuario el docuemnto en la base de datos
            Intent selection = new Intent(AppRegisterActivity.this, SelectionActivity.class);
            selection.putExtra("Nombres", name);
            selection.putExtra("Apellidos", surname);
            startActivity(selection);

        } catch (IllegalArgumentException e) {
            Log.w("EmailPassword", "createDocument: failure", e);
            Toast.makeText(AppRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}