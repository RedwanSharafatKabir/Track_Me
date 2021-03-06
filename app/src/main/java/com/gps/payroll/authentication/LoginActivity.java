package com.gps.payroll.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gps.payroll.splashAndDashboard.MainActivity;
import com.gps.payroll.R;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Objects;

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
        rememberPass.setChecked(true);
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

        if (user != null && !getPassedString.isEmpty()) {
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
            }

            if (password.isEmpty()) {
                waitingDialog.dismiss();
                passwordText.setError("Please enter password");
            }

            else {
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    // Access Employee Email

                    try {
                        employeeReference.child(phone).child("userEmail").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    email = Objects.requireNonNull(snapshot.getValue()).toString();

                                    if (rememberPass.isChecked()) {
                                        rememberMethod(passedString, "employeeS");

                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                waitingDialog.dismiss();

                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                it.putExtra("messageRole", "employeeS");
                                                startActivity(it);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                finish();

                                                phoneText.setText("");
                                                passwordText.setText("");

                                            } else {
                                                waitingDialog.dismiss();
                                                Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG);
                                                t.setGravity(Gravity.CENTER, 0, 0);
                                                t.show();
                                            }
                                        });
                                    }

                                    if (!rememberPass.isChecked()) {
                                        rememberMethod("", "employeeS");

                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                waitingDialog.dismiss();

                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                it.putExtra("messageRole", "employeeS");
                                                startActivity(it);
                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                finish();

                                                phoneText.setText("");
                                                passwordText.setText("");

                                            } else {
                                                waitingDialog.dismiss();
                                                Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG);
                                                t.setGravity(Gravity.CENTER, 0, 0);
                                                t.show();
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
                                                    email = Objects.requireNonNull(snapshot.getValue()).toString();

                                                    if (rememberPass.isChecked()) {
                                                        rememberMethod(passedString, "adminS");
                                                        rememberAdminPhone(phone);

                                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                waitingDialog.dismiss();

                                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                                it.putExtra("messageRole", "adminS");
                                                                startActivity(it);
                                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                finish();

                                                                phoneText.setText("");
                                                                passwordText.setText("");

                                                            } else {
                                                                waitingDialog.dismiss();
                                                                Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG);
                                                                t.setGravity(Gravity.CENTER, 0, 0);
                                                                t.show();
                                                            }
                                                        });
                                                    }

                                                    if (!rememberPass.isChecked()) {
                                                        rememberMethod("", "adminS");
                                                        rememberAdminPhone(phone);

                                                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                waitingDialog.dismiss();

                                                                Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                                                it.putExtra("messageRole", "adminS");
                                                                startActivity(it);
                                                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                                                finish();

                                                                phoneText.setText("");
                                                                passwordText.setText("");

                                                            } else {
                                                                waitingDialog.dismiss();
                                                                Toast t = Toast.makeText(getApplicationContext(), "Authentication failed\nError : " +
                                                                        Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG);
                                                                t.setGravity(Gravity.CENTER, 0, 0);
                                                                t.show();
                                                            }
                                                        });
                                                    }

                                                } catch (Exception e) {
                                                    waitingDialog.dismiss();
                                                    Toast.makeText(LoginActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                waitingDialog.dismiss();
                                                Log.i("DB_Error", error.getMessage());
                                            }
                                        });

                                    } catch (Exception exception) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(LoginActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                                        Log.i("DB_Error", exception.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                waitingDialog.dismiss();
                                Log.i("DB_Error", error.getMessage());
                            }
                        });

                    } catch (Exception e){
                        Log.i("DB_Error", e.getMessage());
                        waitingDialog.dismiss();
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
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void rememberAdminPhone(String phone){
        try {
            FileOutputStream fileOutputStream = openFileOutput("Admin_Phone.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(phone.getBytes());
            fileOutputStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotUserMethod(){
        try {
            String recievedMessageTc;
            FileInputStream fileInputStreamTc = openFileInput("Users_Info.txt");
            InputStreamReader inputStreamReaderTc = new InputStreamReader(fileInputStreamTc);
            BufferedReader bufferedReaderTc = new BufferedReader(inputStreamReaderTc);
            StringBuilder stringBuilder = new StringBuilder();
            while((recievedMessageTc = bufferedReaderTc.readLine())!=null){
                stringBuilder.append(recievedMessageTc);
            }
            getPassedString = stringBuilder.toString();

            String recievedMessageTc2;
            FileInputStream fileInputStreamTc2 = openFileInput("Users_Role.txt");
            InputStreamReader inputStreamReaderTc2 = new InputStreamReader(fileInputStreamTc2);
            BufferedReader bufferedReaderTc2 = new BufferedReader(inputStreamReaderTc2);
            StringBuilder stringBuilder2 = new StringBuilder();
            while((recievedMessageTc2 = bufferedReaderTc2.readLine())!=null){
                stringBuilder2.append(recievedMessageTc2);
            }
            userRole = stringBuilder2.toString();
        }
        catch (Exception e) {
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

        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            finish();
            LoginActivity.this.finishAffinity();
        });

        alertDialogBuilder.setNeutralButton("No", (dialog, which) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
