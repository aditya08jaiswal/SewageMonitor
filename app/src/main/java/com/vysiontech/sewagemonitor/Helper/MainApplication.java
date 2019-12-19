package com.vysiontech.sewagemonitor.Helper;


import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base,"hi"));
    }
}
