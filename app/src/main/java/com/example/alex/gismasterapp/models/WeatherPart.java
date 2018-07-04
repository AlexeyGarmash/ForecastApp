package com.example.alex.gismasterapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.alex.gismasterapp.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Класс-модель, содержащий погодную состовляющую {@link WeatherCurrentInfo} и {@link WeatherInfo}
 */
public class WeatherPart implements Parcelable {

    @SerializedName("descrip")
    @Expose
    private String descrip;
    @SerializedName("tempr")
    @Expose
    private double tempr;
    @SerializedName("hum")
    @Expose
    private int hum;
    @SerializedName("speedWind")
    @Expose
    private double speedWind;
    @SerializedName("directWind")
    @Expose
    private String directWind;
    @SerializedName("weatherIconURL")
    @Expose
    private String weatherIconURL;
    public final static Creator<WeatherPart> CREATOR = new Creator<WeatherPart>() {


        @SuppressWarnings({
                "unchecked"
        })
        public WeatherPart createFromParcel(Parcel in) {
            return new WeatherPart(in);
        }

        public WeatherPart[] newArray(int size) {
            return (new WeatherPart[size]);
        }

    };

    protected WeatherPart(Parcel in) {
        this.descrip = ((String) in.readValue((String.class.getClassLoader())));
        this.tempr = ((double) in.readValue((double.class.getClassLoader())));
        this.hum = ((int) in.readValue((int.class.getClassLoader())));
        this.speedWind = ((double) in.readValue((double.class.getClassLoader())));
        this.directWind = ((String) in.readValue((String.class.getClassLoader())));
        this.weatherIconURL = ((String) in.readValue((String.class.getClassLoader())));
    }

    public WeatherPart() {
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public double getTempr() {
        return tempr;
    }

    public long getTemprInt() {
        return Math.round(tempr);
    }

    public String getFormatedTemp() {
        return String.format("%s \u2103", String.valueOf(Math.round(tempr)));
    }

    public void setTempr(double tempr) {
        this.tempr = tempr;
    }

    public int getHum() {
        return hum;
    }

    public String getFormatedHumidity() {
        return String.valueOf(hum) + "%";
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public double getSpeedWind() {
        return speedWind;
    }

    public void setSpeedWind(int speedWind) {
        this.speedWind = speedWind;
    }

    public String getDirectWind() {
        return directWind;
    }

    //["С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "Ю", "СЗ", "С"];
    public int getWindResource() {
        switch (getDirectWind()) {
            case "С":
                return R.drawable.to_south;
            case "СВ":
                return R.drawable.to_south_west;
            case "СЗ":
                return R.drawable.to_south_east;
            case "Ю":
                return R.drawable.to_north;
            case "ЮВ":
                return R.drawable.to_north_west;
            case "ЮЗ":
                return R.drawable.to_north_east;
            case "З":
                return R.drawable.to_east;
            case "В":
                return R.drawable.to_west;
            default:
                return 0;
        }

    }

    public void setDirectWind(String directWind) {
        this.directWind = directWind;
    }

    public String getWeatherIconURL() {
        return weatherIconURL;
    }

    public void setWeatherIconURL(String weatherIconURL) {
        this.weatherIconURL = weatherIconURL;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(descrip);
        dest.writeValue(tempr);
        dest.writeValue(hum);
        dest.writeValue(speedWind);
        dest.writeValue(directWind);
        dest.writeValue(weatherIconURL);
    }

    public int describeContents() {
        return 0;
    }

}
