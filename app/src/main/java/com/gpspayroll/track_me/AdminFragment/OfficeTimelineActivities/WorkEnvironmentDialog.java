package com.gpspayroll.track_me.AdminFragment.OfficeTimelineActivities;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WorkEnvironmentDialog extends AppCompatDialogFragment {

    private View view;
    private ImageView closeDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.work_environment_dialog, null);
        builder.setView(view).setCancelable(false);

        closeDialog = view.findViewById(R.id.closeWrkEnvId);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return builder.create();
    }
}
