package com.gps.payroll.profileAndAbout;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gps.payroll.R;
import java.util.Objects;

public class ResetPassword extends AppCompatDialogFragment implements View.OnClickListener {

    private View view;
    private NetworkInfo netInfo;
    private String userEmail;
    private ConnectivityManager cm;
    private ProgressBar progressBar;
    private EditText newPassEdit, currentPass, confirmNewPass;
    private TextView reset, no, getEmailFromProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.reset_password, null);
        builder.setView(view).setCancelable(false).setTitle("Reset password for");

        cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressBar = view.findViewById(R.id.progressBarRestPassId);
        progressBar.setVisibility(View.GONE);
        currentPass = view.findViewById(R.id.enterOldPasswordID);
        newPassEdit = view.findViewById(R.id.enterNewPasswordID);
        confirmNewPass = view.findViewById(R.id.retypeNewPasswordID);
        getEmailFromProfile = view.findViewById(R.id.getEmailFromProfileId);
        no = view.findViewById(R.id.notChangeId);
        no.setOnClickListener(this);
        reset = view.findViewById(R.id.submitResetPassId);
        reset.setOnClickListener(this);

        Bundle mArgs = getArguments();
        try {
            userEmail = Objects.requireNonNull(mArgs).getString("email_key");
        } catch (Exception e) {
            Log.i("Error_What", e.getMessage());
        }

        getEmailFromProfile.setText(userEmail + " ?");

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        String currentPassword = currentPass.getText().toString();
        String newPassword = newPassEdit.getText().toString();
        String retypeNewPassword = confirmNewPass.getText().toString();

        if (v.getId() == R.id.submitResetPassId) {
            progressBar.setVisibility(View.VISIBLE);
            cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = cm.getActiveNetworkInfo();
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            if (currentPassword.isEmpty()) {
                currentPass.setError("Enter current password");
                progressBar.setVisibility(View.GONE);
            }

            if(newPassword.isEmpty() || retypeNewPassword.isEmpty()){
                newPassEdit.setError("Enter new password");
                confirmNewPass.setError("Confirm new password");
                progressBar.setVisibility(View.GONE);
            }

            if (!newPassword.equals(retypeNewPassword)) {
                confirmNewPass.setError("Password did not match");
                progressBar.setVisibility(View.GONE);
            }

            if(newPassword.length() < 8 || retypeNewPassword.length() < 8) {
                newPassEdit.setError("Password must be minimum 8 characters");
                confirmNewPass.setError("Password must be minimum 8 characters");
                progressBar.setVisibility(View.GONE);
            }

            else {
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);

                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    getDialog().dismiss();

                                } else {
                                    Toast.makeText(getActivity(), "Password Did not Change", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

                        } else {
                            Toast.makeText(getActivity(), "Password Reset Failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                else {
                    Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }

        if (v.getId() == R.id.notChangeId) {
            Objects.requireNonNull(getDialog()).dismiss();
            progressBar.setVisibility(View.GONE);
        }
    }
}
