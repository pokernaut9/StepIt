package net.devcats.stepit.DeviceHandlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import net.devcats.stepit.BuildConfig;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Ken Juarez on 12/17/16.
 * Handler for all FitBit related tasks.
 */

public class FitBitDevice extends Device {

    private String fitBitToken;

    public FitBitDevice() {
        setType(Device.TYPE_FIT_BIT);
    }

    @Override
    public void connect(FragmentActivity activity) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(activity, Uri.parse(BuildConfig.FITBIT_AUTH_URL));
    }

    @Override
    public void remove(final FragmentActivity activity) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Request request = new Request.Builder()
                            .url(BuildConfig.FITBIT_REVOKE_USER_TOKEN_URL + fitBitToken)
                            .addHeader("Authorization", getAuthorizationHeader())
                            .build();

                    Response response = new OkHttpClient().newCall(request).execute();

                    SharedPreferences.Editor editor = activity.getSharedPreferences(activity.getApplicationInfo().name, Context.MODE_PRIVATE).edit();
                    editor.remove(Device.KEY_DEVICE_DATA);
                    editor.remove(Device.KEY_DEVICE_TYPE);
                    editor.apply();

                    deviceListener.onDeviceRemoved();

                    LogUtils.d(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void requestSteps() {
        new FitBitGetStepsTask().execute();
    }

    public void parseFitBitLoginResponse(String response) {
        fitBitToken = extractToken(response);
    }

    private String extractToken(String response) {
        return response.substring(response.indexOf("access_token=") + "access_token=".length(), response.indexOf("&"));
    }

    public String getToken() {
        return fitBitToken;
    }

    private class FitBitGetStepsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                Request request = new Request.Builder()
                        .url(BuildConfig.FITBIT_GET_STEPS_URL)
                        .addHeader("Authorization", getAuthorizationHeader())
                        .build();
                Response response = new OkHttpClient().newCall(request).execute();

                JSONObject object = new JSONObject(response.body().string());

                // Do we have errors from FitBit?
                if (object.has("errors")) {
                    LogUtils.e(object.toString());
                    return -1;
                }

                JSONArray jsonArray = new JSONObject(response.body().string()).getJSONArray("activities-log-steps");
                return jsonArray.getJSONObject(0).getInt("value");
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer >= 0) {
                stepsListener.onStepsReceived(integer);
            } else {
                LogUtils.e("ERROR: An error occurred while trying to receive steps from FitBit!");
            }
        }
    }

    private String getAuthorizationHeader() {
        try {
            String data = BuildConfig.FITBIT_CLIENT_ID + ":" + BuildConfig.FITBIT_CLIENT_SECRET;
            return "Basic " + Base64.encodeToString(data.getBytes("UTF-8"), Base64.DEFAULT).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
