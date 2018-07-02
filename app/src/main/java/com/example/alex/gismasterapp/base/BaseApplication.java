package com.example.alex.gismasterapp.base;

import android.app.Application;

import io.realm.Realm;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
