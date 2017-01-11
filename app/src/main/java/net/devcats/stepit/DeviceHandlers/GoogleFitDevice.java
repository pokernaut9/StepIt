package net.devcats.stepit.DeviceHandlers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.result.DailyTotalResult;

import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Utils.LogUtils;
import net.devcats.stepit.Utils.PreferencesUtils;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;
import static com.google.android.gms.fitness.data.Field.FIELD_STEPS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Ken Juarez on 12/19/16.
 * Handler for all Android Wearable related tasks.
 */

public class GoogleFitDevice extends Device implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mClient = null;

    public GoogleFitDevice() {
        setType(Device.TYPE_ANDROID_WEARABLE);
    }

    @Override
    public void connect(FragmentActivity activity) {
        mClient = new GoogleApiClient.Builder(activity)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(activity, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        LogUtils.e("Google Play services connection failed. Cause: " + connectionResult.toString());
                    }
                })
                .build();

        mClient.connect();
    }

    @Override
    public void remove(FragmentActivity activity) {
        try {
            mClient.clearDefaultAccountAndReconnect();
            mClient.stopAutoManage(activity);
            mClient.disconnect();
            mClient = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        PreferencesUtils.getInstance(activity).clearDevice();
        deviceListener.onDeviceRemoved();
    }

    @Override
    public void requestSteps() {
        new AndroidWearableGetStepsTask().execute();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LogUtils.d("Connected!!!");
        deviceListener.onDeviceConnected(Device.TYPE_ANDROID_WEARABLE);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            LogUtils.d("Connection lost. Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            LogUtils.d("Connection lost. Reason: Service Disconnected");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        LogUtils.e("ERROR: GoogleFitDevice.java: " + connectionResult.getErrorMessage());
    }

    private class AndroidWearableGetStepsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotalFromLocalDevice(mClient, TYPE_STEP_COUNT_DELTA);

            DailyTotalResult totalResult = result.await(30, SECONDS);

            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();

                if (totalSet != null) {

                    return totalSet.isEmpty()
                            ? -1
                            : totalSet.getDataPoints().get(0).getValue(FIELD_STEPS).asInt();
                }

            } else {
                // handle failure
                return -1;
            }

            return -1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            stepsListener.onStepsReceived(integer);
        }
    }
}
