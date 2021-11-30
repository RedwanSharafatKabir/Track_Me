package com.gpspayroll.track_me.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.AdminActivity.MainActivityAdmin;
import com.gpspayroll.track_me.EmployeeActivity.MainActivity;
import com.gpspayroll.track_me.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView signupPage, enterBtn;
    private CheckBox rememberPass;
    private View parentLayout;
    private ProgressDialog waitingDialog;
    private ConnectivityManager cm;
    private NetworkInfo netInfo;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText passwordText, phoneText;
    private String passedString = "I_User", getPassedString = "", email = "", userRole = "";
    private DatabaseReference employeeReference, adminReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        parentLayout = findViewById(android.R.id.content);
        waitingDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        rememberPass = findViewById(R.id.rememberPasswordCheckId);
        phoneText = findViewById(R.id.inputLoginPhoneId);
        passwordText = findViewById(R.id.inputLoginPassId);

        enterBtn = findViewById(R.id.signInUserId);
        enterBtn.setOnClickListener(this);
        signupPage = findViewById(R.id.signupPageID);
        signupPage.setOnClickListener(this);

        employeeReference = FirebaseDatabase.getInstance().getReference("Employee Info");
        adminReference = FirebaseDatabase.getInstance().getReference("Admin Info");
    }

    @Override
    protected void onStart() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        gotUserMethod();

        if (user != null && !getPassedString.isEmpty() && userRole.equals("adminS")) {
            finish();
            Intent it = new Intent(getApplicationContext(), MainActivityAdmin.class);
            startActivity(it);
        }

        if (user != null && !getPassedString.isEmpty() && userRole.equals("employeeS")) {
            finish();
            Intent it = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(it);
        }

        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.signupPageID){
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }

        if(v.getId()==R.id.signInUserId){
            String phone = phoneText.getText().toString();
            String password = passwordText.getText().toString();

            waitingDialog.setMessage("Signing in...");
            waitingDialog.show();

            if (phone.isEmpty()) {
                waitingDialog.dismiss();
                phoneText.setError("Please enter phone number");
                return;
            }

            if (password.isEmpty()) {
                waitingDialog.dismiss();
                passwordText.setError("Please enter password");
                return;
            }

            else {
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    // Access Employee Email
                    try {
                        employeeReference.child(phone).child("userEmail").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    email = snapshot.getValue().toString();

                                    if (rememberPass.isChecked()) {
                                        rememberMethod(passedString, "employeeS");

                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    waitingDialog.dismiss();

                                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(it);
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    finish();

                                                    phoneText.setText("");
                                                    passwordText.setText("");

                                                } else {
                                                    waitingDialog.dismiss();
                                                    Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                            task.getException().getMessage(), Toast.LENGTH_LONG);
                                                    t.setGravity(Gravity.CENTER, 0, 0);
                                                    t.show();
                                                }
                                            }
                                        });
                                    }

                                    if (!rememberPass.isChecked()) {
                                        passedString = "";
                                        setNullMethod(passedString);

                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    waitingDialog.dismiss();

                                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(it);
                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                    finish();

                                                    phoneText.setText("");
                                                    passwordText.setText("");

                                                } else {
                                                    waitingDialog.dismiss();
                                                    Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                            task.getException().getMessage(), Toast.LENGTH_LONG);
                                                    t.setGravity(Gravity.CENTER, 0, 0);
                                                    t.show();
                                                }
                                            }
                                        });
                                    }

                                } catch (Exception e) {

                                    // Access Admin Email

                                    try {
                                        adminReference.child(phone).child("userEmail").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try {
                                                    email = snapshot.getValue().toString();

                                                    if (rememberPass.isChecked()) {
                                                        rememberMethod(passedString, "adminS");

                                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    waitingDialog.dismiss();

                                                                    Intent it = new Intent(LoginActivity.this, MainActivityAdmin.class);
                                                                    startActivity(it);
                                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                    finish();

                                                                    phoneText.setText("");
                                                                    passwordText.setText("");

                                                                } else {
                                                                    waitingDialog.dismiss();
                                                                    Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                                            task.getException().getMessage(), Toast.LENGTH_LONG);
                                                                    t.setGravity(Gravity.CENTER, 0, 0);
                                                                    t.show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                    if (!rememberPass.isChecked()) {
                                                        passedString = "";
                                                        setNullMethod(passedString);

                                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    waitingDialog.dismiss();

                                                                    Intent it = new Intent(LoginActivity.this, MainActivityAdmin.class);
                                                                    startActivity(it);
                                                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                    finish();

                                                                    phoneText.setText("");
                                                                    passwordText.setText("");

                                                                } else {
                                                                    waitingDialog.dismiss();
                                                                    Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                                            task.getException().getMessage(), Toast.LENGTH_LONG);
                                                                    t.setGravity(Gravity.CENTER, 0, 0);
                                                                    t.show();
                                                                }
                                                            }
                                                        });
                                                    }

                                                } catch (Exception e) {
                                                    waitingDialog.dismiss();
                                                    Toast.makeText(LoginActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {}
                                        });

                                    } catch (Exception exception) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                    } catch (Exception e){
                        Log.i("DB_Error", e.getMessage());
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "wifi or mobile data connection lost", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                }
            }
        }
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

    private void setNullMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = openFileOutput("Users_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gotUserMethod(){
        try {
            String recievedMessageTc;
            FileInputStream fileInputStreamTc = openFileInput("Users_Info.txt");
            InputStreamReader inputStreamReaderTc = new InputStreamReader(fileInputStreamTc);
            BufferedReader bufferedReaderTc = new BufferedReader(inputStreamReaderTc);
            StringBuffer stringBufferTc = new StringBuffer();
            while((recievedMessageTc = bufferedReaderTc.readLine())!=null){
                stringBufferTc.append(recievedMessageTc);
            }
            getPassedString = stringBufferTc.toString();

            String recievedMessageTc2;
            FileInputStream fileInputStreamTc2 = openFileInput("Users_Role.txt");
            InputStreamReader inputStreamReaderTc2 = new InputStreamReader(fileInputStreamTc2);
            BufferedReader bufferedReaderTc2 = new BufferedReader(inputStreamReaderTc2);
            StringBuffer stringBufferTc2 = new StringBuffer();
            while((recievedMessageTc2 = bufferedReaderTc2.readLine())!=null){
                stringBufferTc2.append(recievedMessageTc2);
            }
            userRole = stringBufferTc2.toString();
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
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        alertDialogBuilder.setTitle("EXIT !");
        alertDialogBuilder.setMessage("Are you sure you want to close this app ?");
        alertDialogBuilder.setIcon(R.drawable.exit);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                LoginActivity.this.finishAffinity();
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
