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

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
    private String latitude = "", longitude = "", currentPlace = "";
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private CardView backPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        views = inflater.inflate(R.layout.fragment_office_timeline, container, false);

        curentLocation = views.findViewById(R.id.officeLocationId);
        backPage = views.findViewById(R.id.backPageFloatingId);
        backPage.setOnClickListener(this);

        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            requestLocation();

        } else {
            Toast.makeText(getActivity(), "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }

        return views;
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

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

            getCurrentLocation();

        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestLocation();
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);

        try {
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        } catch (Exception e){
            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addressList;

        try {
            Double lat = Double.parseDouble(latitude), log = Double.parseDouble(longitude);
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            currentPlace = addressList.get(0).getAddressLine(0);

            curentLocation.setText(currentPlace);

        } catch (IOException e) {
            Log.i("ERROR ", "Permission Denied");
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
