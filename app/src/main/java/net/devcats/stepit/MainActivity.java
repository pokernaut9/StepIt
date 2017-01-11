package net.devcats.stepit;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.devcats.stepit.Fragments.BaseFragment;
import net.devcats.stepit.Fragments.CreateAccountFragment;
import net.devcats.stepit.Fragments.HomeFragment;
import net.devcats.stepit.Fragments.SelectDeviceFragment;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Utils.PreferencesUtils;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements BaseFragment.PushFragmentInterface, Device.DeviceListener {

    private DeviceHandler deviceHandler;
    private Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        data = intent.getData();

        deviceHandler = DeviceHandler.getInstance();

        UserHandler userHandler = UserHandler.getInstance();
        userHandler.loadUser(this);

        if (savedInstanceState != null) {
            return;
        }

        // Are we receiving a FitBit login?
        if (data != null && data.toString().startsWith("stepit://callback#")) {

            onDeviceConnected(Device.TYPE_FIT_BIT);

        // Do we already have a user logged in?
        } else if (userHandler.getUser() != null && userHandler.getUser().getId() > 0) {

            int deviceType = PreferencesUtils.getInstance(this).getInt(Device.KEY_DEVICE_TYPE);

            // Let them select device or load existing device
            if (deviceType < 0) {
                pushFragment(SelectDeviceFragment.newInstance());
            } else {
                DeviceHandler.getInstance().connectDevice(this, deviceType);
            }

        // New user, show create account screen
        } else {


            userHandler.removeUser(this);
            pushFragment(CreateAccountFragment.newInstance());
        }
    }

    @Override
    public void pushFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void removeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();

    }

    @Override
    public void onDeviceConnected(int type) {
        PreferencesUtils.getInstance(this).setInt(Device.KEY_DEVICE_TYPE, type);

        switch (type) {
            case Device.TYPE_ANDROID_WEARABLE:

                break;

            case Device.TYPE_FIT_BIT:
                ((FitBitDevice) deviceHandler.getDevice()).parseFitBitLoginResponse(data.toString());
                break;
        }

        pushFragment(HomeFragment.newInstance());
    }

    @Override
    public void onDeviceRemoved() {
        deviceHandler.setDevice(null);
        finish();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
