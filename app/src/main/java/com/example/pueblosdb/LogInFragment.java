package com.example.pueblosdb;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pueblosdb.clases.User;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment {
    private EditText tv1;
    private TextInputLayout tv2;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private GoogleSignInClient gsc;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private final CallbackManager callbackManager = CallbackManager.Factory.create();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LogInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mAuth = FirebaseAuth.getInstance();
        tv1 = view.findViewById(R.id.email);
        tv2 = view.findViewById(R.id.password_container);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        gsc = GoogleSignIn.getClient(getActivity(), gso);

        Button enter = view.findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        Button google = view.findViewById(R.id.googlesignin);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        Button facebook = view.findViewById(R.id.facebooksignin);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookSignIn();
            }
        });

        Button register = view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        return view;
    }

    public void signIn() throws IllegalArgumentException {

        String Email = tv1.getText().toString();
        String Password = Objects.requireNonNull(tv2.getEditText()).getText().toString();
        try {
            if (Email.isEmpty() || Password.isEmpty())
                throw new IllegalArgumentException("Requiere rellenar todos los campos");

            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        try {
                            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())
                                throw new IllegalArgumentException("Verifique su correo electrónico");

                            // Sign in success
                            Log.d(TAG, "signInWithEmail: success");

                            //a veces no se conecta pero es por el android studio xd
                           db.collection("users").document(Email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d(TAG, "onSuccess: added Preferences");
                                    agregarPreferencias(documentSnapshot, Email);
                                    //ir a la Main activity si se recuperó el docuemto correctamente
                                    Intent main = new Intent(getActivity(), MainActivity.class);
                                    startActivity(main);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.d(TAG, "onFailure: Logging out");
                                   Toast.makeText(getActivity(), "Error al iniciar sesión, verifique su conexión a internet o intente nuevamente", Toast.LENGTH_LONG).show();
                                   FirebaseAuth.getInstance().signOut();
                               }
                           });
                        } catch (IllegalArgumentException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: failure", task.getException());
                        Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "signInWithEmail:failure", e);
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void googleSignIn(){
        Intent intent = gsc.getSignInIntent();
        gsc.signOut();
        activityResultLauncher.launch(intent);
    }



    public void facebookSignIn(){
        Toast.makeText(getActivity(), "Funcionalidad no disponible", Toast.LENGTH_SHORT).show();
        /*TODO esperar a que facebook se le de la gana de volver a implementar las cuentas de prueba para poder probar el login con facebook

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
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
            );

    private void authUser(AuthCredential credential){
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success
                    Log.d(TAG, "signInWithApp: success");
                    //ir a la google register activity si ho nay datos en la db
                    String Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    db.collection("users").document(Email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                agregarPreferencias(documentSnapshot, Email);
                                Intent main = new Intent(getActivity(), MainActivity.class);
                                startActivity(main);
                            } else {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AppRegisterFragment()).commit();
                            }
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithApp: failure", task.getException());
                    Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void agregarPreferencias(DocumentSnapshot document, String email) {
        User user = document.toObject(User.class);
        SharedPreferences.Editor prefsEditor = getActivity().getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit();
        prefsEditor.putString("email", email);
        prefsEditor.putString("name", user.getNombre());
        prefsEditor.putString("surname", user.getApellidos());
        prefsEditor.putString("cargo", user.getCargo().toString());
        prefsEditor.apply();
        prefsEditor.commit();
    }

    public void signUp() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RegisterFragment()).commit();
    }
}