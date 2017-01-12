package net.devcats.stepit.Utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

/**
 * Created by Ken Juarez on 12/19/16.
 * This class is used to display reusable UI elements, such as dialog boxes and toast messages
 */

public class UiUtils {

    public static void showWarning(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .create().show();
    }

    public static String formatPhoneNumber(String s) {
        String formattedPhoneNumber = "";
        if (!TextUtils.isEmpty(s)) {
            boolean hasCountryCode = hasCountryCode(s);
            String digitsOnly = extractDigits(s);
            if (hasCountryCode) {
                digitsOnly = digitsOnly.substring(1);
            }
            if (digitsOnly.length() > 0) {
                formattedPhoneNumber = "+1 ";
                if (digitsOnly.length() <= 3) {
                    formattedPhoneNumber += String.format("(%s)", digitsOnly);
                } else if (digitsOnly.length() <= 6) {
                    formattedPhoneNumber += String.format("(%s) %s", digitsOnly.substring(0, 3), digitsOnly.substring(3));
                } else {
                    formattedPhoneNumber += String.format("(%s) %s-%s", digitsOnly.substring(0, 3), digitsOnly.substring(3, 6), digitsOnly.substring(6));
                }
            }
        }
        return formattedPhoneNumber;
    }

    private static String extractDigits(String s) {
        return !TextUtils.isEmpty(s) ? s.replaceAll("\\D+","") : "";
    }

    private static boolean hasCountryCode(String s) {
        return !TextUtils.isEmpty(s) && s.startsWith("+1");
    }
}
