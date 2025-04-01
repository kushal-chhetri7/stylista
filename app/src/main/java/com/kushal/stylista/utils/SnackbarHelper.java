package com.kushal.stylista.utils;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.kushal.stylista.R;


public class SnackbarHelper {

    public static void showSnackbar(View view, String message, SnackbarType type) {
        showSnackbar(view, message, type, LENGTH_SHORT);
    }

    public static void showSnackbar(View view, String message, SnackbarType type, int duration) {
        Snackbar.make(view, message, duration)
                .setBackgroundTint(view.getContext().getColor(getBackgroundColor(type)))
                .setTextColor(view.getContext().getColor(R.color.snackbar_text_color))
                .show();
    }

    private static int getBackgroundColor(SnackbarType type) {
        switch (type) {
            case GENERAL:
                return R.color.snackbar_general_bg;
            case SUCCESS:
                return R.color.snackbar_success_bg;
            case ERROR:
                return R.color.snackbar_error_bg;
        }
        return 0;
    }


}
