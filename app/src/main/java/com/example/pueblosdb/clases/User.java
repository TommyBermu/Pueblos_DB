package com.example.pueblosdb.clases;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class User {

    // attributes
    private String nombre;
    private String apellidos;
    //fecha de nacimiento
    public User(){}

    public User(String nombre, String apellidos) {
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public abstract void verInformacion(FirebaseFirestore db, String email);
}
