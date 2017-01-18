package net.devcats.stepit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import net.devcats.stepit.Fragments.BaseFragment;
import net.devcats.stepit.Fragments.HomeFragment;
import net.devcats.stepit.Fragments.SelectDeviceFragment;
import net.devcats.stepit.Handlers.DeviceHandler;
import net.devcats.stepit.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.Handlers.UserHandler;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Utils.PreferencesUtils;
import net.devcats.stepit.Utils.UiUtils;

public class MainActivity extends AppCompatActivity implements BaseFragment.PushFragmentInterface, Device.DeviceListener {

    // TODO: Show device type in menu

    private DeviceHandler deviceHandler;
    private Uri data;
    private ActionBarDrawerToggle drawerToggle;
    private UserHandler userHandler;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        data = intent.getData();

        deviceHandler = DeviceHandler.getInstance();

        userHandler = UserHandler.getInstance();
        userHandler.loadUser(this);

        if (savedInstanceState != null) {
            return;
        }

        initMenuDrawer();

        // Are we receiving a FitBit login?
        if (data != null && data.toString().startsWith("stepit://callback#")) {

            onDeviceConnected(Device.TYPE_FIT_BIT);

        // Do we already have a user logged in?
        } else if (userHandler.getUser() != null && userHandler.getUser().getId() > 0) {

            int deviceType = PreferencesUtils.getInstance().getInt(this, Device.KEY_DEVICE_TYPE);

            // Let them select device or load existing device
            if (deviceType < 0) {
                pushFragment(SelectDeviceFragment.newInstance());
            } else {
                DeviceHandler.getInstance().connectDevice(this, deviceType);
            }

        // New user, show create account screen
        } else {
            userHandler.removeUser(this);
            Intent newIntent = new Intent(this, LoginActivity.class);
            startActivity(newIntent);

            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    private void initMenuDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        setSupportActionBar(toolbar);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });


        drawerLayout.addDrawerListener(drawerToggle);

        drawerToggle.syncState();

        View header = navigationView.getHeaderView(0);

        TextView tvName = (TextView) header.findViewById(R.id.tvName);
        tvName.setText(userHandler.getUser().getName());
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch(menuItem.getItemId()) {

            case R.id.navDevice:
                handleDeviceClicked();
                break;

            case R.id.navSignOut:
                handleSignOutClicked();
                break;
        }

        setTitle(menuItem.getTitle());
        drawerLayout.closeDrawers();
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
                            deviceHandler.removeConnectedDevice(MainActivity.this);
                            pushFragment(SelectDeviceFragment.newInstance());
                        }
                    },
                    null
            );
        }
    }

    private void handleSignOutClicked() {
        PreferencesUtils.getInstance().clear(this);
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
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
                navigationView.getMenu().findItem(R.id.navDevice).setTitle(R.string.google_fit_device);
                break;

            case Device.TYPE_FIT_BIT:
                navigationView.getMenu().findItem(R.id.navDevice).setTitle(R.string.fit_bit_device);
                if (data != null) { // Only need to call this when a device is added for the first time, or updating token.
                    FitBitDevice device = (FitBitDevice) deviceHandler.getDevice();
                    if (device != null) {
                        device.parseFitBitLoginResponse(this, data.toString());
                    }
                }
                break;
        }

        pushFragment(HomeFragment.newInstance());
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void onDeviceRemoved() {
        deviceHandler.setDevice(null);
        finish();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
