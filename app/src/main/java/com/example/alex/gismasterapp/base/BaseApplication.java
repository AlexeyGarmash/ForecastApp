package com.example.alex.gismasterapp.base;

import android.app.Application;

import io.realm.Realm;

/**
 * Базовый класс, инициализирующий глобальные действия
 * такие как, инициализация {@link Realm} DataBase.
 *
 * @author Alex
 * @version 1.0
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
