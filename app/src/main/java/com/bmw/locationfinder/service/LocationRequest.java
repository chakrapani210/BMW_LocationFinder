package com.bmw.locationfinder.service;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;

/**
 * LocationRequest is customised VolleyRequest for parsing the data from server.
 * Actually, we can directly use StringRequest for our purpose. But, this will hold good, if there are nay changes in API response syntax.
 *
 */
public class LocationRequest extends Request<Location[]> {
    private static final String TAG = LocationRequest.class.getSimpleName();
    private static final String LOCATION_URL = "http://localsearch.azurewebsites.net/api/Locations";

    private Gson mGson;
    private ILocationService.OnDataAvailableCallback mListner;
    public LocationRequest(ILocationService.OnDataAvailableCallback listener) {
        super(Method.GET, LOCATION_URL, listener);
        mGson = new Gson();
        mListner = listener;
    }

    @Override
    protected Response<Location[]> parseNetworkResponse(NetworkResponse response) {

        String jsonString = null;
        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new VolleyError("Response Parsing was failed"));
        }
        Log.v(TAG, "Parsed Json string ::" + jsonString);

        //JsonElement jsonElement = mGson.fromJson(jsonString, JsonElement.class);
        Location[] locations = mGson.fromJson(jsonString, Location[].class);
        /*JsonArray jsonArray = jsonElement.getAsJsonArray();
        Location[] locations = parseLocationData(jsonArray);*/
        return Response.success(locations, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(Location[] response) {
        mListner.onSuccess(response);
    }

    /*private Location[] parseLocationData(JsonArray dataArray) {
        Location[] result = null;
        if(dataArray != null && dataArray.size() > 0) {
            result = new Location[dataArray.size()];
            for (int i = 0; i < dataArray.size(); i++) {
                JsonElement element = dataArray.get(i);
                result[i] = parseLocationElement(element);
            }
        }
        return result;

    }

    private Location parseLocationElement(JsonElement element) {
        Location location = new Location();
        JsonObject locationObj = element.getAsJsonObject();
        location.setID(locationObj.get(Location.ID_NAME).getAsInt());
        location.setName(locationObj.get(Location.NAME).getAsString());
        location.setLatitude(locationObj.get(Location.LATITUDE).getAsDouble());
        location.setLongitude(locationObj.get(Location.LONGITUDE).getAsDouble());
        location.setAddress(locationObj.get(Location.ADDRESS).getAsString());
        location.setArrivalTime(locationObj.get(Location.ARRIVAL_TIMES).getAsString());
        return location;
    }*/
}
