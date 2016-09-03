package com.fdv.loggedoff;

import android.app.Application;
import android.content.Context;

public class LoggedOffApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();


    }
    /**
     * Provides access to the singleton and the getCloudinary method
     * @param context Android Application context
     * @return instance of the Application singleton.
     */
    public static LoggedOffApplication getInstance(Context context) {
        return (LoggedOffApplication)context.getApplicationContext();
    }

}
