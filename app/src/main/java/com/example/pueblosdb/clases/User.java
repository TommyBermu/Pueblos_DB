package com.example.pueblosdb.clases;

import static android.content.Context.MODE_PRIVATE;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pueblosdb.AuthActivity;
import com.example.pueblosdb.MainActivity;
import com.example.pueblosdb.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class User {

    // attributes
    protected String nombre;
    protected String apellidos;
    protected Cargo cargo;
    //fecha de nacimiento
    public User(){}

    public User(String nombre, String apellidos, Cargo cargo) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cargo = cargo;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
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

    public void verInformacion(){};

    static public void salir(){

    }
}