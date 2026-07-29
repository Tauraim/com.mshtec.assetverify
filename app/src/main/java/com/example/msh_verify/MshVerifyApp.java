package com.example.msh_verify;

import android.app.Application;
import com.google.android.material.color.DynamicColors;

public class MshVerifyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Apply dynamic color to all activities in the app
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
