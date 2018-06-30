package com.example.alex.gismasterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherCurrentInfo implements Parcelable {
    @SerializedName("weatherPart")
    @Expose
    private WeatherPart weatherPart;
    @SerializedName("sunriseTime")
    @Expose
    private String sunriseTime;
    @SerializedName("sunsetTime")
    @Expose
    private String sunsetTime;
    @SerializedName("coord")
    @Expose
    private CityInfo cityInfo;
    public final static Creator<WeatherCurrentInfo> CREATOR = new Creator<WeatherCurrentInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public WeatherCurrentInfo createFromParcel(Parcel in) {
            return new WeatherCurrentInfo(in);
        }

        public WeatherCurrentInfo[] newArray(int size) {
            return (new WeatherCurrentInfo[size]);
        }

    }
            ;

    protected WeatherCurrentInfo(Parcel in) {
        this.weatherPart = ((WeatherPart) in.readValue((WeatherPart.class.getClassLoader())));
        this.sunriseTime = ((String) in.readValue((String.class.getClassLoader())));
        this.sunsetTime = ((String) in.readValue((String.class.getClassLoader())));
        this.cityInfo = ((CityInfo) in.readValue((CityInfo.class.getClassLoader())));
    }

    public WeatherCurrentInfo() {
    }

    public WeatherPart getWeatherPart() {
        return weatherPart;
    }

    public void setWeatherPart(WeatherPart weatherPart) {
        this.weatherPart = weatherPart;
    }

    public String getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(String sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(String sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public CityInfo getCoord() {
        return cityInfo;
    }

    public void setCoord(CityInfo coord) {
        this.cityInfo = coord;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(weatherPart);
        dest.writeValue(sunriseTime);
        dest.writeValue(sunsetTime);
        dest.writeValue(cityInfo);
    }

    public int describeContents() {
        return 0;
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WeatherCurrentInfo){
            WeatherCurrentInfo toCompare = (WeatherCurrentInfo) obj;
            return this.cityInfo.equals(toCompare.getCoord());
        }
        return false;
    }
}
