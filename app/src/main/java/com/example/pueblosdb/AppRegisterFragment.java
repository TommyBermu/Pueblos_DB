package com.example.pueblosdb;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppRegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppRegisterFragment extends Fragment {
    private EditText tv1, tv2;
    private Button setatributes;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AppRegisterFragment() {
        // Required empty public constructor
    }

    public static AppRegisterFragment newInstance(String param1, String param2) {
        AppRegisterFragment fragment = new AppRegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_app_register, container, false);
        tv1 = view.findViewById(R.id.nombre);
        tv2 = view.findViewById(R.id.apellido);

        setatributes = view.findViewById(R.id.button2);
        setatributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String name = tv1.getText().toString();
                    String surname = tv2.getText().toString();
                    if (name.isEmpty() || surname.isEmpty())
                        throw new IllegalArgumentException("Requiere rellenar todos los campos");

                    //se crea el usuario el docuemnto en la base de datos
                    Bundle bundle = new Bundle();
                    bundle.putString("Nombres", name);
                    bundle.putString("Apellidos", surname);
                    getActivity().getSupportFragmentManager().setFragmentResult("key", bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SelectionFragment()).commit();

                } catch (IllegalArgumentException e) {
                    Log.w("EmailPassword", "createDocument: failure", e);
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}