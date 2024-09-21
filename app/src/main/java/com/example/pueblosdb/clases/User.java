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
import com.example.pueblosdb.MainActivity;
import com.example.pueblosdb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class User {
    // attributes
    private String nombre;
    private String apellidos;
    private Cargo cargo;
    private HashMap<String, Boolean> inscripciones;
    private Boolean completeInfo;
    private String email;

    //appInfo
    private FragmentActivity context;
    private FirebaseAuth mAuth;
    private FirebaseUser fUser;
    private SharedPreferences prefs;
    private FirebaseFirestore db;

    /**
     * empty constructor
     */
    public User(){}

    /**
     * constructor que se usa en las Activities
     * @param context contexto para ejecutar la mayoria de los metodos
     */
    public User(@NonNull FragmentActivity context){
        mAuth = FirebaseAuth.getInstance();
        fUser = mAuth.getCurrentUser();
        prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), MODE_PRIVATE);;
        db = FirebaseFirestore.getInstance();

        this.nombre = prefs.getString("name", "No hay datos");
        this.apellidos = prefs.getString("surname", "No hay datos");
        this.cargo = Cargo.valueOf(prefs.getString("cargo", "No hay datos"));
        this.completeInfo = prefs.getBoolean("completeInfo", false);
        try {
            this.email = fUser.getEmail(); // si es null significa que está en la AuthActivity
        } catch (NullPointerException E){
            this.email = "No hay datos";
        }
        this.context = context;
    }

    /**
     * Constructor que se envía a firebase
     * @param nombre nombre del usuario
     * @param apellidos apellidos del usuario
     * @param cargo cargo del usuario
     * @param inscripciones inscripciones del usuario
     * @param completeInfo si el usuario ha completado la información
     */
    public User(String nombre, String apellidos, Cargo cargo, HashMap<String, Boolean> inscripciones, Boolean completeInfo, String email) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.cargo = cargo;
        this.inscripciones = inscripciones;
        this.completeInfo = completeInfo;
        this.email = email;
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

    public Boolean isCompleteInfo() {
        return completeInfo;
    }

    public void setCompleteInfo(Boolean completeInfo) {
        this.completeInfo = completeInfo;
    }

    public String getEmail(){
        return email;
    }

    public enum Cargo {
        EXTERNO,
        COMUNERO,
        ADMIN
    }

    public void inscribirse(){
        //TODO hacer que se inscriba en un grupo o en una convocatoria (debe pasar parámetros respectivos)
    }


    public void createUser(Cargo cargo, String name, String surname, String Email) {
        final String TAG = "CreateUser:EmailPassword";
        db.collection("users").document(Email).set(new User(name, surname, cargo, new HashMap<String, Boolean>(), false, Email)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onSucces: docuemnto creado correctamente");
                    logOut();
                } else {
                    fUser.delete();
                    Log.e(TAG, "onFailure, no se pudo crear el documento, se elimina el usuario : " + task.getException().getMessage());
                }
            }
        });
    }

    public void deleteUser(AuthCredential credential){
        fUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    fUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            db.collection("users").document(email).delete();
                            Toast.makeText(context, "Cuenta eliminada", Toast.LENGTH_SHORT).show();
                            logOut();
                        }
                    });
                } else {
                    Toast.makeText(context, "Error al autenticar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateInfo(String nombres, String apellidos){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.update("nombre", nombres);
        docRef.update("apellidos", apellidos);
        prefs.edit().putString("name", nombres).putString("surname", apellidos).apply();
        setNombre(nombres);
        setApellidos(apellidos);
    }

    public void updateInfo(String nombres, String apellidos, String naneMadre, String surnameMadre, String namePadre, String surnamePadre, String fechaNacimiento, String sexo, int clan, String prefesion){

        Map<String, Object> info = new HashMap<>();
        info.put("nombre Madre", naneMadre);
        info.put("apellidos Madre", surnameMadre);
        info.put("nombre Padre", namePadre);
        info.put("apellidos Padre", surnamePadre);
        info.put("fecha de nacimiento", fechaNacimiento);
        info.put("sexo", sexo);
        info.put("clan", clan);
        info.put("profesion", prefesion);

        if (completeInfo){
            db.collection("info_comunero").document(email).update(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    addInfo(info, nombres, apellidos, completeInfo);
                }
            });
        } else {
            db.collection("info_comunero").document(email).set(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    addInfo(info, nombres, apellidos, completeInfo);
                }
            });
        }
    }

    public void logIn(@NonNull String Email, String Password){
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
                    if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                        //lo mismo, sale que puede ser NULL porque puede estar registrado con telefono, pero eso no está implementado.
                        Log.w(TAG, "Email is not verified");
                        Toast.makeText(context, "Email is not verified", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        return;
                    }
                    Log.d(TAG, "LogInWithEmailAndPassword: success");
                    //a veces no se conecta pero es por el android studio xd
                    db.collection("users").document(Email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                // si se recuperó el docuemto correctamente, agergar las preferencias e ir a la Main activity
                                Log.d(TAG, "onSuccess: added Preferences");
                                agregarPreferencias(task.getResult(), Email);
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

    public void ApplogIn(){ // TODO log in con cuentas externas (GOOGLE, Facebook)

    }

    public void logOut(){
        final String TAG = "LogOut";

        prefs.edit().clear().apply();
        //LoginManager.getInstance().logOut(); TODO hablilitar cuando se haga lo de facebook
        FirebaseAuth.getInstance().signOut();
        Intent auth = new Intent(context, AuthActivity.class);
        context.startActivity(auth);
        Log.d(TAG, "LogOut: success");
    }

    /** ** ** ** ** ** ** *
     * Some minor methods *
     * ** ** ** * ** ** **/

    public void agregarPreferencias(@NonNull DocumentSnapshot document, String email) {
        User user = document.toObject(User.class);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("email", email);
        prefsEditor.putString("name", user.getNombre());
        prefsEditor.putString("surname", user.getApellidos());
        prefsEditor.putString("cargo", user.getCargo().toString());
        prefsEditor.putStringSet("inscripciones", user.getInscripciones().keySet());
        prefsEditor.putBoolean("completeInfo", user.isCompleteInfo());

        if (!user.getCargo().equals(Cargo.EXTERNO) && user.isCompleteInfo()){
            db.collection("info_comunero").document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> info = documentSnapshot.getData();
                    for (String key : info.keySet()){
                        prefsEditor.putString(key, "" + info.get(key));
                    }
                    prefsEditor.apply();
                }
            });
        }
        prefsEditor.apply();
    }

    private void addInfo(@NonNull Map<String, Object> info, String nombres, String apellidos, boolean completeInfo) {
        //edita el documento
        DocumentReference docRef = db.collection("users").document(email);
        docRef.update("nombre", nombres);
        docRef.update("apellidos", apellidos);
        //edita las preferencias
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", nombres).putString("surname", apellidos);
        for (String key : info.keySet()) {
            editor.putString(key, "" + info.get(key));
        }
        //edita el objeto
        setNombre(nombres);
        setApellidos(apellidos);
        if (!completeInfo){
            docRef.update("completeInfo", true);
            editor.putBoolean("completeInfo", true);
            setCompleteInfo(true);
        }
        editor.apply();
        Toast.makeText(context, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
    }

    public void replaceFragment(Fragment fragment){
        context.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    @NonNull
    @Override
    public String toString() {
        return "nombre: " + this.nombre + ", apellidos: " + this.apellidos + ", cargo: " + this.cargo;
    }
}