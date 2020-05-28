package com.sgbit.androidremoteaccess.model;

import java.util.Date;

/**
 * Created by padmajeet on 4/15/19.
 */

public class Location {

    private String mobileNumber;
    private double latitude;
    private double longitude;
    private Date createdate=new Date();
    private Date modifieddate=new Date();
    public String getMobileNumber() {
        return mobileNumber;
    }
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    public Date getCreatedate() {
        return createdate;
    }
    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }
    public Date getModifieddate() {
        return modifieddate;
    }
    public void setModifieddate(Date modifieddate) {
        this.modifieddate = modifieddate;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
