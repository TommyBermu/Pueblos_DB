package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pueblosdb.clases.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.util.HashSet;

public class UserProfileFragment extends Fragment {
    private SharedPreferences prefs;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        TextView tv1 = view.findViewById(R.id.user_emailvisualizer);
        tv1.setText(prefs.getString("email", "No hay datos"));
        TextView tv2 = view.findViewById(R.id.user_namevisualizer);
        tv2.setText(prefs.getString("name", "No hay datos"));
        TextView tv3 = view.findViewById(R.id.user_surnamevisualizer);
        tv3.setText(prefs.getString("surname", "No hay datos"));
        TextView tv4 = view.findViewById(R.id.user_showCargo);
        tv4.setText(prefs.getString("cargo", "No hay datos"));


        Button changeEmail = view.findViewById(R.id.user_changeEmail);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_email, null);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button emailConfirm = dialogView.findViewById(R.id.email_confirm_dialog);
                emailConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "Falta implementar el cambio de email:p", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
            }
        });

        Button changePassword = view.findViewById(R.id.user_changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button emailConfirm = dialogView.findViewById(R.id.password_confirm_dialog);
                emailConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(requireActivity(), "Falta implementar el cambio de contraseña :p", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
            }
        });

        Button saveChanges = view.findViewById(R.id.user_saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireActivity(), prefs.getStringSet("inscripciones", new HashSet<String>()).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteAccount = view.findViewById(R.id.user_deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button confirm = dialogView.findViewById(R.id.delete_confirm_dialog);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText etv1 = dialogView.findViewById(R.id.email_dialog);
                        EditText ptv2 = dialogView.findViewById(R.id.password_dialog);
                        try{
                            AuthCredential credential = EmailAuthProvider.getCredential(etv1.getText().toString(), ptv2.getText().toString());
                            User.deleteUser(credential, requireActivity());
                            dialog.cancel();
                        }catch (IllegalArgumentException e){
                            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }
}