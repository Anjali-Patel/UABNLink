package com.uabn.gss.uabnlink.Activities;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Constants {

    public static final String TAG = "UABN ";

    public static final int MULTI_PERMISSION = 1001;

    public static void hideKeyboard(Activity activity) {
        View v = activity.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null && v != null;
//        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
