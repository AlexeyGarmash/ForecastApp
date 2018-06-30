package com.example.alex.gismasterapp;


import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class Utils {

    public static void setImageByURL(Context context, ImageView view, int width, int height, String imgURL){
        Glide.with(context)
                .load(imgURL)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.googleg_disabled_color_18)
                        .override(width,height))
                .into(view);
    }

    public static void setImageByResource(ImageView view, int id, int width, int height, Context context){
        Glide.with(context)
                .load(id)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.googleg_disabled_color_18)
                        .override(width,height))
                .into(view);
    }

}
