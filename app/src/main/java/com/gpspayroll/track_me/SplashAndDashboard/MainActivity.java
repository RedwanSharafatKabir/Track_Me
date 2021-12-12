package com.gpspayroll.track_me.SplashAndDashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.gpspayroll.track_me.AdminFragment.EmployeesList;
import com.gpspayroll.track_me.AdminFragment.OnFieldEmployees;
import com.gpspayroll.track_me.AdminFragment.OfficeTimelineActivities.OfficeTimeline;
import com.gpspayroll.track_me.AdminFragment.SalaryHistory;
import com.gpspayroll.track_me.ProfileAndAbout.AboutFragment;
import com.gpspayroll.track_me.ProfileAndAbout.ProfileFragment;
import com.gpspayroll.track_me.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{

    public BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gotUserMethod();

        bottomNavigationView = findViewById(R.id.bottomNavigationID);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setBackground(null);

        fragment = new Dashboard();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
        fragmentTransaction.commit();
    }

    private void gotUserMethod(){
        try {
            String recievedMessageTc;
            FileInputStream fileInputStreamTc = openFileInput("Users_Role.txt");
            InputStreamReader inputStreamReaderTc = new InputStreamReader(fileInputStreamTc);
            BufferedReader bufferedReaderTc = new BufferedReader(inputStreamReaderTc);
            StringBuffer stringBufferTc = new StringBuffer();

            while((recievedMessageTc = bufferedReaderTc.readLine())!=null){
                stringBufferTc.append(recievedMessageTc);
            }

            userRole = stringBufferTc.toString();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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

                Bundle bundle = new Bundle();
                bundle.putString("messageRole", userRole);

                fragment = new ProfileFragment();
                fragment.setArguments(bundle);
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

        if(OnFieldEmployees.backBtnListener!=null){
            OnFieldEmployees.backBtnListener.onBackPressed();
        }

        if(OfficeTimeline.backBtnListener!=null){
            OfficeTimeline.backBtnListener.onBackPressed();
        }

        if(SalaryHistory.backBtnListener!=null){
            SalaryHistory.backBtnListener.onBackPressed();
        }

        if(EmployeesList.backBtnListener!=null){
            EmployeesList.backBtnListener.onBackPressed();
        }
    }
}
