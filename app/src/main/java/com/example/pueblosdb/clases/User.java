package com.example.pueblosdb.clases;

import static android.content.Context.MODE_PRIVATE;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.pueblosdb.AuthActivity;
import com.example.pueblosdb.LogInFragment;
import com.example.pueblosdb.MainActivity;
import com.example.pueblosdb.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class User {
    // attributes
    private String nombre;
    private String apellidos;
    private Cargo cargo;
    private HashMap<String, Boolean> inscripciones;
    //Date fecha de nacimiento
    //String profesion
    //String clan
    //String carpeta
    //boolean sexo

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


    public static void createUser(FragmentActivity context, Cargo cargo, String name, String surname, String Email) {
        final String TAG = "CreateUser:EmailPassword";
        FirebaseFirestore.getInstance().collection("users").document(Email).set(new User(name, surname, cargo, new HashMap<String, Boolean>())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onSucces: docuemnto creado correctamente");
                    logOut(context);
                    replaceFragment(context, new LogInFragment());
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null : "No hay usuario registrado";
                    user.delete();
                    Log.e(TAG, "onFailure: " + task.getException().getMessage());
                }
            }
        });
    }

    public static void deleteUser(AuthCredential credential, FragmentActivity context){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null : "User is null";
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            FirebaseFirestore.getInstance().collection("users").document(user.getEmail()).delete();
                            //sale que puede ser NULL porque se puede iniciar sesión con telefono, pero eso no está implementado.
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

    public static void logIn(String Email, String Password, FragmentActivity context){
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String TAG = "LogIn:EmailPassword";

        if (Email.isEmpty() || Password.isEmpty()) {
            Log.w(TAG, "failure: requiere llenar todos lo campos");
            Toast.makeText(context, "requiere llenar todos lo campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (!mAuth.getCurrentUser().isEmailVerified()) {
                        //lo mismo, sale que puede ser NULL porque puede estar registrado con telefono, pero eso no está implementado.
                        Log.w(TAG, "Email is not verified");
                        Toast.makeText(context, "Email is not verified", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        return;
                    }
                    Log.d(TAG, "LogInWithEmailAndPassword: success");
                    //a veces no se conecta pero es por el android studio xd
                    FirebaseFirestore.getInstance().collection("users").document(Email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                // si se recuperó el docuemto correctamente, agergar las preferencias e ir a la Main activity
                                Log.d(TAG, "onSuccess: added Preferences");
                                agregarPreferencias(task.getResult(), Email, context);
                                Intent main = new Intent(context, MainActivity.class);
                                context.startActivity(main);
                            }
                            else {
                                Log.e(TAG, "onFailure: Logging out", task.getException());
                                Toast.makeText(context, "Error al iniciar sesión, verifique su conexión a internet o intente nuevamente", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "signInWithEmail: failure", task.getException());
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void logOut(FragmentActivity context){
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R.string.prefs_file), MODE_PRIVATE).edit();
        editor.clear().apply();
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Intent auth = new Intent(context, AuthActivity.class);
        context.startActivity(auth);
    }

    public static void agregarPreferencias(DocumentSnapshot document, String email, FragmentActivity context) {
        User user = document.toObject(User.class);
        SharedPreferences.Editor prefsEditor = context.getSharedPreferences(context.getString(R.string.prefs_file), MODE_PRIVATE).edit();
        prefsEditor.putString("email", email);
        prefsEditor.putString("name", user.getNombre());
        prefsEditor.putString("surname", user.getApellidos());
        prefsEditor.putString("cargo", user.getCargo().toString());
        prefsEditor.putStringSet("inscripciones", user.getInscripciones().keySet());
        prefsEditor.apply();
    }

    public static void replaceFragment(FragmentActivity context, Fragment fragment){
        context.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}