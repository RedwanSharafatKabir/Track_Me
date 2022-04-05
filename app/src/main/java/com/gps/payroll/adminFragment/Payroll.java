package com.gps.payroll.adminFragment;

import android.annotation.SuppressLint;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.payroll.modelClasses.StoreSalaryHistory;
import com.gps.payroll.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Payroll extends AppCompatDialogFragment implements View.OnClickListener{

    private ProgressBar progressBar;
    private DatabaseReference databaseReference, salaryHistory;
    private TextView payrollDetails, confirmPayroll;
    private String dateNow, userPhone, checkin="", workhour="", remuneration="", username="", checkout="", status="";

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.payroll, null);
        builder.setView(view).setCancelable(false);

        Bundle mArgs = getArguments();
        userPhone = Objects.requireNonNull(mArgs).getString("phone_key");
        username = mArgs.getString("name_key");
        workhour = mArgs.getString("hours_key");
        remuneration = mArgs.getString("salary_key");
        checkin = mArgs.getString("checkIn_key");
        checkout = mArgs.getString("checkOut_key");

        databaseReference = FirebaseDatabase.getInstance().getReference("Employees List");
        salaryHistory = FirebaseDatabase.getInstance().getReference("Payment History");

        progressBar = view.findViewById(R.id.payrollProgressId);
        progressBar.setVisibility(View.GONE);

        payrollDetails = view.findViewById(R.id.payrollDetailsId);
        confirmPayroll = view.findViewById(R.id.confirmPayrollId);
        confirmPayroll.setOnClickListener(this);
        ImageView closeDialog = view.findViewById(R.id.closeDialogPayrollId);
        closeDialog.setOnClickListener(this);

        payrollDetails.setText("Pay " + username + ",\n" + remuneration + " BDT For " + workhour + " Hours");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.confirmPayrollId){
            ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                payrollMethod();

            } else {
                Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }

        if(v.getId()==R.id.closeDialogPayrollId){
            Objects.requireNonNull(getDialog()).dismiss();
        }
    }

    private void payrollMethod() {
        status = "Paid";
        storeHistory(userPhone, username, workhour, remuneration, checkin, checkout, status);
    }

    private void storeHistory(String userPhone, String username, String workhour, String remuneration, String checkin, String checkout, String status) {
        StoreSalaryHistory storeSalaryHistory = new StoreSalaryHistory(userPhone, username, workhour, remuneration, checkin, checkout, status);
        salaryHistory.child(userPhone).setValue(storeSalaryHistory);

        Date cal = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        dateNow = simpleDateFormat1.format(cal);

        try {
            databaseReference.child(dateNow).child(userPhone).removeValue();
        } catch (Exception e){
            Log.i("Error", e.getMessage());
        }

        Toast.makeText(getActivity(), "Successfully Paid Salary", Toast.LENGTH_SHORT).show();
        Objects.requireNonNull(getDialog()).dismiss();
    }
}
