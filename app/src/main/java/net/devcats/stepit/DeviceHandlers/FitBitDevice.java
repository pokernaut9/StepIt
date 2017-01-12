package net.devcats.stepit.DeviceHandlers;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import net.devcats.stepit.BuildConfig;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Utils.LogUtils;
import net.devcats.stepit.Utils.PreferencesUtils;

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

    private final static String KEY_FITBIT_TOKEN = "key_fitbit_token";
    private final static String KEY_FITBIT_TOKEN_TYPE = "key_fitbit_token_type";

    private String fitBitToken;
    private String fitBitTokenType;

    public FitBitDevice(Context context) {
        setType(Device.TYPE_FIT_BIT);

        fitBitToken = PreferencesUtils.getInstance(context).getString(KEY_FITBIT_TOKEN);
        fitBitTokenType = PreferencesUtils.getInstance(context).getString(KEY_FITBIT_TOKEN_TYPE);
    }

    @Override
    public void connect(FragmentActivity activity) {
        super.connect(activity);

        if (fitBitToken == null || fitBitToken.isEmpty()) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(activity, Uri.parse(BuildConfig.FITBIT_AUTH_URL));
        } else {
            deviceListener.onDeviceConnected(Device.TYPE_FIT_BIT);
        }
    }

    @Override
    public void remove(final FragmentActivity activity) {
        super.remove(activity);
        new FitBitSignOutTask().setContext(activity).execute();
    }

    @Override
    public void requestSteps() {
        new FitBitGetStepsTask().execute();
    }

    public void parseFitBitLoginResponse(Context context, String response) {
        fitBitToken = extractToken(response);
        PreferencesUtils.getInstance(context).setString(KEY_FITBIT_TOKEN, fitBitToken);

        fitBitTokenType = extractTokenType(response);
        PreferencesUtils.getInstance(context).setString(KEY_FITBIT_TOKEN_TYPE, fitBitTokenType);
    }

    private String extractToken(String response) {
        return response.substring(response.indexOf("access_token=") + "access_token=".length(), response.indexOf("&"));
    }

    private String extractTokenType(String response) {
        return response.substring(response.indexOf("token_type=") + "token_type=".length(), response.indexOf("&", response.indexOf("token_type=")));
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
                        .addHeader("Authorization", fitBitTokenType + " " + fitBitToken)
                        .build();
                Response response = new OkHttpClient().newCall(request).execute();

                JSONObject object = new JSONObject(response.body().string());

                // Do we have errors from FitBit?
                if (object.has("errors")) {
                    LogUtils.e(object.toString());
                    return -1;
                }

                JSONArray jsonArray = object.getJSONArray("activities-steps");
                return jsonArray.getJSONObject(0).getInt("value");
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if (integer < 0) {
                LogUtils.e("ERROR: An error occurred while trying to receive steps from FitBit!");
            }
            stepsListener.onStepsReceived(integer);
        }
    }

    private class FitBitSignOutTask extends AsyncTask<Void, Void, Void> {

        private Context context;

        public FitBitSignOutTask setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Request request = new Request.Builder()
                        .url(BuildConfig.FITBIT_REVOKE_USER_TOKEN_URL + fitBitToken)
                        .addHeader("Authorization", getAuthorizationHeader())
                        .build();

                Response response = new OkHttpClient().newCall(request).execute();
                // TODO: Handle message

                LogUtils.d(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            PreferencesUtils.getInstance(context).remove(KEY_FITBIT_TOKEN);
            PreferencesUtils.getInstance(context).remove(KEY_FITBIT_TOKEN_TYPE);

            deviceListener.onDeviceRemoved();
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
