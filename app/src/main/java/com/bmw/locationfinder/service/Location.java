package com.bmw.locationfinder.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class for Location parameters
 */
public class Location implements Parcelable {
    public static final String LOCATION_KEY = "locations_data_key";
    public static final String ID_NAME = "ID";
    public static final String NAME = "Name";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String ADDRESS = "Address";
    public static final String ARRIVAL_TIMES = "ArrivalTime";

    private int ID;
    private String Name;
    private double Latitude;
    private double Longitude;
    private String Address;
    private String ArrivalTime;
    private long distanceToDest = -1; // -1 means, not yet calculated

    /**
     * No args constructor for use in serialization
     *
     */
    public Location() {
    }

    /**
     *
     * @param arrivalTime
     * @param address
     * @param name
     * @param longitude
     * @param latitude
     * @param iD
     */
    public Location(int iD, String name, double latitude, double longitude, String address, String arrivalTime) {
        this.ID = iD;
        this.Name = name;
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Address = address;
        this.ArrivalTime = arrivalTime;
    }

    public Location(Parcel source) {
        ID = source.readInt();
        Name = source.readString();
        Latitude = source.readDouble();
        Longitude = source.readDouble();
        Address = source.readString();
        ArrivalTime = source.readString();
        distanceToDest = source.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(Name);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
        dest.writeString(Address);
        dest.writeString(ArrivalTime);
        dest.writeLong(distanceToDest);
    }

    /**
     *
     * @return
     * The ID
     */
    public int getID() {
        return ID;
    }

    /**
     *
     * @param iD
     * The ID
     */
    public void setID(int iD) {
        this.ID = iD;
    }

    /**
     *
     * @return
     * The Name
     */
    public String getName() {
        return Name;
    }

    /**
     *
     * @param name
     * The Name
     */
    public void setName(String name) {
        this.Name = name;
    }

    /**
     *
     * @return
     * The Latitude
     */
    public double getLatitude() {
        return Latitude;
    }

    /**
     *
     * @param latitude
     * The Latitude
     */
    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    /**
     *
     * @return
     * The Longitude
     */
    public double getLongitude() {
        return Longitude;
    }

    /**
     *
     * @param longitude
     * The Longitude
     */
    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    /**
     *
     * @return
     * The Address
     */
    public String getAddress() {
        return Address;
    }

    /**
     *
     * @param address
     * The Address
     */
    public void setAddress(String address) {
        this.Address = address;
    }

    /**
     *
     * @return
     * The ArrivalTime
     */
    public String getArrivalTime() {
        return ArrivalTime;
    }

    /**
     *
     * @param arrivalTime
     * The ArrivalTime
     */
    public void setArrivalTime(String arrivalTime) {
        this.ArrivalTime = arrivalTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[0];
        }
    };


    public long getDistanceToDest() {
        return distanceToDest;
    }

    public void setDistanceToDest(long distanceToDest) {
        this.distanceToDest = distanceToDest;
    }

}