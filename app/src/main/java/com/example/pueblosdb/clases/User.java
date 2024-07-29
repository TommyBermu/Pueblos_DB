package com.example.pueblosdb.clases;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class User {

    // attributes
    private String email;
    private String nombre;
    private String apellidos;
    //fecha de nacimiento
    public User(){}

    public User(String nombre, String apellidos, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
    }

    public String getEmail() {
        return email;
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

    public abstract void verInformacion(FirebaseFirestore db);
}
