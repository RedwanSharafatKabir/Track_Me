package com.gps.payroll.profileAndAbout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gps.payroll.backPageListener.BackListenerFragment;
import com.gps.payroll.splashAndDashboard.Dashboard;
import com.gps.payroll.splashAndDashboard.MainActivity;
import com.gps.payroll.R;

public class AboutFragment extends Fragment implements BackListenerFragment {

    public static BackListenerFragment backBtnListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) requireActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        Fragment fragment = new Dashboard();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
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
