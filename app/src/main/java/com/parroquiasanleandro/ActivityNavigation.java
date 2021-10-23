package com.parroquiasanleandro;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parroquiasanleandro.databinding.ActivityNavigationBinding;

public class ActivityNavigation extends AppCompatActivity {
    private final Activity activity = ActivityNavigation.this;
    private final Context context = ActivityNavigation.this;

    private ActivityNavigationBinding binding;
    private BottomNavigationView navView;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.bottomNavigationView);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_fragment_inicio, R.id.navigation_fragment_avisos, R.id.navigation_fragment_menu,
                R.id.navigation_fragment_informacion, R.id.navigation_fragment_perfil)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navigation);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Usuario.actualizarUsuarioLocal(context, user);
        }
    }
}
