package com.bmw.locationfinder.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bmw.locationfinder.R;
import com.bmw.locationfinder.service.Location;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by f68dpim on 8/14/16.
 */
public class LocationMapFragment extends Fragment {
    private Location mLocation;

    private GoogleMap mGoogleMap;
    private MapView mMapView;

    public static LocationMapFragment getInstance(Location location) {
        LocationMapFragment fragment = new LocationMapFragment();
        Bundle data = new Bundle();
        data.putParcelable(Location.LOCATION_KEY, location);
        fragment.setArguments(data);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mLocation = getArguments().getParcelable(Location.LOCATION_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            //mLocation = savedInstanceState.getParcelable(Location.LOCATION_KEY);
        }
        View view = inflater.inflate(R.layout.fragment_location_map_layout, container, false);
        mMapView = (MapView)view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                LatLng coordinate = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                CameraUpdate location = CameraUpdateFactory.newLatLngZoom(coordinate, 13);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinate));
                googleMap.animateCamera(location);
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(coordinate)
                        .title("Marker"));
            }
        });

        TextView arrivalTimeView = (TextView)view.findViewById(R.id.arrival_time);
        arrivalTimeView.setText(calculateArivalDueTime(mLocation.getArrivalTime()) + "");
        TextView locationView = (TextView)view.findViewById(R.id.location);
        locationView.setText(mLocation.getName());
        TextView latitudeView = (TextView)view.findViewById(R.id.latitude);
        latitudeView.setText(mLocation.getLatitude() + "");
        TextView longitudeView = (TextView)view.findViewById(R.id.longitude);
        longitudeView.setText(mLocation.getLongitude() + "");
        TextView addressView = (TextView)view.findViewById(R.id.address );
        addressView.setText(mLocation.getAddress());

        return view;
    }

    private String calculateArivalDueTime(String arrivalTime) {
        int dueMillis = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try {
            date = (Date)formatter.parse(arrivalTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1 + "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        long millis = cal.getTimeInMillis();

        dueMillis = (int)(millis - System.currentTimeMillis());
        int hrs = dueMillis / (60 * 60 * 1000);
        int mins = (dueMillis % (60 * 60 * 1000))/(1000 * 60);
        int secs = ((dueMillis % (60 * 60 * 1000))%(1000 * 60))/1000;
        return hrs >= 1 ? hrs + " Hrs " + mins + "Mins" : mins >= 1 ? mins + "Mins" + secs + " Secs" : secs + "Secs";
    }

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
        //outState.putParcelable(Location.LOCATION_KEY, mLocation);
    }

    @Override
    public void onStart() {
        super.onStart();

        //mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mMapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        //mMapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        mMapView = null;
    }
}
