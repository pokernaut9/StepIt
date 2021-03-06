package net.devcats.stepit.Model;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import net.devcats.stepit.Handlers.PreferencesHandler;
import net.devcats.stepit.StepItApplication;

/**
 * Created by Ken Juarez on 12/18/16.
 * Used to hold information about the selected device.
 */

public abstract class Device {

    public interface DeviceListener {
        void onDeviceConnected(int type);
        void onDeviceRemoved();
    }

    public interface StepsListener {
        void onStepsReceived(int steps);
    }

    public static final String KEY_DEVICE_TYPE = "device_type";

    public static final int TYPE_GOOGLE_FIT = 0;
    public static final int TYPE_FIT_BIT = 1;
    public static final int TYPE_PHONE = 2;

    private int type;

    protected DeviceListener deviceListener;
    protected StepsListener stepsListener;

    public void connect() {
        StepItApplication.getAppComponent().preferencesHandler().setInt(Device.KEY_DEVICE_TYPE, type);
    }

    public abstract void requestSteps();

    public void remove() {
        StepItApplication.getAppComponent().preferencesHandler().remove(Device.KEY_DEVICE_TYPE);
    }

    public int getType() {
        return type;
    }

    protected void setType(int type) {
        this.type = type;
    }

    public void registerListener(DeviceListener deviceListener) {
        this.deviceListener = deviceListener;
    }

    public void registerStepsListener(StepsListener stepsListener) {
        this.stepsListener = stepsListener;
    }
}
