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
}
