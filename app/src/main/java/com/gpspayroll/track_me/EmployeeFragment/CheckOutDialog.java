package com.gpspayroll.track_me.EmployeeFragment;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.ModelClasses.StoreEmployees;
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckOutDialog extends AppCompatDialogFragment implements View.OnClickListener {

    private View view;
    private int PER_HOUR_SALARY = 60;
    private ImageView closeDialog;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference, employeeStatusReference;
    private TextView userName, currentTime, confirmCheckOut, currentDate;
    private String userPhone, timeNow, dateNow, checkin="", workhour="";
    private String employeeLocation="", remuneration="", username="", lattitude="", longitude="";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_check_out, null);
        builder.setView(view).setCancelable(false);

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");
        employeeStatusReference = FirebaseDatabase.getInstance().getReference("Employees List");

        progressBar = view.findViewById(R.id.checkOutProgressId);
        progressBar.setVisibility(View.GONE);
        userName = view.findViewById(R.id.checkOutUserNameId);
        currentTime = view.findViewById(R.id.checkOutTimeId);
        currentDate = view.findViewById(R.id.checkOutDateId);
        confirmCheckOut = view.findViewById(R.id.confirmCheckOutId);
        confirmCheckOut.setOnClickListener(this);
        closeDialog = view.findViewById(R.id.closeDialogOutId);
        closeDialog.setOnClickListener(this);

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getUsername();

            Date cal = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
            dateNow = simpleDateFormat1.format(cal);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm aaa");
            timeNow = simpleDateFormat2.format(new Date());

            currentDate.setText(dateNow);
            currentTime.setText("at " + timeNow);

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.confirmCheckOutId){
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                checkOutStatus();
            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        if(v.getId()==R.id.closeDialogOutId){
            getDialog().dismiss();
        }
    }

    private void checkOutStatus() {
        // If checkOut time value in database is "Counting" then user will be checked out
        // Other wise toast a message "You already left your office"
        progressBar.setVisibility(View.VISIBLE);
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        try{
            employeeStatusReference.child(dateNow).child(userPhone).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (userPhone.equals(snapshot.child("userPhone").getValue().toString())) {
                            if(snapshot.child("checkout").getValue().toString().equals("Counting")) {
                                username = snapshot.child("username").getValue().toString();
                                checkin = snapshot.child("checkin").getValue().toString();
                                employeeLocation = snapshot.child("employeeLocation").getValue().toString();
                                lattitude = snapshot.child("lattitude").getValue().toString();
                                longitude = snapshot.child("longitude").getValue().toString();

                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
                                timeNow = simpleDateFormat.format(new Date());

                                Date checkInTime = simpleDateFormat.parse(checkin);
                                Date checkOutTime = simpleDateFormat.parse(timeNow);

                                // Calculate Work Hours
                                long difference = checkOutTime.getTime() - checkInTime.getTime();
                                int days = (int) (difference / (1000*60*60*24));
                                int hours = (int) ((difference - (1000*60*60*24*days)) / (1000*60*60));
                                hours = (hours < 0 ? -hours : hours);

                                workhour = String.valueOf(hours);
                                remuneration = String.valueOf(PER_HOUR_SALARY*hours);

                                storeUpdatedEmployeeStatusData(username, checkin, timeNow, workhour, remuneration,
                                        userPhone, employeeLocation, lattitude, longitude);

                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "You Have Already Checked-Out", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e){
                        progressBar.setVisibility(View.GONE);
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
            Toast.makeText(getActivity(), "Check-Out Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeUpdatedEmployeeStatusData(String username, String checkin, String checkout, String workhour,
                                                String remuneration, String userPhone, String employeeLocation, String lattitude, String longitude) {

        StoreEmployees storeEmployees = new StoreEmployees(username, checkin, checkout, workhour, remuneration,
                userPhone, employeeLocation, lattitude, longitude);

        employeeStatusReference.child(dateNow).child(userPhone).setValue(storeEmployees);

        progressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "Successfully Checked-Out from Office", Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
    }

    private void getUsername() {
        try{
            databaseReference.child(userPhone).child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        userName.setText(snapshot.getValue().toString());
                    } catch (Exception e){
                        try {
                            Toast.makeText(getActivity(), "User Does Not Exist", Toast.LENGTH_SHORT).show();
                        } catch (Exception exception){
                            Log.i("Exception", exception.getMessage());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        } catch (Exception e){
            try {
                Toast.makeText(getActivity(), "No Data of This User", Toast.LENGTH_SHORT).show();
            } catch (Exception exception){
                Log.i("Exception", exception.getMessage());
            }
        }
    }
}
