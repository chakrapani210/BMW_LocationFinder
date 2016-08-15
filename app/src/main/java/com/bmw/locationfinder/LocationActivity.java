package com.bmw.locationfinder;

import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.bmw.locationfinder.fragments.LocationListFragment;
import com.bmw.locationfinder.fragments.LocationMapFragment;
import com.bmw.locationfinder.service.Location;

/**
 * LocationActivity holds LocationListFragment and Location<apFragments
 */
public class LocationActivity extends AppCompatActivity implements ITransaction {

    private static final String TAG = LocationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        init();
    }

    @Override
    public void onTransact(TransactionType action, Bundle data) {
        switch (action) {
            case LOCATION_MAP_VIEW:
                Location location = data.getParcelable(Location.LOCATION_KEY);
                LocationMapFragment fragment = LocationMapFragment.getInstance(location);
                changeFragment(fragment);
        }
    }

    private void init() {
        if(getCurrentFragment() == null) {
            LocationListFragment fragment = new LocationListFragment();
            changeFragment(fragment);
        }
    }

    public void changeFragment(Fragment fragment) {

        if (fragment == null) {
            return;
        }
        String backStateName = fragment.getClass().getSimpleName();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.addToBackStack(backStateName).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment currentFragment = getCurrentFragment();

        Log.v(TAG, "onBackPressed ::" + currentFragment);

        if(null == currentFragment){
            finish();
        }
    }

    private Fragment getCurrentFragment() {
        Fragment mnFragment;
        mnFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        return mnFragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LocationListFragment.MY_PERMISSIONS_FOR_LOCATION_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationListFragment fragment = (LocationListFragment)getCurrentFragment();
                    fragment.triggerSortByDistance();
                }
                return;
            }
        }
    }
}
