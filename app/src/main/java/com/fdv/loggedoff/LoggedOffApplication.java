package com.fdv.loggedoff;

import android.app.Application;
import android.content.Context;

import com.cloudinary.Cloudinary;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

public class LoggedOffApplication extends Application{
    private static String CLOUD_NAME ="disconnect";
    private static String CLOUD_API_KEY ="126789643432281";
    private static String CLOUD_API_SECRET_KEY ="c7uQvvIKRdFGNO-BofcNlhvpIpc";

    /**
     * Initializes UIL, Parse and Cloudinary upon creation of Application.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        initCloudinary();

    }
    private Cloudinary cloudinary;

    /**
     * @return An initialized Cloudinary instance
     */
    public Cloudinary getCloudinary() {
        return cloudinary;
    }

    /**
     * Provides access to the singleton and the getCloudinary method
     * @param context Android Application context
     * @return instance of the Application singleton.
     */
    public static LoggedOffApplication getInstance(Context context) {
        return (LoggedOffApplication)context.getApplicationContext();
    }

    private void initCloudinary() {
        // Cloudinary: creating a cloudinary instance using meta-data from manifest

     //   cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(this));
        Map config = new HashMap();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", CLOUD_API_KEY);
        config.put("api_secret", CLOUD_API_SECRET_KEY);
        cloudinary = new Cloudinary(config);

     //   L.i("Cloudinary initialized");
    }
}
