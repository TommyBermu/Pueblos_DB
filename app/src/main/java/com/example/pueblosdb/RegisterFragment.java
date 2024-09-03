package com.example.pueblosdb;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private EditText tv1, tv2, tv3, tv4, tv5;
    private static final String TAG = "EmailPassword";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

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

                                //enviar un código de verificación al email
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.sendEmailVerification();
                                Toast.makeText(getActivity(), "Verifique su correo electrónico con el link enviado al correo proporcionado y luego inicie sesión", Toast.LENGTH_LONG).show();

                                //se crea el usuario el docuemnto en la base de datos
                                Bundle bundle = new Bundle();
                                bundle.putString("Nombres", name);
                                bundle.putString("Apellidos", surname);
                                getActivity().getSupportFragmentManager().setFragmentResult("key", bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectionFragment()).commit();
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
        return view;
    }
}