package com.bmw.locationfinder;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by f68dpim on 8/13/16.
 */
public class LocationApplication extends Application {
    private static RequestQueue sRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        sRequestQueue = Volley.newRequestQueue(this);
    }

    /**
     * Returns, Volley RequestQueue Applicaiton is Ideal place for holding SingleInstances
     * @return RequestQueue
     */
    public static RequestQueue getRequestQueue() {
        return sRequestQueue;
    }
}
