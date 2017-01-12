package net.devcats.stepit.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.R;
import net.devcats.stepit.Utils.LogUtils;
import net.devcats.stepit.Utils.PreferencesUtils;

import butterknife.BindView;

/**
 * Created by Ken Juarez on 12/17/16.
 * Fragment that is used as a dashboard once user is logged in and has a selected device.
 */

public class HomeFragment extends BaseFragment implements DeviceHandler.DeviceResponseListener {

    @BindView(R.id.tvToken)
    TextView tvToken;
    @BindView(R.id.btnDisconnectDevice)
    Button btnDisconnectDevice;
    @BindView(R.id.btnGetSteps)
    Button btnGetSteps;
    @BindView(R.id.btnClearPreferences)
    Button btnClearPreferences;

    private Device device;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceHandler.getInstance().setDeviceResponseListener(this);
        device = DeviceHandler.getInstance().getDevice();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (device.getType() == Device.TYPE_FIT_BIT) {
            tvToken.setText(((FitBitDevice) DeviceHandler.getInstance().getDevice()).getToken());
        } else if (device.getType() == Device.TYPE_GOOGLE_FIT) {
            tvToken.setText("GOOGLE FIT!!!");
        }

        btnDisconnectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceHandler.getInstance().removeConnectedDevice(getActivity());
            }
        });

        btnGetSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceHandler.getInstance().requestSteps();
            }
        });

        btnClearPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferencesUtils.getInstance(getContext()).clear();
            }
        });
    }

    @Override
    public void onStepsReceived(final int steps) {
        tvToken.setText("STEPS!!!!: " + steps);
        LogUtils.d("STEPS!!!! " + steps);
    }
}
