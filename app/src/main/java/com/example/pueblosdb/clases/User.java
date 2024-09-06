package com.example.pueblosdb.clases;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.pueblosdb.AuthActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public enum Cargo {
        EXTERNO,
        COMUNERO,
        ADMIN
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
                            User.logOut(context, prefs);
                        }
                    });
                } else {
                    Toast.makeText(context, "Error al autenticar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    static public void logOut(Context context, @NonNull SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent auth = new Intent(context, AuthActivity.class);
        context.startActivity(auth);
    }
}