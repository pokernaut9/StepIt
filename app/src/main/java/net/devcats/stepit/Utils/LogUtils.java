package net.devcats.stepit.Utils;

import android.util.Log;

import net.devcats.stepit.BuildConfig;

/**
 * Created by Ken Juarez on 12/17/16.
 * Utility used to control logs
 */

public class LogUtils {

    public static void d(String message) {
        if (BuildConfig.ENVIRONMENT.equals("debug"))
            Log.d("StepIt", message);
    }

    public static void d(String tag, String message) {
        if (BuildConfig.ENVIRONMENT.equals("debug"))
            Log.d(tag, message);
    }

    public static void e(String message) {
        if (BuildConfig.ENVIRONMENT.equals("debug"))
            Log.e("StepIt", message);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.ENVIRONMENT.equals("debug"))
            Log.e(tag, message);
    }

}
