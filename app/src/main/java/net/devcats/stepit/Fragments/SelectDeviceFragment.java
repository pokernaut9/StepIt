package net.devcats.stepit.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.R;

import butterknife.BindView;

/**
 * Created by Ken Juarez on 12/17/16.
 * Fragment used to select which type of device we are working with
 */

public class SelectDeviceFragment extends BaseFragment {

    @BindView(R.id.btnFitBit)
    Button btnFitBit;
    @BindView(R.id.btnAndroidWearable)
    Button btnAndroidWearable;

    public static SelectDeviceFragment newInstance() {
        return new SelectDeviceFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_divice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAndroidWearable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceHandler.getInstance().connectDevice(getActivity(), Device.TYPE_ANDROID_WEARABLE);
            }
        });

        btnFitBit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceHandler.getInstance().connectDevice(getActivity(), Device.TYPE_FIT_BIT);
            }
        });
    }
}
