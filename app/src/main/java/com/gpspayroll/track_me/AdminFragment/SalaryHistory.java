package com.gpspayroll.track_me.AdminFragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.gpspayroll.track_me.Adapters.SalaryHistoryAdapter;
import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.SplashAndDashboard.Dashboard;
import com.gpspayroll.track_me.SplashAndDashboard.MainActivity;
import com.gpspayroll.track_me.ModelClasses.StoreSalaryHistory;
import com.gpspayroll.track_me.R;

import java.util.ArrayList;

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
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Payment History");

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        storeSalaryHistoryArrayList.clear();

                        for (DataSnapshot item : snapshot.getChildren()) {
                            StoreSalaryHistory storeSalaryHistory = item.getValue(StoreSalaryHistory.class);
                            storeSalaryHistoryArrayList.add(storeSalaryHistory);
                        }

                        recyclerView.setAdapter(salaryHistoryAdapter);
                        salaryHistoryAdapter.notifyDataSetChanged();
                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

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

        refresh(1000);
    }

    private void refresh(int milliSecond) {
        final Handler handler = new Handler(Looper.getMainLooper());

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getHistory();
            }
        };

        handler.postDelayed(runnable, milliSecond);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backFromSalaryHistoryId) {
            ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

            fragment = new Dashboard();
            fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentID, fragment, "EMPLOYEE_FRAGMENT");
            fragmentTransaction.commit();
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
