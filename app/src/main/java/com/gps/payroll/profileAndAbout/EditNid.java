package com.gps.payroll.profileAndAbout;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.payroll.modelClasses.StoreEmployeeData;
import com.gps.payroll.R;

import java.util.Objects;

public class EditNid extends AppCompatDialogFragment implements View.OnClickListener {

    private View view;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private ProgressBar progressBar;
    private EditText nidEdit;
    private TextView submit, no;
    private DatabaseReference databaseReference;
    private String username, userPhone, userEmail, userNid, userAddress;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.edit_nid, null);
        builder.setView(view).setCancelable(false).setTitle("Update NID number");

        cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        progressBar = view.findViewById(R.id.progressBarNidId);
        progressBar.setVisibility(View.GONE);
        nidEdit = view.findViewById(R.id.enterNidId);

        no = view.findViewById(R.id.notChangeNidId);
        no.setOnClickListener(this);
        submit = view.findViewById(R.id.submitNidId);
        submit.setOnClickListener(this);

        Bundle mArgs = getArguments();
        try {
            userEmail = mArgs.getString("email_key");
            userNid = mArgs.getString("nid_key");
            userAddress = mArgs.getString("address_key");
            userPhone = mArgs.getString("phone_key");
            username = mArgs.getString("username_key");

            if(!userNid.equals("Update Now")){
                nidEdit.setText(userNid);
            }

        } catch (Exception e) {
            Log.i("Error_What", e.getMessage());
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String nidStr = nidEdit.getText().toString();

        if (v.getId() == R.id.submitNidId) {
            progressBar.setVisibility(View.VISIBLE);
            cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();

            if (nidStr.isEmpty()) {
                nidEdit.setError("Enter nid number");
                progressBar.setVisibility(View.GONE);
            }

            else {
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    storeDataMethod(username, userPhone, userEmail, nidStr, userAddress);
                }

                else {
                    Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }

        if (v.getId() == R.id.notChangeNidId) {
            Objects.requireNonNull(getDialog()).dismiss();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void storeDataMethod(String username, String userPhone, String userEmail, String userNid, String userAddress){
        StoreEmployeeData storeEmployeeData = new StoreEmployeeData(username, userPhone, userEmail, userNid, userAddress);
        databaseReference.child(userPhone).setValue(storeEmployeeData);

        progressBar.setVisibility(View.GONE);
        Objects.requireNonNull(getDialog()).dismiss();

        Toast.makeText(getActivity(), "Successfully Updated", Toast.LENGTH_SHORT).show();
    }
}
