package com.bmw.locationfinder.service;

import com.android.volley.Response;
import com.google.maps.model.LatLng;

/**
 * ILocationService interface defines the API of LocationService
 */
public interface ILocationService {
    public static interface OnDataAvailableCallback extends Response.ErrorListener {
        void onSuccess(Location[] data);
    }
    void getLocations(OnDataAvailableCallback callback);
    void updateDistancesToDestination(Location[] locations, LatLng destination, OnDataAvailableCallback callback);
}
