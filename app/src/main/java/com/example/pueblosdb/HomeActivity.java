package com.example.pueblosdb;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
//import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    private final FirebaseFirestore db  = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private TextView tv1, tv2, tv3;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ///
        mAuth = FirebaseAuth.getInstance();
        Email = mAuth.getCurrentUser().getEmail();
        tv1 = findViewById(R.id.emailvisualizer);
        tv1.setText(Email);
        tv2 = findViewById(R.id.namevisualizer);
        tv3 = findViewById(R.id.surnamevisualizer);

        //Navigation menu
        //TODO reemplazar en el xml el constraintLayout por DrawerLayout, porque no funciona con el constraintLayout
        /*
        drawerLayout = findViewById(R.id.main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });*/
    }


    /*
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home_menu) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);

        } else if (id == R.id.profile_menu) {
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);

        } else if (id == R.id.whatsapp_menu) {
            Toast.makeText(this, "Contact Us", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.logout_menu) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();

        }else if (id == R.id.delete_menu) {
            Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }*/

    public void gotoMyProfile(View view){
        Intent intent = new Intent(this, MyProfile.class);
        startActivity(intent);
    }

    public void gotoFileSolicitude(View view){
        Intent intent = new Intent(this, FileSolicitude.class);
        startActivity(intent);
    }


    public void viewData(View view) {
        //comunero.verInformacion(db, Email);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        tv2.setText(prefs.getString("name", "No hay datos"));
        tv3.setText(prefs.getString("surname", "No hay datos"));
    }

    public void logOut(View view) {
        SharedPreferences.Editor prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit();
        prefs.clear().apply();
        //LoginManager.getInstance().logOut();
        mAuth.signOut();
        Intent auth = new Intent(this, AuthActivity.class);
        startActivity(auth);
    }
}