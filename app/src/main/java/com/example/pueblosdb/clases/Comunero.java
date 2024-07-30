package com.example.pueblosdb.clases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class Comunero extends User {
    private String cargo;

    public Comunero(){}

    public Comunero (String nombre, String apellido, String cargo) {
        super(nombre, apellido);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public void verInformacion(FirebaseFirestore db, String email) {

        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Comunero user = documentSnapshot.toObject(Comunero.class);
                Log.d("Query", user.getNombre());
                Log.d("Query", user.getApellidos());
            }
        });
    }
}
