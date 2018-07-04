package com.example.alex.gismasterapp.realm;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.alex.gismasterapp.models.CityInfo;
import com.example.alex.gismasterapp.realm.models.CityInfoRealm;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Класс для выполнения операций с БД {@link Realm}, такие как вставка или удаление элемента.
 */
public class RealmDb {

    /**
     * Добавляет обьект класса {@link CityInfoRealm} в БД {@link Realm}.
     * Если уже присутствует такое место в БД, то записи не дублируются.
     *
     * @param cityInfo добавляемое место (город) {@link CityInfoRealm}
     */
    public static void insertRealmModel(final CityInfoRealm cityInfo) {
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                realm.copyToRealmOrUpdate(cityInfo);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.i("Insert model", "Object " + cityInfo.getCityName() + " success!!!");
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("Insert model", "Object " + cityInfo.getCityName() + " error!!!");
                realm.close();
            }
        });
    }

    /**
     * Удаляет обьект класса {@link CityInfoRealm} с БД {@link Realm},
     * если он присутствует.
     *
     * @param cityInfo удаляемое место (город) {@link CityInfoRealm}
     */
    public static void removeRealmModel(final CityInfoRealm cityInfo) {
        final Realm realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CityInfoRealm> result = realm.where(CityInfoRealm.class).equalTo("id", cityInfo.getId()).findAll();
                result.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.i("Delete model", "Object " + cityInfo.getCityName() + cityInfo.getId() + " success!!!");
                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("Delete model", "Object " + cityInfo.getCityName() + cityInfo.getId() + " error!!! " + error.getMessage());
                realm.close();
            }
        });
    }
}
