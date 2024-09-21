package com.example.pueblosdb;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class RegisterFragment extends Fragment {
    private EditText tv1, tv2, tv3, tv4, tv5;
    private static final String TAG = "EmailPassword";

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv1 = view.findViewById(R.id.nombres);
        tv2 = view.findViewById(R.id.apellidos);
        tv3 = view.findViewById(R.id.correo);
        tv4 = view.findViewById(R.id.password_created);
        tv5 = view.findViewById(R.id.password_created_confirmed);

        Button crear_registro = view.findViewById(R.id.crear_registro);
        crear_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(Email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success
                                Log.d(TAG, "createUserWithEmail:success");

                                //sale el cuadro de díalogo para seleccionar la relacion con la comunidad
                                ((AuthActivity) requireActivity()).createUser(name, surname);

                                //enviar un código de verificación al email
                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                Toast.makeText(getActivity(), "Verifique su correo electrónico con el link enviado al correo proporcionado y luego inicie sesión", Toast.LENGTH_LONG).show();

                            } else {
                                // If sign up fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IllegalArgumentException e) {
                    Log.w(TAG, "createUserWithEmail:failure", e);
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}