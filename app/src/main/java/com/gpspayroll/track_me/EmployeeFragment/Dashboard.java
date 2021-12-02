package com.gpspayroll.track_me.EmployeeFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpspayroll.track_me.Authentication.LoginActivity;
import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.EmployeeActivity.MainActivity;
import com.gpspayroll.track_me.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Dashboard extends Fragment implements BackListenerFragment{

    public static BackListenerFragment backBtnListener;
    private View views;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return views;
    }

    @Override
    public void onBackPressed() {
        Dashboard myFragment = (Dashboard)getActivity().getSupportFragmentManager().findFragmentByTag("EMPLOYEE_FRAGMENT");

        if (myFragment != null && myFragment.isVisible()) {
            AlertDialog.Builder alertDialogBuilder;
            alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("EXIT !");
            alertDialogBuilder.setMessage("Are you sure you want to close this app ?");
            alertDialogBuilder.setIcon(R.drawable.exit);
            alertDialogBuilder.setCancelable(false);

            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nullValue = "";
                    setNullMethod(nullValue);

                    getActivity().finish();
                    getActivity().finishAffinity();
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

    private void setNullMethod(String passedString){
        try {
            FileOutputStream fileOutputStream = getActivity().openFileOutput("Users_Info.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(passedString.getBytes());
            fileOutputStream.close();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        backBtnListener = this;
    }

    @Override
    public void onPause() {
        backBtnListener = null;
        super.onPause();
    }
}
