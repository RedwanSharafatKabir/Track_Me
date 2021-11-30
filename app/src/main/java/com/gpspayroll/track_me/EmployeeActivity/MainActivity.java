package com.gpspayroll.track_me.EmployeeActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.gpspayroll.track_me.AdminActivity.MainActivityAdmin;
import com.gpspayroll.track_me.Authentication.LoginActivity;
import com.gpspayroll.track_me.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String passedString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("EXIT !");
        alertDialogBuilder.setMessage("Are you sure you want to close this app ?");
        alertDialogBuilder.setIcon(R.drawable.exit);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setNullMethod(passedString);
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

    private void setNullMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = openFileOutput("Users_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
