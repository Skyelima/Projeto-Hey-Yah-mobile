package com.heyya.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.heyya.app.data.MockDataManager;
import com.heyya.app.fragments.*;
import com.heyya.app.models.UserData;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private MockDataManager dataManager;
    private TextView navUserName, navUserLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);
            dataManager = new MockDataManager(this);

            // Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Drawer
            drawerLayout = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.nav_open, R.string.nav_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            // Navigation
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            // Nav Header
            View headerView = navigationView.getHeaderView(0);
            if (headerView != null) {
                navUserName = headerView.findViewById(R.id.nav_user_name);
                navUserLevel = headerView.findViewById(R.id.nav_user_level);
            }
            updateNavHeader();

            // Default fragment - Dashboard
            if (savedInstanceState == null) {
                loadFragment(new DashboardFragment());
                navigationView.setCheckedItem(R.id.nav_dashboard);
                setTitle("📊 Dashboard");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
    }

    private void updateNavHeader() {
        try {
            if (dataManager == null) return;
            UserData user = dataManager.getUserData();
            if (navUserName != null) navUserName.setText(MockDataManager.MOCK_NAME);
            if (navUserLevel != null) navUserLevel.setText("🏆 Nível " + user.getNivel() + " — " + user.getNivelTitulo());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        try {
            Fragment fragment = null;
            String title = "";
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                fragment = new DashboardFragment();
                title = "📊 Dashboard";
            } else if (id == R.id.nav_tasks) {
                fragment = new TasksFragment();
                title = "📋 Tarefas";
            } else if (id == R.id.nav_schedule) {
                fragment = new ScheduleFragment();
                title = "⚙️ Escala";
            } else if (id == R.id.nav_ai) {
                fragment = new AIFragment();
                title = "🤖 Sugestões IA";
            } else if (id == R.id.nav_gamification) {
                fragment = new GamificationFragment();
                title = "🏆 Gamificação";
            } else if (id == R.id.nav_logout) {
                dataManager.setLoggedIn(false);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }

            if (fragment != null) {
                loadFragment(fragment);
                setTitle(title);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro navegação: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void refreshNavHeader() {
        updateNavHeader();
    }
}
