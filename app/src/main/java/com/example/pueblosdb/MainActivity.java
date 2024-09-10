package com.example.pueblosdb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.pueblosdb.clases.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        Window window = getWindow();
        // quita el StatusBar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // pone el Navigation Bar de color azul oscuro
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        /* esto es para quitar el NavigationBar ,tambien hay que quitar la opcion de (android:fitsSystemWindows="true") en el activity_main.xml para que funcione bien sin la barra
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        */

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        if (prefs.getString("cargo", User.Cargo.EXTERNO.toString()).equals(User.Cargo.ADMIN.toString())){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.admin_nav_menu);
        }
        if (prefs.getString("cargo", User.Cargo.EXTERNO.toString()).equals(User.Cargo.EXTERNO.toString())){
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.ext_nav_menu);
        }

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.home_bottom_menu){
                replaceFragment(new HomeFragment());

            } else if (id == R.id.market_bottom_menu){
                replaceFragment(new MarketFragment());

            } else if (id == R.id.library_bottom_menu){
                replaceFragment(new LibraryFragment());

            } else if (id == R.id.profile_bottom_menu){
                if (prefs.getString("cargo", User.Cargo.EXTERNO.toString()).equals(User.Cargo.EXTERNO.toString()))
                    replaceFragment(new ProfileUserFragment());
                else
                    replaceFragment(new ProfileComuFragment());
            }
            return true;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.folder_change_menu && canAccess()) {
            replaceFragment(new ChangeFolderFragment());
        } else if (id == R.id.group_join_menu && canAccess()) {
            replaceFragment(new GroupsFragment());

        } else if (id == R.id.finance_status_menu && canAccess()) {
            replaceFragment(new FinanceFragment());
        } else if (id == R.id.logout_menu) {
            User.logOut(this);
        }else if (id == R.id.admin_options_menu) {
            replaceFragment(new AdminOptionsFragment());
        } else if (id == R.id.create_publication_menu) {
            replaceFragment(new PublishFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null || !mAuth.getCurrentUser().isEmailVerified() || prefs.getString("email", null) == null){
            Intent auth = new Intent(this, AuthActivity.class);
            startActivity(auth);
        }
    }

    private boolean canAccess(){
        if (prefs.getBoolean("completeInfo", false))
            return true;
        Toast.makeText(this, "Debe completar la información primero", Toast.LENGTH_SHORT).show();
        replaceFragment(new ProfileComuFragment());
        return false;
    }

    private void replaceFragment(Fragment fragment){
        User.replaceFragment(this, fragment);
    }
}