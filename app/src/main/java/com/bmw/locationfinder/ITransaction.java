package com.bmw.locationfinder;

import android.os.Bundle;

/**
 * ITransaction is for handling transaction of Fragments in LocationActivity.
 * This Interface removes the dependency among the Fragments.
 */
public interface ITransaction {
    enum TransactionType {
        LOCATION_LIST_VIEW, LOCATION_MAP_VIEW;
    }
    void onTransact(TransactionType action, Bundle data);
}
