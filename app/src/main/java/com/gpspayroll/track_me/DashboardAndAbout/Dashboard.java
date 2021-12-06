package com.gpspayroll.track_me.DashboardAndAbout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.AdminFragment.EmployeeSalary;
import com.gpspayroll.track_me.AdminFragment.OfficeTimeline;
import com.gpspayroll.track_me.Authentication.SignupActivity;
import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.EmployeeFragment.CheckInDialog;
import com.gpspayroll.track_me.EmployeeFragment.CheckOutDialog;
import com.gpspayroll.track_me.ModelClasses.OfficeLocationInfo;
import com.gpspayroll.track_me.ModelClasses.StoreEmployeeData;
import com.gpspayroll.track_me.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Dashboard extends Fragment implements BackListenerFragment, View.OnClickListener{

    private View views;
    private Fragment fragment;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private boolean checkInOut;
    private ProgressBar progressBar;
    private int PERMISSION_ID = 101;
    private FragmentTransaction fragmentTransaction;
    public static BackListenerFragment backBtnListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private DatabaseReference databaseReference, employeeReference;
    private CardView checkIn, checkOut, officeTimeline, employees, salaryHistory;
    private String userRole, latitude = "", longitude = "", currentPlace = "";
    private String adminPhone = "", temp = "", userPhone, dateNow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_dashboard, container, false);

        progressBar = views.findViewById(R.id.dashboardProgressId);
        checkIn = views.findViewById(R.id.checkInId);
        checkIn.setOnClickListener(this);
        checkOut = views.findViewById(R.id.checkOutId);
        checkOut.setOnClickListener(this);
        officeTimeline = views.findViewById(R.id.officecTimelineId);
        officeTimeline.setOnClickListener(this);
        employees = views.findViewById(R.id.employeesId);
        employees.setOnClickListener(this);
        salaryHistory = views.findViewById(R.id.salaryHistoryId);
        salaryHistory.setOnClickListener(this);

        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        dateNow = simpleDateFormat1.format(cal);

        gotUserMethod();

        try{
            if(userRole.equals("adminS")){
                officeTimeline.setVisibility(View.VISIBLE);
                employees.setVisibility(View.VISIBLE);
                salaryHistory.setVisibility(View.VISIBLE);

                checkIn.setVisibility(View.GONE);
                checkOut.setVisibility(View.GONE);

            } else if(userRole.equals("employeeS")){
                employees.setVisibility(View.GONE);
                salaryHistory.setVisibility(View.GONE);

                officeTimeline.setVisibility(View.VISIBLE);
                checkIn.setVisibility(View.VISIBLE);
                checkOut.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
        }

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        databaseReference = FirebaseDatabase.getInstance().getReference("Office Location");
        employeeReference = FirebaseDatabase.getInstance().getReference("Employees List");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            requestLocation();
//            getLastLocation();
            checkEmployeeValidLocation();

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return views;
    }

    // Provided by Akhter Vai
/*
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = String.valueOf(location.getLatitude());
                                    longitude = String.valueOf(location.getLongitude());
                                    Log.wtf("lat", latitude);
                                    Log.wtf("lon", longitude);

                                    getAdminPhone();
                                    currentPlace = getCompleteAddressString(getActivity(), location.getLatitude(), location.getLongitude());
                                    storeOfficeLocation(adminPhone, latitude, longitude, currentPlace);
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getActivity(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    public static String getCompleteAddressString(Context ctx, double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }

                strAdd = strReturnedAddress.toString();

                Log.wtf("My Current location address", strReturnedAddress.toString());

            } else {
                Log.wtf("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("My Current location address", "Cannot get Address!");
        }

        return strAdd;
    }
*/

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

            getCurrentLocation();

        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestLocation();
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null) {
            try {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addressList;

            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                currentPlace = addressList.get(0).getAddressLine(0);

                getAdminPhone();
                storeOfficeLocation(adminPhone, latitude, longitude, currentPlace);

            } catch (IOException e) {
                Log.i("ERROR ", "Permission Denied");
            }
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.checkInId){
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                if(checkInOut == true){
                    Toast.makeText(getActivity(), "You Have Already Checked-in", Toast.LENGTH_LONG).show();
                } else if(checkInOut == false){
                    Bundle armgs = new Bundle();
                    armgs.putString("location_key", currentPlace);

                    CheckInDialog checkInDialog = new CheckInDialog();
                    checkInDialog.setArguments(armgs);
                    checkInDialog.show(getActivity().getSupportFragmentManager(), "Sample dialog");
                }
            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        if(v.getId()==R.id.checkOutId){
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                if(checkInOut == true){
                    Bundle armgs = new Bundle();
                    armgs.putString("location_key", currentPlace);

                    CheckOutDialog checkOutDialog = new CheckOutDialog();
                    checkOutDialog.setArguments(armgs);
                    checkOutDialog.show(getActivity().getSupportFragmentManager(), "Sample dialog");
                } else if(checkInOut == false){
                    Toast.makeText(getActivity(), "You Have to Check-in First", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_LONG).show();
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

        if(v.getId()==R.id.salaryHistoryId){
            ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

//            fragment = new SalaryHistory();
            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }
    }

    private void checkEmployeeValidLocation(){
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            try {
                                if(currentPlace.equals(item.child("placeName").getValue().toString())){
                                    temp = item.child("placeName").getValue().toString();

                                    if(currentPlace.equals(temp)){
                                        confirmEmployeeStatus();
                                    }
                                }

                            } catch (Exception e) {
                                Log.i("Exception", e.getMessage());
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception e){
                        Log.i("Exception", e.getMessage());
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
            progressBar.setVisibility(View.GONE);
        }
    }

    private void confirmEmployeeStatus(){
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        try{
            employeeReference.child(dateNow).child(userPhone).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (userPhone.equals(snapshot.child("userPhone").getValue().toString())) {
                            checkInOut = true;
                            progressBar.setVisibility(View.GONE);
                        }

                    } catch (Exception e){
                        checkInOut = false;
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        } catch (Exception e){
            checkInOut = false;
            progressBar.setVisibility(View.GONE);
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

    private void storeOfficeLocation(String phoneNumber, String latitude, String longitude, String placeName){
        OfficeLocationInfo officeLocationInfo = new OfficeLocationInfo(latitude, longitude, placeName);
        databaseReference.child(phoneNumber).setValue(officeLocationInfo);
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

            adminPhone = stringBufferTc.toString();
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
