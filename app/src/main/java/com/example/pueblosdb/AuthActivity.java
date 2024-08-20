package com.example.pueblosdb;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.*;
import com.example.pueblosdb.clases.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private EditText tv1;
    private TextInputLayout tv2;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private GoogleSignInClient gsc;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private final CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //SplashScreen
        setTheme(R.style.Theme_PueblosDB);

        mAuth = FirebaseAuth.getInstance();
        SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit().clear();
        prefs.apply();

        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tv1 = findViewById(R.id.email);
        tv2 = findViewById(R.id.password_container);

        //Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        gsc = GoogleSignIn.getClient(this, gso);
    }

    public void signIn(View view) throws IllegalArgumentException {

        String Email = tv1.getText().toString();
        String Password = Objects.requireNonNull(tv2.getEditText()).getText().toString();
        try {
            if (Email.isEmpty() || Password.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        try {
                            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                throw new IllegalArgumentException("Verifique su correo electrónico");

                            // Sign in success
                            Log.d(TAG, "signInWithEmail: success");
                            DocumentReference docRef = db.collection("users").document(Email);
                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    agregarPreferencias(documentSnapshot);
                                }
                            });
                            //ir a la home activity
                            Intent home = new Intent(AuthActivity.this, HomeActivity.class);
                            startActivity(home);
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        Toast.makeText(AuthActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "signInWithEmail:failure", e);
            Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void googleSignIn(View view){
        Intent intent = gsc.getSignInIntent();
        gsc.signOut();
        activityResultLauncher.launch(intent);
    }



    public void facebookSignIn(View view){
        Toast.makeText(AuthActivity.this, "Funcionalidad no disponible", Toast.LENGTH_SHORT).show();
        /* //TODO esperar a que facebook se le de la gana de volver a implementar las cuentas de prueba para poder probar el login con facebook

        LoginManager.getInstance().logInWithReadPermissions(AuthActivity.this, Collections.singletonList("email"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();

                AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
                authUser(credential);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(@NonNull FacebookException exception) {
                Toast.makeText(AuthActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
      */
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            int result = o.getResultCode();
                            Intent data = o.getData();

                            callbackManager.onActivityResult(1, result, data);

                            if (result == RESULT_OK){
                                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                                try {
                                    GoogleSignInAccount account = task.getResult(ApiException.class);
                                    if (account != null){
                                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                                        authUser(credential);
                                    }
                                } catch (Exception e){
                                    Toast.makeText(AuthActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
            );

    private void authUser(AuthCredential credential){
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success
                    Log.d(TAG, "signInWithApp: success");
                    //ir a la google register activity si ho nay datos en la db
                    db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {

                                    agregarPreferencias(document);

                                    Intent home = new Intent(AuthActivity.this, HomeActivity.class);
                                    startActivity(home);
                                } else {
                                    Intent gra = new Intent(AuthActivity.this, AppRegisterActivity.class);
                                    startActivity(gra);
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithApp: failure", task.getException());
                    Toast.makeText(AuthActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void agregarPreferencias(DocumentSnapshot document) {
        User user = document.toObject(User.class);
        SharedPreferences.Editor prefsEditor = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit();
        prefsEditor.putString("name", user.getNombre());
        prefsEditor.putString("surname", user.getApellidos());
        prefsEditor.apply();
    }

    public void signUp(View view) {
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }
}