package com.gpspayroll.track_me.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gpspayroll.track_me.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private CardView backPage;
    private NetworkInfo netInfo;
    private GoogleMap mGoogleMap;
    private float zoomLevel = 16f;
    private ConnectivityManager cm;
    private int PERMISSION_ID = 101;
    private DatabaseReference databaseReference;
    private SupportMapFragment supportMapFragment;
    private String dateNow, lattitude="", longitude="", employeeName="";
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapID);
        supportMapFragment.getMapAsync(this);

        Date cal = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
        dateNow = simpleDateFormat1.format(cal);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();

        backPage = findViewById(R.id.backFromMapId);
        backPage.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Employees List");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            getLastLocation();
        } else {
            Toast.makeText(MapActivity.this, "Turn On Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.map_style));
    }

    private void getEmployeesLocation() {
        try{
            databaseReference.child(dateNow).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot items : snapshot.getChildren()) {
                            try {
                                for (DataSnapshot item : items.getChildren()) {
                                    if(item.getKey().equals("checkout")) {
                                        if (item.getValue().toString().equals("Counting")) {
                                            // Get Lattitude
                                            databaseReference.child(dateNow).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    try {
                                                        for (DataSnapshot items : snapshot.getChildren()) {
                                                            try {
                                                                for (DataSnapshot item : items.getChildren()) {
                                                                    if (item.getKey().equals("lattitude")) {
                                                                        lattitude = item.getValue().toString();

                                                                        // Get Longitude
                                                                        databaseReference.child(dateNow).addValueEventListener(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                try {
                                                                                    for (DataSnapshot items : snapshot.getChildren()) {
                                                                                        try {
                                                                                            for (DataSnapshot item : items.getChildren()) {
                                                                                                if (item.getKey().equals("longitude")) {
                                                                                                    longitude = item.getValue().toString();

                                                                                                    // Get UserName
                                                                                                    databaseReference.child(dateNow).addValueEventListener(new ValueEventListener() {
                                                                                                        @Override
                                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                            try {
                                                                                                                for (DataSnapshot items : snapshot.getChildren()) {
                                                                                                                    try {
                                                                                                                        for (DataSnapshot item : items.getChildren()) {
                                                                                                                            if (item.getKey().equals("username")) {
                                                                                                                                employeeName = item.getValue().toString();

                                                                                                                                LatLng SearchlatLng = new LatLng(Double.parseDouble(lattitude), Double.parseDouble(longitude));

                                                                                                                                mGoogleMap.addMarker(new MarkerOptions().position(SearchlatLng)
                                                                                                                                        .title(employeeName).icon(bitmapDescriptorFromVector(MapActivity.this,
                                                                                                                                                R.drawable.lot_marker)));
                                                                                                                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SearchlatLng, zoomLevel));
                                                                                                                            }
                                                                                                                        }

                                                                                                                    } catch (Exception e) {
                                                                                                                        Log.i("TAG_Error", e.getMessage());
                                                                                                                    }
                                                                                                                }
                                                                                                            } catch (Exception e) {
                                                                                                                Log.i("TAG_Error", e.getMessage());
                                                                                                            }
                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                                                            Log.i("Tag_DatabaseError", error.getMessage());
                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            }

                                                                                        } catch (Exception e) {
                                                                                            Log.i("TAG_Error", e.getMessage());
                                                                                        }
                                                                                    }
                                                                                } catch (Exception e) {
                                                                                    Log.i("TAG_Error", e.getMessage());
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                                Log.i("Tag_DatabaseError", error.getMessage());
                                                                            }
                                                                        });
                                                                    }
                                                                }

                                                            } catch (Exception e){
                                                                Log.i("TAG_Error", e.getMessage());
                                                            }
                                                        }
                                                    } catch (Exception e){
                                                        Log.i("TAG_Error", e.getMessage());
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.i("Tag_DatabaseError", error.getMessage());
                                                }
                                            });
                                        }
                                    }
                                }

                            } catch (Exception e){
                                Log.i("TAG_Error", e.getMessage());
                            }
                        }
                    } catch (Exception e){
                        Log.i("TAG_Error", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("Tag_DatabaseError", error.getMessage());
                }
            });

        } catch (Exception e){
            Log.i("TAG_Error", e.getMessage());
        }
    }

    // Set Location marker with image bitmap
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int VectorID) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, VectorID);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

///////////////////////////////
    // Map Permission
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    getEmployeesLocation();
                                }
                            }
                        }
                );

            } else {
                Toast.makeText(MapActivity.this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lattitude = String.valueOf(mLastLocation.getLatitude());
            longitude = String.valueOf(mLastLocation.getLongitude());
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
///////////////////////////////

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backFromMapId){
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
