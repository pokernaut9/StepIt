package net.devcats.stepit.Handlers;

import android.support.v4.app.FragmentActivity;

import net.devcats.stepit.Handlers.DeviceHandlers.GoogleFitDevice;
import net.devcats.stepit.Handlers.DeviceHandlers.FitBitDevice;
import net.devcats.stepit.Model.Device;

/**
 * Created by Ken Juarez on 12/18/16.
 * This class is used to manage the selected device
 */

public class DeviceHandler implements Device.StepsListener {

    public interface DeviceResponseListener {
        void onStepsReceived(int steps);
    }

    private DeviceResponseListener deviceResponseListener;
    private Device device;

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public int getDeviteType() {
        return device.getType();
    }

    public void connectDevice(FragmentActivity activity, int deviceType) {
        switch (deviceType) {
            case Device.TYPE_GOOGLE_FIT:
                device = new GoogleFitDevice(activity);
                device.registerListener((Device.DeviceListener) activity);
                device.registerStepsListener(this);
                device.connect();
                break;

            case Device.TYPE_FIT_BIT:
                device = new FitBitDevice(activity);
                device.registerListener((Device.DeviceListener) activity);
                device.registerStepsListener(this);
                device.connect();
                break;

            case Device.TYPE_PHONE:

                break;
        }
    }

    public void removeConnectedDevice() {
        device.remove();
    }

    public void requestSteps() {
        if (deviceResponseListener != null) {
            device.requestSteps();
        }
    }

    public void setDeviceResponseListener(DeviceResponseListener deviceResponseListener) {
        this.deviceResponseListener = deviceResponseListener;
    }


    @Override
    public void onStepsReceived(int steps) {
        deviceResponseListener.onStepsReceived(steps);
    }

}
