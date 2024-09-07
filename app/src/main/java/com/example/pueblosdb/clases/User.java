package com.example.pueblosdb.clases;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.pueblosdb.AuthActivity;
import com.example.pueblosdb.MainActivity;
import com.example.pueblosdb.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Objects;

public class User {
    // attributes
    private String nombre;
    private String apellidos;
    private Cargo cargo;
    private HashMap<String, Boolean> inscripciones;
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

    public HashMap<String, Boolean> getInscripciones() {
        return inscripciones;
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
        //TODO hacer que se inscriba en un grupo o en una convocatoria (debe pasar parámetros respectivos)
    }

    public static void createUser(){

    }

    public static void deleteUser(AuthCredential credential, Context context, SharedPreferences prefs, @NonNull FirebaseUser user){
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

    public static void logIn(String Email, String Password, Context context){
        try {
            if (Email.isEmpty() || Password.isEmpty()) {
                throw new IllegalArgumentException("Requiere rellenar todos los campos");
            }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        try {
                            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                throw new IllegalArgumentException("Verifique su correo electrónico");

                            // Sign in success
                            Log.d("EmailPassword", "signInWithEmail: success");

                            //a veces no se conecta pero es por el android studio xd
                            FirebaseFirestore.getInstance().collection("users").document(Email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d("EmailPassword", "onSuccess: added Preferences");
                                    agregarPreferencias(documentSnapshot, Email, context);
                                    //ir a la Main activity si se recuperó el docuemto correctamente
                                    Intent main = new Intent(context, MainActivity.class);
                                    context.startActivity(main);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("EmailPassword", "onFailure: Logging out");
                                    Toast.makeText(context, "Error al iniciar sesión, verifique su conexión a internet o intente nuevamente", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                }
                            });
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("EmailPassword", "signInWithEmail: failure", task.getException());
                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w("EmailPassword", "signInWithEmail:failure", e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void logOut(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.prefs_file), MODE_PRIVATE).edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent auth = new Intent(context, AuthActivity.class);
        context.startActivity(auth);
    }

    public static void agregarPreferencias(DocumentSnapshot document, String email, Context context) {
        User user = document.toObject(User.class);
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(context.getString(R.string.prefs_file), MODE_PRIVATE).edit();
        prefsEditor.putString("email", email);
        prefsEditor.putString("name", user.getNombre());
        prefsEditor.putString("surname", user.getApellidos());
        prefsEditor.putString("cargo", user.getCargo().toString());
        prefsEditor.putStringSet("inscripciones", user.getInscripciones().keySet());
        prefsEditor.apply();
    }
}