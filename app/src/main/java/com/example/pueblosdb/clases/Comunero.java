package com.example.pueblosdb.clases;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Comunero extends User {
    private String cargo;
    Comunero user;

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

    }
}
