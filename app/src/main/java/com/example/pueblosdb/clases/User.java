package com.example.pueblosdb.clases;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.pueblosdb.AuthActivity;
import com.example.pueblosdb.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class User {

    // attributes
    protected String nombre;
    protected String apellidos;
    protected Cargo cargo;
    protected HashMap<String, Boolean> inscripciones;
    //fecha de nacimiento
    public User(){}

    public User(String nombre, String apellidos, Cargo cargo, HashMap<String, Boolean> inscripciones) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.inscripciones = inscripciones;
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

    public Set<String> getInscripciones() {
        return inscripciones.keySet();
    }

    public void setInscripciones(HashMap<String, Boolean> inscripciones) {
        this.inscripciones = inscripciones;
    }

    public enum Cargo {
        EXTERNO,
        COMUNERO,
        ADMIN
    }

    public static void inscribirse(){

    }

    static public void createUser(){

    }

    static public void deleteUser(AuthCredential credential, Context context, SharedPreferences prefs, @NonNull FirebaseUser user){
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(user.getEmail())).delete();
                            Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                            logOut(context);
                        }
                    });
                } else {
                    Toast.makeText(context, "Error al autenticar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void logOut(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.prefs_file), MODE_PRIVATE).edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent auth = new Intent(context, AuthActivity.class);
        context.startActivity(auth);
    }
}