package com.gps.payroll.adminFragment.officeTimelineActivities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.gps.payroll.R;

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
