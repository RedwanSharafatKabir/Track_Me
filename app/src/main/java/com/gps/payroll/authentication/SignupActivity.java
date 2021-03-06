package com.gps.payroll.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gps.payroll.modelClasses.StoreEmployeeData;
import com.gps.payroll.R;
import com.gps.payroll.splashAndDashboard.MainActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private View parentLayout;
    private ImageView backPage;
    private ProgressDialog waitingDialog;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private FirebaseAuth mAuth;
    private TextView loginPage, signUp;
    private DatabaseReference databaseReference;
    private EditText signupEmailText, signupUsernameText, signupPasswordText, signupPhoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        parentLayout = findViewById(android.R.id.content);
        waitingDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");

        signupEmailText = findViewById(R.id.inputSignupEmailId);
        signupUsernameText = findViewById(R.id.inputSignupUsernameId);
        signupPasswordText = findViewById(R.id.inputSignupPassId);
        signupPhoneText = findViewById(R.id.inputSignupPhoneId);

        signUp = findViewById(R.id.signUpUserId);
        signUp.setOnClickListener(this);
        backPage = findViewById(R.id.backPageId);
        backPage.setOnClickListener(this);
        loginPage = findViewById(R.id.loginPageID);
        loginPage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.loginPageID){
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }

        if(view.getId()==R.id.backPageId){
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }

        if(view.getId()==R.id.signUpUserId){
            final String email = signupEmailText.getText().toString();
            final String phone = signupPhoneText.getText().toString();
            final String username = signupUsernameText.getText().toString();
            final String password = signupPasswordText.getText().toString();

            waitingDialog.setMessage("Signing up...");
            waitingDialog.show();

            if (email.isEmpty()) {
                waitingDialog.dismiss();
                signupEmailText.setError("Please enter email address");
                return;
            }

            if (username.isEmpty()) {
                waitingDialog.dismiss();
                signupUsernameText.setError("Please enter username");
                return;
            }

            if (phone.isEmpty()) {
                waitingDialog.dismiss();
                signupPhoneText.setError("Please enter your contact number");
                return;
            }

            if (password.isEmpty()) {
                waitingDialog.dismiss();
                signupPasswordText.setError("Please enter password");
                return;
            }

            if (password.length() < 8) {
                waitingDialog.dismiss();
                signupPasswordText.setError("Password must be at least 8 characters");
                return;
            }

            if((phone.length() < 11) || phone.length() > 11) {
                waitingDialog.dismiss();
                signupPhoneText.setError("Invalid phone number");
            }

            else {
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    signupWithEmail(email, username, phone, password);

                } else {
                    waitingDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(parentLayout, "Turn on internet connection", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.Red));
                    snackbar.setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE);
                    snackbar.setDuration(10000).show();
                }
            }
        }
    }

    private void signupWithEmail(String email, String username, String phone, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    waitingDialog.dismiss();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                rememberMethod("I_User", "employeeS");
                                storeDataMethod(email, username, phone);

                                signupEmailText.setText("");
                                signupUsernameText.setText("");
                                signupPhoneText.setText("");
                                signupPasswordText.setText("");

                                finish();
                                Intent it = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(it);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            } else {
                                waitingDialog.dismiss();
                                Log.i("Signup_Exception", task.getException().getMessage());
                                Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                        task.getException().getMessage(), Toast.LENGTH_LONG);
                                t.setGravity(Gravity.CENTER, 0, 0);
                                t.show();
                            }
                        }
                    });

                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        waitingDialog.dismiss();
                        Toast t = Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();

                    } else {
                        waitingDialog.dismiss();
                        Log.i("Signup_Exception", task.getException().getMessage());
                        Toast t = Toast.makeText(getApplicationContext(), "Authentication failed. Error : "
                                + "Connection lost.", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                    }
                }
            }
        });
    }

    private void rememberMethod(String passedString, String userRole){
        try {
            FileOutputStream fileOutputStream = openFileOutput("Users_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();

            FileOutputStream fileOutputStream2 = openFileOutput("Users_Role.txt", Context.MODE_PRIVATE);
            fileOutputStream2.write(userRole.getBytes());
            fileOutputStream2.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeDataMethod(String email, String username, String phone){
        String displayname = phone;
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            UserProfileChangeRequest profile;
            profile = new UserProfileChangeRequest.Builder().setDisplayName(displayname).build();
            user.updateProfile(profile).addOnCompleteListener(task -> {});
        }

        String sampleAddress="Update Now", sampleNid="Update Now";

        StoreEmployeeData storeEmployeeData = new StoreEmployeeData(username, phone, email, sampleNid, sampleAddress);
        databaseReference.child(phone).setValue(storeEmployeeData);

        Toast.makeText(SignupActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}
