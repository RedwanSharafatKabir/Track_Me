package com.gpspayroll.track_me.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.Authentication.LoginActivity;
import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.DashboardAndAbout.MainActivity;
import com.gpspayroll.track_me.DashboardAndAbout.Dashboard;
import com.gpspayroll.track_me.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProfileFragment extends Fragment implements BackListenerFragment, View.OnClickListener {

    private View views;
    private Fragment fragment;
    public static BackListenerFragment backBtnListener;
    private CardView resetPass, logout;
    private FragmentTransaction fragmentTransaction;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private TextView phone, email, name;
    private DatabaseReference databaseReference;
    private String messageRole, userPhone, userEmailText;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_profile, container, false);

        progressBar = views.findViewById(R.id.profileProgressId);
        resetPass = views.findViewById(R.id.resetPassId);
        resetPass.setOnClickListener(this);
        logout = views.findViewById(R.id.logoutId);
        logout.setOnClickListener(this);

        phone = views.findViewById(R.id.userPhoneId);
        email = views.findViewById(R.id.userEmailId);
        name = views.findViewById(R.id.userNameId);

        try{
            messageRole = getArguments().getString("messageRole");
            if(messageRole.equals("adminS")){
                getAdminPhone();
                databaseReference = FirebaseDatabase.getInstance().getReference("Admin Info");

            } else if(messageRole.equals("employeeS")){
                userPhone = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");
            }
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
        }

        getUserInfo();

        return views;
    }

    private void getAdminPhone(){
        try {
            String recievedMessageTc;
            FileInputStream fileInputStreamTc = getActivity().openFileInput("Admin_Phone.txt");
            InputStreamReader inputStreamReaderTc = new InputStreamReader(fileInputStreamTc);
            BufferedReader bufferedReaderTc = new BufferedReader(inputStreamReaderTc);
            StringBuffer stringBufferTc = new StringBuffer();

            while((recievedMessageTc = bufferedReaderTc.readLine())!=null){
                stringBufferTc.append(recievedMessageTc);
            }

            userPhone = stringBufferTc.toString();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUserInfo() {
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            try {
                databaseReference.child(userPhone).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        phone.setText(userPhone);
                        name.setText(snapshot.child("username").getValue().toString());
                        userEmailText = snapshot.child("userEmail").getValue().toString();
                        email.setText(userEmailText);

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e){
                Toast.makeText(getActivity(), "No Such User", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.resetPassId) {
            Bundle armgs = new Bundle();
            String emailText = email.getText().toString();
            armgs.putString("email_key", emailText);

            ResetPassword resetPassword = new ResetPassword();
            resetPassword.setArguments(armgs);
            resetPassword.show(getActivity().getSupportFragmentManager(), "Sample dialog");
        }

        if(v.getId()==R.id.logoutId) {
            logoutApp();
        }
    }

    private void logoutApp(){
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Logout !");
        alertDialogBuilder.setMessage("Are you sure you want to logout ?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nullValue = "";
                setNullMethod(nullValue);

                getActivity().finish();
                getActivity().finishAffinity();
            }
        });

        alertDialogBuilder.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void setNullMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = getActivity().openFileOutput("Users_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();

            FileOutputStream fileOutputStream2 = getActivity().openFileOutput("Admin_Phone.txt", Context.MODE_PRIVATE);
            fileOutputStream2.write(passedString.getBytes());
            fileOutputStream2.close();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        fragment = new Dashboard();
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        backBtnListener = this;
    }

    @Override
    public void onPause() {
        backBtnListener = null;
        super.onPause();
    }
}
