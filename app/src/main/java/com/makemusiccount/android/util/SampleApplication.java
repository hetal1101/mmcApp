package com.makemusiccount.android.util;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

public class SampleApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();

        // setup default typefaces
        TypefaceProvider.registerDefaultIconSets();
    }

}
