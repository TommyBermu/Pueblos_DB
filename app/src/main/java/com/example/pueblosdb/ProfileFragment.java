package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private TextView tv1, tv2, tv3, tv4;
    private SharedPreferences prefs;
    private FirebaseUser User;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        User = FirebaseAuth.getInstance().getCurrentUser();

        prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        tv1 = view.findViewById(R.id.emailvisualizer);
        tv1.setText(prefs.getString("email", "No hay datos"));
        tv2 = view.findViewById(R.id.namevisualizer);
        tv2.setText(prefs.getString("name", "No hay datos"));
        tv3 = view.findViewById(R.id.surnamevisualizer);
        tv3.setText(prefs.getString("surname", "No hay datos"));
        tv4 = view.findViewById(R.id.showCargo);
        tv4.setText(prefs.getString("cargo", "No hay datos"));


        Button changeEmail = view.findViewById(R.id.changeEmail);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_email, null);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                Button emailConfirm = dialogView.findViewById(R.id.email_confirm_dialog);
                emailConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "Falta implementar el cambio de email:p", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button changePassword = view.findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                Button emailConfirm = dialogView.findViewById(R.id.password_confirm_dialog);
                emailConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "Falta implementar el cambio de contraseña :p", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button saveChanges = view.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), "Falta implementar el cambio de Datos :p", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteAccount = view.findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        return view;
    }

    public void deleteAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button confirm = dialogView.findViewById(R.id.delete_confirm_dialog);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etv1 = dialogView.findViewById(R.id.email_dialog);
                EditText ptv2 = dialogView.findViewById(R.id.password_dialog);
                try{
                    AuthCredential credential = EmailAuthProvider.getCredential(etv1.getText().toString(), ptv2.getText().toString());
                    reauthenticate(credential, dialog);
                }catch (IllegalArgumentException e){
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void reauthenticate(AuthCredential credential, AlertDialog dialog){
        User.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.delete().addOnCompleteListener(new OnCompleteListener<Void>(){
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.collection("users").document(Objects.requireNonNull(User.getEmail())).delete();
                            Toast.makeText(requireActivity(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                            ((MainActivity)requireActivity()).salir();
                        }
                    });

                } else {
                    Toast.makeText(requireActivity(), "Error al autenticar la cuenta", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
}