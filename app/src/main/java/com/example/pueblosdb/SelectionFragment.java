package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pueblosdb.clases.Cargo;
import com.example.pueblosdb.clases.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectionFragment extends Fragment {
    private String name, surname, Email;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectionFragment newInstance(String param1, String param2) {
        SelectionFragment fragment = new SelectionFragment();
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

        getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                name = result.getString("Nombres");
                surname = result.getString("Apellidos");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selection, container, false);

        Email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Button member = view.findViewById(R.id.btnSoyMiembro);
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(Cargo.COMUNERO);
            }
        });

        Button nonMember = view.findViewById(R.id.btnNOSoyMiembro);
        nonMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(Cargo.EXTERNO);
            }
        });

        return view;
    }

    public void createUser(Cargo cargo){
        db.collection("users").document(Email).set(new User(name, surname, cargo));
        //salir de la cuenta para ir al AuthActivity
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LogInFragment()).commit();
    }
}