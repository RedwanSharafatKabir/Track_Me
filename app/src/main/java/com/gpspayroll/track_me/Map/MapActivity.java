package com.gpspayroll.track_me.Map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.gpspayroll.track_me.R;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
