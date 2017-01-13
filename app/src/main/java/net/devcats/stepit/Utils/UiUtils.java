package net.devcats.stepit.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

/**
 * Created by Ken Juarez on 12/19/16.
 * This class is used to display reusable UI elements, such as dialog boxes and toast messages
 */

public class UiUtils {

    public static void showWarning(Context context, int titleRes, int messageRes) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .create().show();
    }

    public static void showYesNoDialog(Context context, int titleRes, int messageRes, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setMessage(messageRes)
                .setPositiveButton(android.R.string.yes, positiveListener)
                .setNegativeButton(android.R.string.no, negativeListener)
                .create().show();
    }
}
