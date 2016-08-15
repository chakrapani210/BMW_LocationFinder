package com.bmw.locationfinder.service;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.bmw.locationfinder.LocationApplication;
import com.bmw.locationfinder.R;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

/**
 * LocationService implements for Location API's for GetLocations and Distance Calculations
 * TO-DO: To be implemented as a Bind Service component.
 */
public class LocationService implements ILocationService {

    // Free DistanceMatrixAPI key from Google, is supporting only 5 endpoint at a time.
    // so, the each request holds 5 locations for distance calculation.
    private static final int MAX_LOCATIONS_PER_REQUEST = 5;
    private final Context mContext;

    public LocationService(Context context) {
        mContext = context;
    }

    @Override
    public void getLocations(OnDataAvailableCallback callback) {
        RequestQueue queue = LocationApplication.getRequestQueue();
        Request<Location[]> request = new LocationRequest(callback);
        queue.add(request);
    }

    @Override
    public void updateDistancesToDestination(Location[] locations, final LatLng destination, final OnDataAvailableCallback callback) {
        AsyncTask<Location[], Void, Location[]> task = new AsyncTask<Location[], Void, Location[]>() {
            @Override
            protected Location[] doInBackground(Location[]... locations) {
                GeoApiContext context = new GeoApiContext();
                context.setApiKey(mContext.getString(R.string.geo_api_key));

                // Free DistanceMatrixAPI key from Google, is supporting only 5 endpoint at a time.
                // so, the each request holds 5 locations for distance calculation.
                for(int j = 0; j < locations[0].length; j+= MAX_LOCATIONS_PER_REQUEST) {
                    int length = locations[0].length - j < MAX_LOCATIONS_PER_REQUEST ? locations[0].length - j : MAX_LOCATIONS_PER_REQUEST;
                    LatLng[] origins = new LatLng[length];
                    DistanceMatrixApiRequest matrix = DistanceMatrixApi.newRequest(context);
                    for (int i = 0; i < length; i++) {
                        origins[i] = new LatLng(locations[0][j+i].getLatitude(), locations[0][j+i].getLongitude());
                    }
                    matrix.origins(origins);
                    matrix.destinations(destination);
                    matrix.mode(TravelMode.DRIVING);
                    matrix.units(Unit.IMPERIAL);
                    DistanceMatrix result = matrix.awaitIgnoreError();
                    DistanceMatrixRow[] rows = result.rows;
                    for (int i = 0; i < rows.length; i++) {
                        locations[0][j+i].setDistanceToDest(rows[i].elements[0].distance.inMeters);
                    }
                }
                return locations[0];
            }

            @Override
            protected void onPostExecute(Location[] locations) {
                super.onPostExecute(locations);
                callback.onSuccess(locations);
            }
        };
        task.execute(locations);

    }
}
