package com.gpspayroll.track_me.AdminFragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.DashboardAndAbout.Dashboard;
import com.gpspayroll.track_me.DashboardAndAbout.MainActivity;
import com.gpspayroll.track_me.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OfficeTimeline  extends Fragment implements BackListenerFragment, View.OnClickListener {

    private View views;
    private TextView curentLocation;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    public static BackListenerFragment backBtnListener;
    private DatabaseReference databaseReference;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private CardView backPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_office_timeline, container, false);

        curentLocation = views.findViewById(R.id.officeLocationId);
        backPage = views.findViewById(R.id.backPageFloatingId);
        backPage.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Office Location");
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getOfficeLocation();

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return views;
    }

    private void getOfficeLocation() {
        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot item : snapshot.getChildren()) {
                            try {
                                curentLocation.setText(item.child("placeName").getValue().toString());
                            } catch (Exception e) {
                                Log.i("Exception", e.getMessage());
                            }
                        }
                    } catch (Exception e){
                        Log.i("Exception", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } catch (Exception e){
            Log.i("Exception", e.getMessage());
        }
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
