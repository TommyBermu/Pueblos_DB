package com.example.pueblosdb.clases;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Comunero extends User {
    private String cargo;

    public Comunero(){}

    public Comunero (String nombre, String apellido, String email, String cargo) {
        super(nombre, apellido, email);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public void verInformacion(FirebaseFirestore db) {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //tv2.setText(task.getResult().getDocuments().get(0).get("nombre").toString());
                    //tv3.setText(task.getResult().getDocuments().get(0).get("apellido").toString());
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("Query", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w("FailedQuery", "Error getting documents.", task.getException());
                }
            }
        });
    }
}
