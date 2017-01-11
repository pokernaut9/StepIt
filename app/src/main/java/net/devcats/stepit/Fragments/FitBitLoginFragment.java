package net.devcats.stepit.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.devcats.stepit.R;

/**
 * Created by Ken Juarez on 12/17/16.
 * Fragment that allows the user to login to their FitBit account
 */

public class FitBitLoginFragment extends BaseFragment {

    public static FitBitLoginFragment newInstance() {
        return new FitBitLoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fitbit_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        FitBitDevice.startLogin(getContext());
    }

}
