package com.bmw.locationfinder.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bmw.locationfinder.ITransaction;
import com.bmw.locationfinder.R;
import com.bmw.locationfinder.service.ILocationService;
import com.bmw.locationfinder.service.Location;
import com.bmw.locationfinder.service.LocationService;
import com.google.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * LocationListFragment hold list of Locations.
 */
public class LocationListFragment extends Fragment implements ILocationService.OnDataAvailableCallback {
    public static final int MY_PERMISSIONS_FOR_LOCATION_ACCESS = 1;
    private static final String TAG = LocationListFragment.class.getSimpleName();
    Location[] mLocations;
    private RecyclerView mRecyclerView;
    private LocationAdapter mAdapter;
    private ITransaction mITransaction;
    private View mRootView;
    private View mNoConnectionLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private void queryLocationData() {
        //Don't trigger Server call, if Location data is already available.
        if(mLocations != null) {
            return;
        }
        if(checkInternetAvailability()) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mNoConnectionLayout.setVisibility(View.GONE);
            showProgressDialog();
            ILocationService service = new LocationService(this.getContext());
            service.getLocations(this);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mNoConnectionLayout.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_list_layout, container, false);
        mRecyclerView = (RecyclerView)mRootView.findViewById(R.id.list);
        mAdapter = new LocationAdapter();
        LinearLayoutManager lManager = new LinearLayoutManager(getContext());
        lManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(lManager);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, false);
        mRecyclerView.addItemDecoration(itemDecoration);
        mNoConnectionLayout = mRootView.findViewById(R.id.no_connection);
        Button retry = (Button) mRootView.findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetAvailability()) {
                    queryLocationData();
                }
            }
        });

        //Don't trigger Server call, if Location data is already available.
        if(savedInstanceState != null) {
            mLocations = (Location[]) savedInstanceState.getParcelableArray(Location.LOCATION_KEY);
        } else {
            queryLocationData();
        }
        mRecyclerView.setAdapter(mAdapter);
        return mRootView;
    }

    /**
     * Checks if Internet is available or not.
     * @return
     */
    private boolean checkInternetAvailability() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.d(TAG, "checkInternetAvailability: " + isConnected);
        return isConnected;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.sort_options_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortByArrivalTime:
                sortByArrivalTime();
                break;
            case R.id.sortByDistance:
                triggerSortByDistance();
                break;
            case R.id.sortByName:
                sortByName();
                break;
        }
        return false;
    }

    private void sortByName() {
        ArrayList<Location> locationList = new ArrayList<Location>();
        for (Location location: mLocations) {
            locationList.add(location);
        }
        Collections.sort(locationList, new Comparator<Location>() {
            @Override
            public int compare(Location lhs, Location rhs) {
                //compare names
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        mLocations = locationList.toArray(mLocations);
        mAdapter.notifyDataSetChanged();
    }

    public void triggerSortByDistance() {
        if(mLocations[0].getDistanceToDest() == -1) { // -1 means, Distance wasn't queried yet.
            ILocationService service = new LocationService(this.getContext());
            LatLng ltdLng = getCurrentLatLng();
            if(ltdLng == null) {
                Toast.makeText(getContext(), "Please enable GPS", Toast.LENGTH_LONG).show();
                return;
            }
            showProgressDialog();

            //Calculate distance from the current locations to the originated locations
            service.updateDistancesToDestination(mLocations, ltdLng, new ILocationService.OnDataAvailableCallback() {
                @Override
                public void onSuccess(Location[] data) {
                    mLocations = data;
                    sortByDistance();
                    dismissProgressDialog();
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        } else {
            sortByDistance();
        }
    }

    public void sortByDistance() {
        ArrayList<Location> locationList = new ArrayList<Location>();
        for (Location location: mLocations) {
            locationList.add(location);
        }
        Collections.sort(locationList, new Comparator<Location>() {
            @Override
            public int compare(Location lhs, Location rhs) {
                //compare Distances
                return (int)(lhs.getDistanceToDest() - rhs.getDistanceToDest());
            }
        });
        mLocations = locationList.toArray(mLocations);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Returns current user location coordinates
     * @return
     */
    private LatLng getCurrentLatLng() {
        LocationManager locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object

        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria

        String provider = locationManager.getBestProvider(criteria, false);

        //From the 6.0, permissions to be checked dynamically
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            android.location.Location location = locationManager.getLastKnownLocation(provider);

            return location == null ? null : new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            // Permission wasn't granted already. Request user for permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_FOR_LOCATION_ACCESS);

            return  null;
        }
    }

    private void sortByArrivalTime() {
        ArrayList<Location> locationList = new ArrayList<Location>();
        for (Location location: mLocations) {
            locationList.add(location);
        }
        Collections.sort(locationList, new Comparator<Location>() {
                    @Override
                    public int compare(Location lhs, Location rhs) {
                        long lhsMillis = toMillis(lhs.getArrivalTime());
                        long rhsMillis = toMillis(rhs.getArrivalTime());
                        //Sorting by arrival time
                        return (int)(lhsMillis - rhsMillis);
                    }
                });
        mLocations = locationList.toArray(mLocations);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Converts Date from string to Milli Seconds
     * @param dateString Data in "yyyy-MM-dd'T'HH:mm:ss.SSS" formate
     * @return Millis seconds
     */
    private long toMillis(String dateString) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try {
            date = (Date)formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);
        return cal.getTimeInMillis();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof ITransaction) {
            mITransaction = (ITransaction) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mITransaction = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(Location.LOCATION_KEY, mLocations);
    }
    @Override
    public void onSuccess(Location[] data) {
        dismissProgressDialog();
        mLocations = data;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void dismissProgressDialog() {
        DialogFragment dialog = (DialogFragment)getFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG);
        if(dialog != null) {
            dialog.dismissAllowingStateLoss();
        }
    }

    private void showProgressDialog() {
        ProgressDialogFragment dialog = new ProgressDialogFragment();
        dialog.show(getFragmentManager(), ProgressDialogFragment.TAG);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        //To-do: Errors to be handled
    }

    private class LocationAdapter extends RecyclerView.Adapter<LocationViewHolder> {
        public LocationAdapter() {
        }

        @Override
        public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LocationListFragment.this.getContext())
                    .inflate(R.layout.location_list_item, parent, false);
            return new LocationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LocationViewHolder holder, int position) {
            holder.setLocation(mLocations[position], position);
        }

        @Override
        public int getItemCount() {
            return mLocations == null ? 0 : mLocations.length ;
        }
    }

    private class LocationViewHolder extends RecyclerView.ViewHolder {
        private final View mItemView;
        private TextView mLocationView;
        private TextView mAddressView;

        public LocationViewHolder(View itemView) {
            super(itemView);
            mItemView = itemView;
            mLocationView = (TextView)itemView.findViewById(R.id.location);
            mAddressView = (TextView)itemView.findViewById(R.id.address);
        }

        public void setLocation(Location location, final int position) {
            mLocationView.setText(location.getName());
            mAddressView.setText(location.getAddress());
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLocationSelected(position);
                }
            });
        }
    }

    private void onLocationSelected(int position) {
        Location location = mLocations[position];
        Bundle data = new Bundle();
        data.putParcelable(Location.LOCATION_KEY, location);
        mITransaction.onTransact(ITransaction.TransactionType.LOCATION_MAP_VIEW, data);
    }


}
