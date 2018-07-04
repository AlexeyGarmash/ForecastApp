package com.example.alex.gismasterapp.models;

/**
 * Класс-модель информации о прогнозе погоды в заданном месте.
 */
public class WeatherInfo {
    private String timeShort;

    private String dateShort;

    private String dayWeek;

    private String datetime;

    private WeatherPart weatherPart;

    public WeatherPart getWeatherPart() {
        return weatherPart;
    }

    public void setWeatherPart(WeatherPart weatherPart) {
        this.weatherPart = weatherPart;
    }


    public String getTimeShort() {
        return timeShort;
    }

    public void setTimeShort(String timeShort) {
        this.timeShort = timeShort;
    }


    public String getDateShort() {
        return dateShort;
    }

    public void setDateShort(String dateShort) {
        this.dateShort = dateShort;
    }


    public String getDayWeek() {
        return dayWeek;
    }

    public void setDayWeek(String dayWeek) {
        this.dayWeek = dayWeek;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

}
