package com.example.pueblosdb;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentResultListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.pueblosdb.clases.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class SelectionFragment extends Fragment {
    private String name, surname, Email;

    public SelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        return inflater.inflate(R.layout.fragment_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Email =  FirebaseAuth.getInstance().getCurrentUser().getEmail();

        Button member = view.findViewById(R.id.btnSoyMiembro);
        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(User.Cargo.COMUNERO);
            }
        });

        Button nonMember = view.findViewById(R.id.btnNOSoyMiembro);
        nonMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(User.Cargo.EXTERNO);
            }
        });
    }

    public void createUser(User.Cargo cargo) {
        User.createUser(requireActivity(), cargo, name, surname, Email);
    }
}