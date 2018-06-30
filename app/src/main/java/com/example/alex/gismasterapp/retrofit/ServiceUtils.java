package com.example.alex.gismasterapp.retrofit;

public class ServiceUtils {
    private ServiceUtils() {}




    public static AppWeatherService getService(String baseUrl) {

        return RetrofitClient.getClient(baseUrl).create(AppWeatherService.class);
    }


    public static void setNewUrl(String newUrl){
        RetrofitClient.setNewUrl(newUrl);
    }


}
