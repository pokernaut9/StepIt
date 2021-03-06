package net.devcats.stepit;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import net.devcats.stepit.Handlers.CompetitionsHandler;
import net.devcats.stepit.Interfaces.RefreshCallbacks;
import net.devcats.stepit.UI.Competition.CompetitionFragment;
import net.devcats.stepit.UI.Home.HomeFragment;
import net.devcats.stepit.UI.Login.LoginActivity;
import net.devcats.stepit.UI.Base.BaseFragment;
import net.devcats.stepit.UI.NewCompetition.NewCompetitionFragment;
import net.devcats.stepit.UI.SelectDevice.SelectDeviceFragment;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.Handlers.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Handlers.PreferencesHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static net.devcats.stepit.Handlers.DeviceHandlers.GoogleFitDevice.REQUEST_CODE_RESOLVE_ERR;

public class MainActivity extends FragmentActivity implements
        BaseFragment.PushFragmentInterface,
        BaseFragment.FABControls,
        Device.DeviceListener {

    @Inject
    DeviceHandler deviceHandler;
    @Inject
    UserHandler userHandler;
    @Inject
    PreferencesHandler preferencesHandler;
    @Inject
    CompetitionsHandler competitionsHandler;

    private Uri data;
    private FloatingActionButton floatingActionButton;
    private List<RefreshCallbacks> refreshCallbacks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        StepItApplication.getAppComponent().inject(this);

        Intent intent = getIntent();
        data = intent.getData();

        userHandler.loadUser();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                currentFragment.onFabTouched();
            }
        });

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
                deviceHandler.connectDevice(this, deviceType);
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
    }

    @Override
    public void onBackPressed() {
        BaseFragment currentFragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof CompetitionFragment || currentFragment instanceof NewCompetitionFragment) {
            removeFragment(currentFragment);
        } else {
            super.onBackPressed();
        }

        refreshFragments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RESOLVE_ERR && resultCode == RESULT_OK) {
            deviceHandler.getDevice().connect();
        }
    }

//    private void handleDeviceClicked() {
//        if (deviceHandler.getDevice() == null) {
//            pushFragment(SelectDeviceFragment.newInstance());
//        } else {
//
//            UiUtils.showYesNoDialog(
//                    this,
//                    R.string.warning, R.string.device_already_connected,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            deviceHandler.removeConnectedDevice();
//                            pushFragment(SelectDeviceFragment.newInstance());
//                        }
//                    },
//                    null
//            );
//        }
//    }
//
//    private void handleSignOutClicked() {
//        preferencesHandler.clear();
//        startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        finish();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                removeFragment(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public void setFABVisible(boolean visible) {
        floatingActionButton.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void refreshFragments() {
        for (RefreshCallbacks callback : refreshCallbacks) {
            callback.refresh();
        }
    }

    public void registerRefreshCallback(BaseFragment fragment) {
        refreshCallbacks.add(fragment);
    }

    public void unregisterRefreshCallback(BaseFragment fragment) {
        refreshCallbacks.remove(fragment);
    }
}
