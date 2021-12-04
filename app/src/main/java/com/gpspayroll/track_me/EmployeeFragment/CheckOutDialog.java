package com.gpspayroll.track_me.EmployeeFragment;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CheckOutDialog extends AppCompatDialogFragment implements View.OnClickListener{

    private View view;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private DatabaseReference databaseReference;
    private String userPhone, timeNow, dateNow;
    private TextView userName, currentTime, confirmCheckOut, currentDate;

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

        userName = view.findViewById(R.id.checkOutUserNameId);
        currentTime = view.findViewById(R.id.checkOutTimeId);
        currentDate = view.findViewById(R.id.checkOutDateId);
        confirmCheckOut = view.findViewById(R.id.confirmCheckOutId);
        confirmCheckOut.setOnClickListener(this);

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
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUsername() {
        try{
            databaseReference.child(userPhone).child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        userName.setText(snapshot.getValue().toString());
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
