package com.example.alex.gismasterapp.retrofit;

import com.example.alex.gismasterapp.models.CityInfo;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;
import com.example.alex.gismasterapp.models.WeatherInfo;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AppWeatherService {
    @POST("/five")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<WeatherInfo[]> getForecast(@Body CityInfo post);

    @POST("/current")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<WeatherCurrentInfo> getCurrentWeather(@Body CityInfo post);

    @POST("/location")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<CityInfo> getLocation(@Body CityInfo post);
}
