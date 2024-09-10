package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pueblosdb.clases.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;


public class ProfileComuFragment extends Fragment {
    private SharedPreferences prefs;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String birthday;

    public ProfileComuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_comu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Mi Perfil
        prefs = requireActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        TextView tv1 = view.findViewById(R.id.emailvisualizer);
        tv1.setText(prefs.getString("email", "No hay datos"));
        EditText tv2 = view.findViewById(R.id.namevisualizer);
        tv2.setText(prefs.getString("name", "No hay datos"));
        EditText tv3 = view.findViewById(R.id.surnamevisualizer);
        tv3.setText(prefs.getString("surname", "No hay datos"));
        TextView tv4 = view.findViewById(R.id.showCargo);
        tv4.setText(prefs.getString("cargo", "No hay datos"));

        //Otros Datos Personales
        EditText etv1 = view.findViewById(R.id.nameMother);
        etv1.setText(prefs.getString("nombre Madre", null));
        EditText etv2 = view.findViewById(R.id.surnameMother);
        etv2.setText(prefs.getString("apellidos Madre", null));
        EditText etv3 = view.findViewById(R.id.nameFather);
        etv3.setText(prefs.getString("nombre Padre", null));
        EditText etv4 = view.findViewById(R.id.surnameFather);
        etv4.setText(prefs.getString("apellidos Padre", null));
        EditText etv5 = view.findViewById(R.id.profession);
        etv5.setText(prefs.getString("profesion", null));

        TextView birthDate = view.findViewById(R.id.birthdayDate);
        birthDate.setText(prefs.getString("fecha de nacimiento", null));

        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(requireActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf_end = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                birthday = dayOfMonth + "/" + (month+1) + "/" + year;
                try {
                    if (sdf_end.parse(birthday).before(new Date())){
                        birthDate.setText(birthday);
                    } else {
                        Toast.makeText(requireActivity(), "Seleccione una fecha anterior a hoy", Toast.LENGTH_SHORT).show();
                        birthDate.setText("");
                        birthday = null;
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Spinner spinner = view.findViewById(R.id.clanSpinner);
        String[] clanes = {"clan1", "clan2", "clan3", "clan4", "clan5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, clanes);
        spinner.setAdapter(adapter);

        //spinner.setSelection(clanes.);

        Button saveChanges = view.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = tv2.getText().toString();
                String apellidos = tv3.getText().toString();
                String nombreMadre = etv1.getText().toString();
                String apellidosMadre = etv2.getText().toString();
                String nombrePadre = etv3.getText().toString();
                String apellidosPadre = etv4.getText().toString();
                String profesion = etv5.getText().toString();

                String clan = spinner.getSelectedItem().toString();
                String sexo = "";

                RadioButton rb1 = view.findViewById(R.id.radioButton);
                RadioButton rb2 = view.findViewById(R.id.radioButton2);
                RadioButton rb3 = view.findViewById(R.id.radioButton3);

                if (rb1.isChecked()) {
                    sexo = "Femenino";
                } else if (rb2.isChecked()) {
                    sexo = "Masculino";
                } else if (rb3.isChecked()) {
                    sexo = "Otro";
                }

                if (prefs.getString("cargo", User.Cargo.EXTERNO.toString()).equals(User.Cargo.EXTERNO.toString())) {
                    User.updateInfo(nombre, apellidos);

                } else if (!nombreMadre.isEmpty() && !apellidosMadre.isEmpty() && !nombrePadre.isEmpty() && !apellidosPadre.isEmpty() && birthday != null && !sexo.isEmpty() && !clan.isEmpty() && !profesion.isEmpty()) {
                    User.updateInfo(requireActivity(), nombre, apellidos, nombreMadre, apellidosMadre, nombrePadre, apellidosPadre, birthday, sexo, clan, profesion);

                } else {
                    Toast.makeText(requireActivity(), "Debes llenar y/o seleccionar todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button changeEmail = view.findViewById(R.id.changeEmail);
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
                        Toast.makeText(requireActivity(), "info: " + prefs.getBoolean("completeInfo", false), Toast.LENGTH_SHORT).show();
                        Toast.makeText(requireActivity(), "Falta implementar el cambio de email :p", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
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

        Button deleteAccount = view.findViewById(R.id.deleteAccount);
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
                            User.deleteUser(requireActivity(), credential);
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