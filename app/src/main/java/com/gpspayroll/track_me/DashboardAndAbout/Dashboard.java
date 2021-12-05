package com.gpspayroll.track_me.DashboardAndAbout;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.gpspayroll.track_me.AdminFragment.EmployeeSalary;
import com.gpspayroll.track_me.AdminFragment.OfficeTimeline;
import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.EmployeeFragment.CheckInDialog;
import com.gpspayroll.track_me.EmployeeFragment.CheckOutDialog;
import com.gpspayroll.track_me.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Dashboard extends Fragment implements BackListenerFragment, View.OnClickListener{

    private View views;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    public static BackListenerFragment backBtnListener;
    private CardView checkIn, checkOut, officeTimeline, employees;
    private String userRole;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_dashboard, container, false);

        checkIn = views.findViewById(R.id.checkInId);
        checkIn.setOnClickListener(this);
        checkOut = views.findViewById(R.id.checkOutId);
        checkOut.setOnClickListener(this);
        officeTimeline = views.findViewById(R.id.officecTimelineId);
        officeTimeline.setOnClickListener(this);
        employees = views.findViewById(R.id.employeesId);
        employees.setOnClickListener(this);

        gotUserMethod();

        try{
            if(userRole.equals("adminS")){
                officeTimeline.setVisibility(View.VISIBLE);
                employees.setVisibility(View.VISIBLE);

                checkIn.setVisibility(View.GONE);
                checkOut.setVisibility(View.GONE);

            } else if(userRole.equals("employeeS")){
                employees.setVisibility(View.GONE);

                officeTimeline.setVisibility(View.VISIBLE);
                checkIn.setVisibility(View.VISIBLE);
                checkOut.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
        }

        return views;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.checkInId){
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                CheckInDialog checkInDialog = new CheckInDialog();
                checkInDialog.show(getActivity().getSupportFragmentManager(), "Sample dialog");

            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        if(v.getId()==R.id.checkOutId){
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                CheckOutDialog checkOutDialog = new CheckOutDialog();
                checkOutDialog.show(getActivity().getSupportFragmentManager(), "Sample dialog");

            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        // Admin
        if(v.getId()==R.id.officecTimelineId){
            ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new OfficeTimeline();
            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.employeesId){
            ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new EmployeeSalary();
            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }
    }

    private void gotUserMethod(){
        try {
            String recievedMessageTc;
            FileInputStream fileInputStreamTc = getActivity().openFileInput("Users_Role.txt");
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
    public void onBackPressed() {
        Dashboard myFragment = (Dashboard)getActivity().getSupportFragmentManager().findFragmentByTag("EMPLOYEE_FRAGMENT");

        if (myFragment != null && myFragment.isVisible()) {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("EXIT !");
            alertDialogBuilder.setMessage("Are you sure you want to close this app ?");
            alertDialogBuilder.setIcon(R.drawable.exit);
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
