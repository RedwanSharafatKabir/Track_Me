package com.gps.payroll.adminFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gps.payroll.adapters.OnFieldEmployeeListAdapter;
import com.gps.payroll.backPageListener.BackListenerFragment;
import com.gps.payroll.splashAndDashboard.Dashboard;
import com.gps.payroll.splashAndDashboard.MainActivity;
import com.gps.payroll.map.MapActivity;
import com.gps.payroll.modelClasses.StoreEmployees;
import com.gps.payroll.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class OnFieldEmployees extends Fragment implements BackListenerFragment, View.OnClickListener {

    private String dateNow;
    public static BackListenerFragment backBtnListener;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private NetworkInfo netInfo;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private final ArrayList<StoreEmployees> storeEmployeesArrayList = new ArrayList<>();
    private OnFieldEmployeeListAdapter onFieldEmployeeListAdapter;
    private DatabaseReference databaseReference;
    private Parcelable recyclerViewState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_on_field_employee, container, false);

        Date cal = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        dateNow = simpleDateFormat1.format(cal);

        progressBar = views.findViewById(R.id.employeeListProgressId);
        CardView backPage = views.findViewById(R.id.backFromEmployeeListId);
        backPage.setOnClickListener(this);
        CardView employeeLocationOnMap = views.findViewById(R.id.seeEmployeeLocationId);
        employeeLocationOnMap.setOnClickListener(this);

        recyclerView = views.findViewById(R.id.employeesListRecyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        onFieldEmployeeListAdapter = new OnFieldEmployeeListAdapter(getContext(), storeEmployeesArrayList);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Employees List");

        ConnectivityManager cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getEmployeeList();

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return views;
    }

    private void getEmployeeList() {
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            try {
                databaseReference.child(dateNow).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        storeEmployeesArrayList.clear();
                        for (DataSnapshot items : snapshot.getChildren()) {
                            StoreEmployees storeEmployees = items.getValue(StoreEmployees.class);
                            storeEmployeesArrayList.add(storeEmployees);
                        }

                        recyclerView.setAdapter(onFieldEmployeeListAdapter);
                        onFieldEmployeeListAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recyclerViewState);

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e){
                Log.i("Exception", e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        }

        else {
            Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backFromEmployeeListId){
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new Dashboard();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
            fragmentTransaction.commit();
        }

        if(v.getId()==R.id.seeEmployeeLocationId){
            Intent intent = new Intent(getActivity(), MapActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        fragment = new Dashboard();
        fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
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
