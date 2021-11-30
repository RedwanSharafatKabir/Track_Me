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
import com.gpspayroll.track_me.MainActivity;
import com.gpspayroll.track_me.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView signupPage, enterBtn;
    CheckBox rememberPass;
    View parentLayout;
    ProgressDialog waitingDialog;
    ConnectivityManager cm;
    NetworkInfo netInfo;
    FirebaseAuth mAuth;
    FirebaseUser user;
    EditText emailText, passwordText;
    String passedString="I_User", getPassedString="";

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
        emailText = findViewById(R.id.inputLoginEmailId);
        passwordText = findViewById(R.id.inputLoginPassId);

        enterBtn = findViewById(R.id.signInUserId);
        enterBtn.setOnClickListener(this);
        signupPage = findViewById(R.id.signupPageID);
        signupPage.setOnClickListener(this);
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
            final String email = emailText.getText().toString();
            final String password = passwordText.getText().toString();

            waitingDialog.setMessage("Signing in...");
            waitingDialog.show();

            if (email.isEmpty()) {
                waitingDialog.dismiss();
                emailText.setError("Please enter email address");
                return;
            }

            if (password.isEmpty()) {
                waitingDialog.dismiss();
                passwordText.setError("Please enter password");
                return;
            }

            else {
                if (rememberPass.isChecked()) {
                    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                        rememberMethod(passedString);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    waitingDialog.dismiss();

                                    finish();
                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(it);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    emailText.setText("");
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

                    } else {
                        Toast.makeText(getApplicationContext(), "wifi or mobile data connection lost", Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
                    }
                }

                if (!rememberPass.isChecked()) {
                    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                        passedString = "";
                        setNullMethod(passedString);

                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    waitingDialog.dismiss();

                                    finish();
                                    Intent it = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(it);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                    emailText.setText("");
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

                    } else {
                        Toast.makeText(getApplicationContext(), "wifi or mobile data connection lost", Toast.LENGTH_SHORT).show();
                        waitingDialog.dismiss();
                    }
                }
            }
        }
    }

    private void rememberMethod(String passedString){
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

            while((recievedMessageTc=bufferedReaderTc.readLine())!=null){
                stringBufferTc.append(recievedMessageTc);
            }

            getPassedString = stringBufferTc.toString();
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
