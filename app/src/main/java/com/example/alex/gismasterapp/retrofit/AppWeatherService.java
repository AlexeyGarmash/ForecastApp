package com.example.alex.gismasterapp.retrofit;

import com.example.alex.gismasterapp.models.CityInfo;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;
import com.example.alex.gismasterapp.models.WeatherInfo;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Интерфейс для инициализации тела запросов к серверу с помощью {@link retrofit2.Retrofit} и RxJava
 */
public interface AppWeatherService {

    /**
     * Получает прогноз погоды в виде массива.
     *
     * @param post тело POST запроса
     * @return массив прогноза погоды
     */
    @POST("/five")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<WeatherInfo[]> getForecast(@Body CityInfo post);

    /**
     * Получает текущую погоду.
     *
     * @param post тело POST запроса
     * @return текущую погоду как обьект {@link WeatherCurrentInfo}
     */
    @POST("/current")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<WeatherCurrentInfo> getCurrentWeather(@Body CityInfo post);

    /**
     * Получает информацию о месте (координаты, название города (местности), страны, ID).
     *
     * @param post тело POST запроса
     * @return информацию о месте как обьект {@link CityInfo}
     */
    @POST("/location")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    Single<CityInfo> getLocation(@Body CityInfo post);

}
