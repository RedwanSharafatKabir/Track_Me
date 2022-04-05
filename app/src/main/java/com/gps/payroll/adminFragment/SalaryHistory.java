package com.gps.payroll.adminFragment;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.gps.payroll.adapters.SalaryHistoryAdapter;
import com.gps.payroll.backPageListener.BackListenerFragment;
import com.gps.payroll.splashAndDashboard.Dashboard;
import com.gps.payroll.splashAndDashboard.MainActivity;
import com.gps.payroll.modelClasses.StoreSalaryHistory;
import com.gps.payroll.R;
import java.util.ArrayList;
import java.util.Objects;

public class SalaryHistory extends Fragment implements BackListenerFragment, View.OnClickListener {

    private View views;
    public static BackListenerFragment backBtnListener;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private CardView backPage;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<StoreSalaryHistory> storeSalaryHistoryArrayList = new ArrayList<>();
    private SalaryHistoryAdapter salaryHistoryAdapter;
    private DatabaseReference databaseReference;
    private Parcelable recyclerViewState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_salary_history, container, false);

        progressBar = views.findViewById(R.id.salaryHistoryProgressId);
        backPage = views.findViewById(R.id.backFromSalaryHistoryId);
        backPage.setOnClickListener(this);

        recyclerView = views.findViewById(R.id.salaryHistoryRecyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        salaryHistoryAdapter = new SalaryHistoryAdapter(getContext(), storeSalaryHistoryArrayList);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Payment History");

        cm = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getHistory();

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return views;
    }

    private void getHistory() {
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            try {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        storeSalaryHistoryArrayList.clear();

                        for (DataSnapshot item : snapshot.getChildren()) {
                            StoreSalaryHistory storeSalaryHistory = item.getValue(StoreSalaryHistory.class);
                            storeSalaryHistoryArrayList.add(storeSalaryHistory);
                        }

                        recyclerView.setAdapter(salaryHistoryAdapter);
                        salaryHistoryAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(recyclerViewState);

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                Log.i("Exception", e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getActivity(), "Turn on internet connection", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backFromSalaryHistoryId) {
            ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new Dashboard();
            fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
            fragmentTransaction.commit();
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
