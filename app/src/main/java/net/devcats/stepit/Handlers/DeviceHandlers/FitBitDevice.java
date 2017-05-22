package net.devcats.stepit.Handlers.DeviceHandlers;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;

import net.devcats.stepit.BuildConfig;
import net.devcats.stepit.Model.Device;
import net.devcats.stepit.Handlers.PreferencesHandler;
import net.devcats.stepit.StepItApplication;
import net.devcats.stepit.Utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import javax.inject.Inject;

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

    private final static String FITBIT_ERROR_EXPIRED_TOKEN_STRING = "expired_token";
    private final static int FITBIT_ERROR_EXPIRED_TOKEN = -2;

    private String fitBitToken;
    private String fitBitTokenType;

    private FragmentActivity fragmentActivity;

    @Inject
    PreferencesHandler preferencesHandler;

    public FitBitDevice() {
        StepItApplication.getAppComponent().inject(this);
        setType(Device.TYPE_FIT_BIT);

        fitBitToken = preferencesHandler.getString(KEY_FITBIT_TOKEN);
        fitBitTokenType = preferencesHandler.getString(KEY_FITBIT_TOKEN_TYPE);
    }

    @Override
    public void connect(FragmentActivity context) {
        super.connect(context);

        this.fragmentActivity = context;

        if (fitBitToken == null || fitBitToken.isEmpty()) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(BuildConfig.FITBIT_AUTH_URL));
        } else {
            deviceListener.onDeviceConnected(Device.TYPE_FIT_BIT);
        }
    }

    @Override
    public void remove() {
        super.remove();
        new FitBitSignOutTask().execute();
    }

    @Override
    public void requestSteps() {
        new FitBitGetStepsTask().execute();
    }

    public void parseFitBitLoginResponse(String response) {
        fitBitToken = extractToken(response);
        preferencesHandler.setString(KEY_FITBIT_TOKEN, fitBitToken);

        fitBitTokenType = extractTokenType(response);
        preferencesHandler.setString(KEY_FITBIT_TOKEN_TYPE, fitBitTokenType);
    }

    private String extractToken(String response) {
        return response.substring(response.indexOf("access_token=") + "access_token=".length(), response.indexOf("&"));
    }

    private String extractTokenType(String response) {
        return response.substring(response.indexOf("token_type=") + "token_type=".length(), response.indexOf("&", response.indexOf("token_type=")));
    }

    private class FitBitGetStepsTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                Request request = new Request.Builder()
                        .url(BuildConfig.FITBIT_GET_STEPS_DAY_URL)
                        .addHeader("Authorization", fitBitTokenType + " " + fitBitToken)
                        .build();
                Response response = new OkHttpClient().newCall(request).execute();

                JSONObject object = new JSONObject(response.body().string());

                LogUtils.d(object.toString());

                // Do we have errors from FitBit?
                if (object.has("errors")) {
                    LogUtils.e(object.toString());

                    JSONObject errorObject = object.getJSONArray("errors").getJSONObject(0);

                    if (errorObject.get("errorType").equals(FITBIT_ERROR_EXPIRED_TOKEN_STRING)) {
                        return FITBIT_ERROR_EXPIRED_TOKEN;
                    }

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

            switch (integer) {
                case FITBIT_ERROR_EXPIRED_TOKEN:
                    fitBitToken = "";
                    fitBitTokenType = "";
                    preferencesHandler.setString(KEY_FITBIT_TOKEN, "");
                    preferencesHandler.setString(KEY_FITBIT_TOKEN_TYPE, "");
                    connect(fragmentActivity);
                    break;

                default:
                    stepsListener.onStepsReceived(integer);
            }
        }
    }

    private class FitBitSignOutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Request request = new Request.Builder()
                        .url(BuildConfig.FITBIT_REVOKE_USER_TOKEN_URL + fitBitToken)
                        .addHeader("Authorization", getAuthorizationHeader())
                        .build();

                new OkHttpClient().newCall(request).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            preferencesHandler.remove(KEY_FITBIT_TOKEN);
            preferencesHandler.remove(KEY_FITBIT_TOKEN_TYPE);

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
