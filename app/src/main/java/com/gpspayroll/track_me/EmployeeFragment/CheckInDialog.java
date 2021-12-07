package com.gpspayroll.track_me.EmployeeFragment;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.gpspayroll.track_me.ModelClasses.OfficeLocationInfo;
import com.gpspayroll.track_me.ModelClasses.StoreEmployees;
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckInDialog extends AppCompatDialogFragment implements View.OnClickListener{

    private View view;
    private NetworkInfo netInfo;
    private ImageView closeDialog;
    private ConnectivityManager cm;
    private DatabaseReference databaseReference, employeeReference;
    private TextView userName, currentDate, currentTime, confirmCheckIn;
    private String userPhone, timeNow, dateNow, currentLocation="";
    private String username, checkin, checkout, workhour, remuneration;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.dialog_check_in, null);
        builder.setView(view).setCancelable(false);

        Bundle mArgs = getArguments();
        currentLocation = mArgs.getString("location_key");

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        userPhone = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");
        employeeReference = FirebaseDatabase.getInstance().getReference("Employees List");

        userName = view.findViewById(R.id.checkInUserNameId);
        currentTime = view.findViewById(R.id.checkInTimeId);
        currentDate = view.findViewById(R.id.checkInDateId);
        confirmCheckIn = view.findViewById(R.id.confirmCheckInId);
        confirmCheckIn.setOnClickListener(this);
        closeDialog = view.findViewById(R.id.closeDialogInId);
        closeDialog.setOnClickListener(this);

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getUsername();

            Date cal = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
            dateNow = simpleDateFormat1.format(cal);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm:ss aaa");
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
        if(v.getId()==R.id.confirmCheckInId) {
            cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                checkin = timeNow;
                checkout = "Counting";
                workhour = "Counting";
                remuneration = "Counting";

                storeWorkStatus(username, checkin, checkout, workhour, remuneration, userPhone, currentLocation);

            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        if(v.getId()==R.id.closeDialogInId){
            getDialog().dismiss();
        }
    }

    private void storeWorkStatus(String username, String checkin, String checkout, String workhour,
                                 String remuneration, String userPhone, String employeeLocation) {

        StoreEmployees storeEmployees = new StoreEmployees(username, checkin, checkout, workhour,
                remuneration, userPhone, employeeLocation);

        employeeReference.child(dateNow).child(userPhone).setValue(storeEmployees);

        Toast.makeText(getActivity(), "Checked-in Successfully", Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
    }

    private void getUsername() {
        try{
            databaseReference.child(userPhone).child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        username = snapshot.getValue().toString();
                        userName.setText(username);

                    } catch (Exception e){
                        Toast.makeText(getActivity(), "User Does Not Exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

        } catch (Exception e){
            Toast.makeText(getActivity(), "No Data of This User", Toast.LENGTH_SHORT).show();
        }
    }
}
