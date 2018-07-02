package com.example.alex.gismasterapp.realm.models;



import com.example.alex.gismasterapp.models.CityInfo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CityInfoRealm extends RealmObject {

    @PrimaryKey
    private String id;

    private double lat;

    private double lon;

    private String status;

    private String cityName;

    private String countryName;

    private String address;

    public CityInfoRealm() {

    }

    public CityInfoRealm(String id, double lat, double lon, String status, String cityName, String countryName, String address) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.status = status;
        this.cityName = cityName;
        this.countryName = countryName;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CityInfo getCityInfo(){
        CityInfo cityInfo = new CityInfo();
        cityInfo.setAddress(address);
        cityInfo.setCityName(cityName);
        cityInfo.setId(id);
        cityInfo.setCountryName(countryName);
        cityInfo.setLat(lat);
        cityInfo.setLon(lon);
        cityInfo.setStatus(status);
        return cityInfo;
    }
}
