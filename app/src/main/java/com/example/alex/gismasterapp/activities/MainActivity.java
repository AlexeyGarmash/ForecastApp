package com.example.alex.gismasterapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.adapters.HistoryAdapter;
import com.example.alex.gismasterapp.models.CityInfo;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;
import com.example.alex.gismasterapp.realm.RealmDb;
import com.example.alex.gismasterapp.realm.models.CityInfoRealm;
import com.example.alex.gismasterapp.retrofit.AppWeatherService;
import com.example.alex.gismasterapp.retrofit.ServiceUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;


/**
 * Класс MainActivity служит для обработки и отображения информации
 * о текущей погоде вместе с локальной историей запросов по городам.
 *
 * @author Alex
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    private boolean already;
    public static String TAG = "MainActivity";
    public static String LAT_LON_ADDRESS_DATA;

    private static final int UPDATE_WEATHER = 1;
    private static final int DATABASE_DOWNLOAD = 2;
    private static final int ADD_TO_LIST = 3;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextViewListEmpty;
    private ProgressBar mProgressBarStatus;
    private Switch mSwitch;
    private Toolbar toolbar;
    private HistoryAdapter mHistoryAdapter;

    private Gson gson;
    private CompositeDisposable disposable = new CompositeDisposable();

    private AppWeatherService mAppWeatherService;

    private String address = null;

    private List<WeatherCurrentInfo> currentInfos;


    public static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;

    /**
     * Метод используется при создании активити
     * во время холодного старта. Служит для инициализации переменных.
     *
     * @param savedInstanceState отвечает за сохранениние состояния активити (null если впервые или при перевороте).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setAutoCompleteFragment();

        already = false;


        mProgressBarStatus = findViewById(R.id.progressBarStatus);
        mRecyclerView = findViewById(R.id.rvHistory);
        mTextViewListEmpty = findViewById(R.id.tvListEmpty);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        currentInfos = new ArrayList<>();
        mHistoryAdapter = new HistoryAdapter(currentInfos, this);
        mRecyclerView.setAdapter(mHistoryAdapter);


        mHistoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mHistoryAdapter.getItemCount() == 0) {
                    mTextViewListEmpty.setVisibility(View.VISIBLE);
                } else {
                    mTextViewListEmpty.setVisibility(View.INVISIBLE);
                }
            }
        });
        gson = new Gson();


        //new DownloadCities().execute();
        setRefreshLayout();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString("url_text", "http://192.168.1.106:3000");
        try {
            mAppWeatherService = ServiceUtils.getService(baseUrl);
            //ServiceUtils.setNewUrl(baseUrl);
        } catch (Exception ex) {
            showSnack(ex.getMessage());
        }

    }


    /**
     * Метод используется для установки адреса сервера с настроек приложения и загрузки с БД данных
     * про уже ранее найденные города.
     */
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString("url_text", "http://192.168.1.106:3000");
        try {
            mAppWeatherService = ServiceUtils.getService(baseUrl);
            //ServiceUtils.setNewUrl(baseUrl);
        } catch (Exception ex) {
            showSnack(ex.getMessage());
        }

        readDatabase();
    }

    /**
     * Метод служит для инициализации и установки параметров
     * {@link SwipeRefreshLayout}, необходимого для обновления информации по свайпу сверху вниз.
     */
    private void setRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayoutMain);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                readDatabase();
            }
        });
    }

    /**
     * Метод служит для инициализации и установки параметров
     * {@link PlaceAutocompleteFragment}, необходимого для поиска адреса
     * по введенному названию города пользователя.
     */
    private void setAutoCompleteFragment() {
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Найти место");
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.

                Log.i(TAG, "Place: " + place.getName());//get place details here
                address = place.getAddress().toString();
                //new GetCoordinates().execute(address.replace(" ", "+"));
                mProgressBarStatus.setVisibility(View.VISIBLE);
                sendPostLocation(address);
                autocompleteFragment.setText("");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);

            }
        });
    }

    /**
     * Метод, посылающий запрос на сервер для получения
     * координат по полученному адресу.
     *
     * @param addr точный адрес введенного пользователем места
     */
    private void sendPostLocation(String addr) {
        disposable.add(mAppWeatherService.getLocation(new CityInfo(addr))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CityInfo>() {

                    @Override
                    public void onSuccess(CityInfo cityInfo) {
                        sendPostCurrentWeather(cityInfo, ADD_TO_LIST, 0);
                        RealmDb.insertRealmModel(cityInfo.getCityInfoRealm());
                        mProgressBarStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnack(e.getMessage());
                        mProgressBarStatus.setVisibility(View.INVISIBLE);
                    }
                }));
    }

    /**
     * Посылает запрос на сервер для получения текущей (на момент API)
     * статуса погоды на местности.
     *
     * @param cityInfo содержит координаты, нужные для получения погоды
     * @param type     тип обращения к методу: UPDATE_WEATHER - обновить данные списка,
     *                 ADD_TO_LIST - добавить в список,
     *                 DATABASE_DOWNLOAD - загрузить с БД
     * @param index    индекс города для обновления в списке
     */
    private void sendPostCurrentWeather(final CityInfo cityInfo, final int type, final int index) {
        mProgressBarStatus.setVisibility(View.VISIBLE);
        disposable.add(mAppWeatherService.getCurrentWeather(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherCurrentInfo>() {

                    @Override
                    public void onSuccess(WeatherCurrentInfo currentWeatherInfo) {

                        switch (type) {
                            case UPDATE_WEATHER:
                                updateCurrentWeather(currentWeatherInfo, index);
                                break;
                            case ADD_TO_LIST:
                                addToList(currentWeatherInfo, cityInfo);
                                break;
                            case DATABASE_DOWNLOAD:
                                addFromDatabase(currentWeatherInfo, cityInfo);
                                break;
                        }
                        mProgressBarStatus.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnack(e.getMessage());
                        mProgressBarStatus.setVisibility(View.INVISIBLE);
                    }
                }));
    }

    /**
     * Получает данные из БД, организовует передачу считанных координат на сервер для получения
     * погоды каждого из имеющихся городов.
     */
    private void readDatabase() {
        mProgressBarStatus.setVisibility(View.VISIBLE);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentInfos.clear();
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    List<CityInfoRealm> models = realm.where(CityInfoRealm.class).findAll();
                    for (CityInfoRealm city : models) {
                        sendPostCurrentWeather(city.getCityInfo(), DATABASE_DOWNLOAD, 0);
                        Log.i(TAG, "ID = " + city.getCityInfo().getId());
                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                    if (mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBarStatus.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Добавляет позицию из БД в список и настраивает поля.
     *
     * @param currentWeatherInfo полученный ответ от запроса на получение текущей погоды
     * @param cityInfo           полученный ответ от запроса на получение информации о городе
     */
    private void addFromDatabase(WeatherCurrentInfo currentWeatherInfo, CityInfo cityInfo) {
        currentWeatherInfo.getCoord().setCityName(cityInfo.getCityName());
        currentWeatherInfo.getCoord().setCountryName(cityInfo.getCountryName());
        currentWeatherInfo.getCoord().setId(cityInfo.getId());
        currentInfos.add(currentWeatherInfo);
        mHistoryAdapter.notifyDataSetChanged();

    }

    /**
     * Обновляет index позицию в истории запросов.
     *
     * @param newCurrentWeatherInfo полученный ответ от запроса на обновление погоды по позиции в списке
     * @param index                 позиция места в списке
     */
    private void updateCurrentWeather(WeatherCurrentInfo newCurrentWeatherInfo, int index) {
        currentInfos.get(index).setNewCurrentWeather(newCurrentWeatherInfo);
    }

    /**
     * Добавляет или не добавляет в список место в зависимости от наличия его в списке.
     *
     * @param currentWeatherInfo полученный ответ от запроса на получение текущей погоды
     * @param cityInfo           полученный ответ от запроса на получение информации о городе
     */
    private void addToList(WeatherCurrentInfo currentWeatherInfo, CityInfo cityInfo) {
        WeatherCurrentInfo currentInfo = currentWeatherInfo;
        currentInfo.getCoord().setCityName(cityInfo.getCityName());
        currentInfo.getCoord().setCountryName(cityInfo.getCountryName());

        boolean isContain = currentInfos.contains(currentInfo);
        if (!isContain) {
            currentInfos.add(currentInfo);
        }
        mHistoryAdapter.notifyDataSetChanged();
    }


    /**
     * Показывает {@link Snackbar} с заданным сообщением.
     *
     * @param message сообщение (инфо, ошибка и т.п)
     */
    private void showSnack(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }


    /**
     * Создает меню для {@link android.support.v7.app.ActionBar} из ресурса.
     *
     * @param menu меню
     * @return успешное создание меню.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSwitch = (Switch) menu.findItem(R.id.myswitch)
                .getActionView().findViewById(R.id.switchForActionBar);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplication(), "Ночь", Toast.LENGTH_SHORT)
                            .show();
                    setTheme(R.style.AppThemeDark);
                } else {
                    Toast.makeText(getApplication(), "День", Toast.LENGTH_SHORT)
                            .show();
                    setTheme(R.style.AppTheme);
                }
            }
        });
        return true;
    }

    /**
     * Определят нажатия на пунктах меню.
     *
     * @param item пункт меню, который выбрали.
     * @return успешный факт нажатия на элемент меню.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Вызывается при уничтожении активити. Освобождает ресурсы.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }


}
