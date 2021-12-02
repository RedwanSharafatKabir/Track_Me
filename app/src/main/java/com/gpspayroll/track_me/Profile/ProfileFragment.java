package com.gpspayroll.track_me.Profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gpspayroll.track_me.BackPageListener.BackListenerFragment;
import com.gpspayroll.track_me.EmployeeActivity.MainActivity;
import com.gpspayroll.track_me.EmployeeFragment.Dashboard;
import com.gpspayroll.track_me.R;

public class ProfileFragment extends Fragment implements BackListenerFragment {

    public static BackListenerFragment backBtnListener;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onBackPressed() {
        ((MainActivity) getActivity()).bottomNavigationView.getMenu().setGroupCheckable(0, false, true);

        fragment = new Dashboard();
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
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
