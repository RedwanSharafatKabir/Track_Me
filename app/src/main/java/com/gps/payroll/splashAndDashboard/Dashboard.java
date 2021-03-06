package com.gps.payroll.splashAndDashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
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
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gps.payroll.adminFragment.EmployeesList;
import com.gps.payroll.adminFragment.OnFieldEmployees;
import com.gps.payroll.adminFragment.officeTimelineActivities.OfficeTimeline;
import com.gps.payroll.adminFragment.SalaryHistory;
import com.gps.payroll.backPageListener.BackListenerFragment;
import com.gps.payroll.employeeFragment.CheckInDialog;
import com.gps.payroll.employeeFragment.CheckOutDialog;
import com.gps.payroll.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Dashboard extends Fragment implements BackListenerFragment, View.OnClickListener{

    private View views;
    private Fragment fragment;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private int PERMISSION_ID = 101;
    private DatabaseReference employeeReference;
    private FragmentTransaction fragmentTransaction;
    public static BackListenerFragment backBtnListener;
    private FusedLocationProviderClient mFusedLocationClient;
    private CardView checkIn, officeTimeline, employees, salaryHistory, onFieldEmployees;
    private String userRole, latitude = "", longitude = "", currentPlace = "", userPhone, dateNow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_dashboard, container, false);

        frameLayout = views.findViewById(R.id.checkinFrameId);
        progressBar = views.findViewById(R.id.dashboardProgressId);
        progressBar.setVisibility(View.GONE);
        checkIn = views.findViewById(R.id.checkInId);
        checkIn.setOnClickListener(this);
        officeTimeline = views.findViewById(R.id.officecTimelineId);
        officeTimeline.setOnClickListener(this);
        employees = views.findViewById(R.id.employeesId);
        employees.setOnClickListener(this);
        salaryHistory = views.findViewById(R.id.salaryHistoryId);
        salaryHistory.setOnClickListener(this);
        onFieldEmployees = views.findViewById(R.id.onFieldEmployeesId);
        onFieldEmployees.setOnClickListener(this);

        Date cal = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        dateNow = simpleDateFormat1.format(cal);

        gotUserMethod();

        try{
            if(userRole.equals("adminS")){
                officeTimeline.setVisibility(View.VISIBLE);
                employees.setVisibility(View.VISIBLE);
                salaryHistory.setVisibility(View.VISIBLE);
                onFieldEmployees.setVisibility(View.VISIBLE);

                frameLayout.setVisibility(View.GONE);

            } else if(userRole.equals("employeeS")){
                employees.setVisibility(View.GONE);
                salaryHistory.setVisibility(View.GONE);
                onFieldEmployees.setVisibility(View.GONE);

                officeTimeline.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
        }

        cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        employeeReference = FirebaseDatabase.getInstance().getReference("Employees List");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return views;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.checkInId){
            cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                checkEmployeeValidLocation();

            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_LONG).show();
            }
        }

        // Admin
        if(v.getId()==R.id.officecTimelineId){
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new OfficeTimeline();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.employeesId){
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new EmployeesList();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.onFieldEmployeesId){
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new OnFieldEmployees();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.salaryHistoryId){
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new SalaryHistory();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment);
            fragmentTransaction.commit();
        }
    }

    private void checkEmployeeValidLocation(){
        progressBar.setVisibility(View.VISIBLE);
        userPhone = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();

        try{
            employeeReference.child(dateNow).child(userPhone).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (userPhone.equals(Objects.requireNonNull(snapshot.child("userPhone").getValue()).toString())) {
                            progressBar.setVisibility(View.GONE);

                            CheckOutDialog checkOutDialog = new CheckOutDialog();
                            checkOutDialog.show(requireActivity().getSupportFragmentManager(), "Sample dialog");
                        }

                    } catch (Exception e){
                        progressBar.setVisibility(View.GONE);
                        try{
                            getLastLocation();
                        } catch (Exception error){
                            Log.i("Error", error.getMessage());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    try {
                        progressBar.setVisibility(View.GONE);
                    } catch (Exception e){
                        Log.i("Db_Error", e.getMessage());
                    }
                }
            });

        } catch (Exception e){
            progressBar.setVisibility(View.GONE);
            getLastLocation();
        }
    }

    private void gotUserMethod(){
        try {
            String recievedMessageTc;
            FileInputStream fileInputStreamTc = requireActivity().openFileInput("Users_Role.txt");
            InputStreamReader inputStreamReaderTc = new InputStreamReader(fileInputStreamTc);
            BufferedReader bufferedReaderTc = new BufferedReader(inputStreamReaderTc);
            StringBuilder stringBuilder = new StringBuilder();

            while((recievedMessageTc = bufferedReaderTc.readLine())!=null){
                stringBuilder.append(recievedMessageTc);
            }

            userRole = stringBuilder.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Dashboard myFragment = (Dashboard)requireActivity().getSupportFragmentManager().findFragmentByTag("EMPLOYEE_FRAGMENT");

        if (myFragment != null && myFragment.isVisible()) {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(requireActivity());
            alertDialogBuilder.setTitle("EXIT !");
            alertDialogBuilder.setMessage("Are you sure you want to close this app ?");
            alertDialogBuilder.setIcon(R.drawable.exit);
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                requireActivity().finish();
                requireActivity().finishAffinity();
            });

            alertDialogBuilder.setNeutralButton("No", (dialog, which) -> dialog.cancel());

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

    // Map Location
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        try {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(
                            task -> {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latitude = String.valueOf(location.getLatitude());
                                    longitude = String.valueOf(location.getLongitude());
                                    Log.wtf("lat", latitude);
                                    Log.wtf("lon", longitude);

                                    currentPlace = getCompleteAddressString(getActivity(), location.getLatitude(), location.getLongitude());

                                    Bundle armgs = new Bundle();
                                    armgs.putString("location_key", currentPlace);
                                    armgs.putString("latitude_key", latitude);
                                    armgs.putString("longitude_key", longitude);

                                    CheckInDialog checkInDialog = new CheckInDialog();
                                    checkInDialog.setArguments(armgs);
                                    checkInDialog.show(requireActivity().getSupportFragmentManager(), "Sample dialog");
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
        } catch (Exception e){
            Log.i("Error", e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
    };

    private boolean checkPermissions() {
        try {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        } catch (Exception e){
            Log.i("Error", e.getMessage());
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                StringBuilder strReturnedAddress = new StringBuilder();

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

}
