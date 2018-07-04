package com.example.alex.gismasterapp;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * Класс-утилита для работы с изображениями.
 */
public class Utils {

    /**
     * Устанавливает изображение в {@link ImageView} с помощью {@link Glide} по ссылке (URL)
     * @param context контекст {@link android.app.Activity}
     * @param view {@link ImageView}, к которому ведется привязка изображения
     * @param width новая ширина изображения
     * @param height новая высота изображения
     * @param imgURL ссылка на изображение
     */
    public static void setImageByURL(Context context, ImageView view, int width, int height, String imgURL){
        Glide.with(context)
                .load(imgURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.googleg_disabled_color_18)
                        .override(width,height))
                .into(view);
    }

    /**
     * Устанавливает изображение в {@link ImageView} с помощью {@link Glide} по id ресурса.
     * @param context контекст {@link android.app.Activity}
     * @param view {@link ImageView}, к которому ведется привязка изображения
     * @param width новая ширина изображения
     * @param height новая высота изображения
     * @param id идентификатор ресурса
     */
    public static void setImageByResource(ImageView view, int id, int width, int height, Context context){
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.googleg_disabled_color_18)
                        .override(width,height))
                .into(view);
    }

}
