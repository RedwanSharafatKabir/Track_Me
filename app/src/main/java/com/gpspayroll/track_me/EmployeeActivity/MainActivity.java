package com.gpspayroll.track_me.EmployeeActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.gpspayroll.track_me.About.AboutFragment;
import com.gpspayroll.track_me.AdminActivity.MainActivityAdmin;
import com.gpspayroll.track_me.Authentication.LoginActivity;
import com.gpspayroll.track_me.EmployeeFragment.Dashboard;
import com.gpspayroll.track_me.Profile.ProfileFragment;
import com.gpspayroll.track_me.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    public BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationID);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setBackground(null);

        fragment = new Dashboard();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            // navigation bar items
            case R.id.dashboardID:
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
                fragment = new Dashboard();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
                fragmentTransaction.commit();

                break;

            case R.id.aboutID:
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

                fragment = new AboutFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentID, fragment);
                fragmentTransaction.commit();

                break;

            case R.id.profileID:
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

                fragment = new ProfileFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentID, fragment);
                fragmentTransaction.commit();

                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(Dashboard.backBtnListener!=null){
            Dashboard.backBtnListener.onBackPressed();
        }

        if(AboutFragment.backBtnListener!=null){
            AboutFragment.backBtnListener.onBackPressed();
        }

        if(ProfileFragment.backBtnListener!=null){
            ProfileFragment.backBtnListener.onBackPressed();
        }
    }
}
