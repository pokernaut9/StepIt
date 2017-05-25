package net.devcats.stepit.Handlers.DeviceHandlers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.result.DailyTotalResult;

import net.devcats.stepit.Model.Device;

import static com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA;
import static com.google.android.gms.fitness.data.Field.FIELD_STEPS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by Ken Juarez on 12/19/16.
 * Handler for all Android Wearable related tasks.
 */

public class GoogleFitDevice extends Device implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final int REQUEST_CODE_RESOLVE_ERR = 1001;
    private GoogleApiClient mClient = null;

    private FragmentActivity fragmentActivity;

    public GoogleFitDevice(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
        setType(Device.TYPE_GOOGLE_FIT);
    }

    @Override
    public void connect() {
        super.connect();

        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(fragmentActivity)
                    .enableAutoManage(fragmentActivity, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Fitness.HISTORY_API)
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .build();
        }

        if (!mClient.isConnecting()) {
            mClient.connect();
        }
    }

    @Override
    public void remove() {
        super.remove();

        try {
            mClient.clearDefaultAccountAndReconnect();
            mClient.stopAutoManage(fragmentActivity);
            mClient.disconnect();
            mClient = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        deviceListener.onDeviceRemoved();
    }

    @Override
    public void requestSteps() {
        new AndroidWearableGetStepsTask().execute();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("XXXXX", "Connected!!!");
        deviceListener.onDeviceConnected(Device.TYPE_GOOGLE_FIT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.d("XXXXX", "Connection lost. Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.d("XXXXX", "Connection lost. Reason: Service Disconnected");
        }
        mClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
