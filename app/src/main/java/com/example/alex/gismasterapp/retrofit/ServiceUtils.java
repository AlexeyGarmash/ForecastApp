package com.example.alex.gismasterapp.retrofit;

/**
 * Класс-утилита для получения сервиса,
 * с помощью которого производятся запросы к серверу.
 */
public class ServiceUtils {
    private ServiceUtils() {
    }

    /**
     * Получает рабочий обьект {@link AppWeatherService} с помощью {@link RetrofitClient}
     * @param baseUrl неизменяемая часть пути к серверу
     * @return обьект {@link AppWeatherService}
     */
    public static AppWeatherService getService(String baseUrl) {

        return RetrofitClient.getClient(baseUrl).create(AppWeatherService.class);
    }


    public static void setNewUrl(String newUrl) {
        RetrofitClient.setNewUrl(newUrl);
    }


}
