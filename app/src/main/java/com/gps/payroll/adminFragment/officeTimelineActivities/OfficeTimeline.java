package com.gps.payroll.adminFragment.officeTimelineActivities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gps.payroll.backPageListener.BackListenerFragment;
import com.gps.payroll.splashAndDashboard.Dashboard;
import com.gps.payroll.splashAndDashboard.MainActivity;
import com.gps.payroll.R;

public class OfficeTimeline  extends Fragment implements BackListenerFragment, View.OnClickListener {

    private View views;
    private Fragment fragment;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private TextView totalEmployeesText;
    public static BackListenerFragment backBtnListener;
    private FragmentTransaction fragmentTransaction;
    private DatabaseReference databaseReference;
    private CardView backPage, workEnvironment, workHour, weekend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_office_timeline, container, false);

        totalEmployeesText = views.findViewById(R.id.totalEmployeesTextId);
        backPage = views.findViewById(R.id.backPageFloatingId);
        backPage.setOnClickListener(this);
        workEnvironment = views.findViewById(R.id.workEnvironmentId);
        workEnvironment.setOnClickListener(this);
        workHour = views.findViewById(R.id.workHourTextId);
        workHour.setOnClickListener(this);
        weekend = views.findViewById(R.id.weekendId);
        weekend.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Employee Info");

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getEmployeeList();

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return views;
    }

    private void getEmployeeList() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalEmployeesText.setText("Total " + snapshot.getChildrenCount() + "regular employees are working physically and remotely.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backPageFloatingId){
            ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new Dashboard();
            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.workEnvironmentId){
            WorkEnvironmentDialog workEnvironmentDialog = new WorkEnvironmentDialog();
            workEnvironmentDialog.show(getActivity().getSupportFragmentManager(), "Sample dialog");
        }

        if(v.getId()==R.id.workHourTextId){
            WorkHoursDialog workHoursDialog = new WorkHoursDialog();
            workHoursDialog.show(getActivity().getSupportFragmentManager(), "Sample dialog");
        }
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        fragment = new Dashboard();
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
        fragmentTransaction.commit();
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
