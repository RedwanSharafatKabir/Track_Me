package com.gpspayroll.track_me.ModelClasses;

public class OfficeLocationInfo {
    private String lattitude;
    private String longitude;
    private String placeName;

    public OfficeLocationInfo(String lattitude, String longitude, String placeName) {
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }

    public OfficeLocationInfo() {
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}
