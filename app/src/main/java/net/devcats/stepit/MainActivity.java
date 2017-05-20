package net.devcats.stepit;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import net.devcats.stepit.UI.Home.HomeFragment;
import net.devcats.stepit.UI.Login.LoginActivity;
import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.UI.SelectDevice.SelectDeviceFragment;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Handlers.PreferencesHandler;
import net.devcats.stepit.Utils.UiUtils;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements
        BaseFragment.PushFragmentInterface,
        Device.DeviceListener {

    @Inject
    DeviceHandler deviceHandler;
    @Inject
    UserHandler userHandler;
    @Inject
    PreferencesHandler preferencesHandler;

    private Uri data;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StepItApplication.getAppComponent().inject(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView tvUsername = (TextView) toolbar.findViewById(R.id.tvUsername);

        Intent intent = getIntent();
        data = intent.getData();

        userHandler.loadUser();

        if (savedInstanceState != null) {
            return;
        }

        // Are we receiving a FitBit login?
        if (data != null && data.toString().startsWith("stepit://callback#")) {

            onDeviceConnected(Device.TYPE_FIT_BIT);

        // Do we already have a user logged in?
        } else if (userHandler.getUser() != null && userHandler.getUser().getId() > 0) {

            int deviceType = preferencesHandler.getInt(Device.KEY_DEVICE_TYPE);

            // Let them select device or load existing device
            if (deviceType < 0) {
                pushFragment(SelectDeviceFragment.newInstance());
            } else {
                DeviceHandler.getInstance().connectDevice(this, deviceType);
            }

        // New user, show create account screen
        } else {
            userHandler.removeUser();
            Intent newIntent = new Intent(this, LoginActivity.class);
            startActivity(newIntent);

            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void handleDeviceClicked() {
        if (deviceHandler.getDevice() == null) {
            pushFragment(SelectDeviceFragment.newInstance());
        } else {

            UiUtils.showYesNoDialog(
                    this,
                    R.string.warning, R.string.device_already_connected,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deviceHandler.removeConnectedDevice();
                            pushFragment(SelectDeviceFragment.newInstance());
                        }
                    },
                    null
            );
        }
    }

    private void handleSignOutClicked() {
        PreferencesHandler.getInstance().clear();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
        switch (type) {
            case Device.TYPE_GOOGLE_FIT:

                break;

            case Device.TYPE_FIT_BIT:

                if (data != null) { // Only need to call this when a device is added for the first time, or updating token.
                    FitBitDevice device = (FitBitDevice) deviceHandler.getDevice();
                    if (device != null) {
                        device.parseFitBitLoginResponse(data.toString());
                    }
                }
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
