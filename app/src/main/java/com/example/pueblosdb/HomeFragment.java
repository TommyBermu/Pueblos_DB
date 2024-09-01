package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser User;
    private TextView tv1, tv2, tv3;
    private Button btnchangecarpet, btnjoingroup_requirement, btnfinance_status, deleteaccountbutton, logoutbutton, watchmydata;
    private String Email;
    private SharedPreferences prefs;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        Email = mAuth.getCurrentUser().getEmail();
        User = mAuth.getCurrentUser();
        tv1 = view.findViewById(R.id.emailvisualizer);
        tv1.setText(Email);
        tv2 = view.findViewById(R.id.namevisualizer);
        tv3 = view.findViewById(R.id.surnamevisualizer);
        prefs = getActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);

        btnchangecarpet = view.findViewById(R.id.btnchangecarpet);
        btnjoingroup_requirement = view.findViewById(R.id.btnjoingroup_requirement);
        btnfinance_status = view.findViewById(R.id.btnfinance_status);

        watchmydata = view.findViewById(R.id.watchmydata);
        watchmydata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewData();
            }
        });

        logoutbutton = view.findViewById(R.id.logoutbutton);
        logoutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                salir();
            }
        });

        deleteaccountbutton = view.findViewById(R.id.deleteaccountbutton);
        deleteaccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        return view;
    }

    public void viewData() {
        tv2.setText(prefs.getString("name", "No hay datos"));
        tv3.setText(prefs.getString("surname", "No hay datos"));
    }

    public void deleteAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        Button confirm = dialogView.findViewById(R.id.confirm_dialog);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etv1 = dialogView.findViewById(R.id.email_dialog);
                EditText ptv2 = dialogView.findViewById(R.id.password_dialog);
                String email = etv1.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(email, ptv2.getText().toString());
                reauthenticate(credential, dialog);
            }
        });
    }

    private void salir() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        mAuth.signOut();
        Intent auth = new Intent(getActivity(), AuthActivity.class);
        startActivity(auth);
    }

    private void reauthenticate(AuthCredential credential, AlertDialog dialog){
        User.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    User.delete().addOnCompleteListener(new OnCompleteListener<Void>(){
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            db.collection("users").document(User.getEmail()).delete();
                            Toast.makeText(getActivity(), "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                            salir();
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Error al autenticar la cuenta", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
}