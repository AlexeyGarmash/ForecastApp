package com.example.alex.gismasterapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.Utils;
import com.example.alex.gismasterapp.adapters.ForecastAdapter;
import com.example.alex.gismasterapp.models.CityInfo;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;
import com.example.alex.gismasterapp.models.WeatherInfo;
import com.example.alex.gismasterapp.retrofit.AppWeatherService;
import com.example.alex.gismasterapp.retrofit.ServiceUtils;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Класс ForecastActivity служит для обработки и отображения информации
 * о прогнозе погоды переданной ему местности.
 *
 * @author Alex
 * @version 1.0
 */
public class ForecastActivity extends AppCompatActivity {

    private AppWeatherService mAppWeatherService;

    private CompositeDisposable disposable = new CompositeDisposable();


    private WeatherCurrentInfo currentWeather;
    private String cityName = null;
    private String countryName = null;
    //Current weather info views
    private TextView mTextViewCity;
    private TextView mTextViewSunRise;
    private TextView mTextViewSunSet;
    private TextView mTextViewTemp;
    private TextView mTextViewDescription;
    private ImageView mImageViewWindDir;
    private ImageView mImageViewWeatherIcon;
    private TextView mTextViewWindDir;
    private TextView mTextViewWindSpeed;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    //Forecast five days info views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private Gson gson;
    private List<WeatherInfo> weathersThreeHours = new ArrayList<>();
    private List<WeatherInfo> weathersSixHours = new ArrayList<>();
    private List<WeatherInfo> weathersNineHours = new ArrayList<>();

    private Button mButtonOpenMap;
    private Spinner mSpinner;

    /**
     * Метод используется при создании активити
     * во время холодного старта. Служит для инициализации переменных.
     *
     * @param savedInstanceState отвечает за сохранениние состояния активити (null если впервые или при перевороте).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarForecast);
        setSupportActionBar(toolbar);

        currentWeather = getIntent().getExtras().getParcelable(MainActivity.LAT_LON_ADDRESS_DATA);
        cityName = currentWeather.getCoord().getCityName();
        countryName = currentWeather.getCoord().getCountryName();
        toolbar.setTitle(String.format("%s, %s", cityName, countryName));
        gson = new Gson();
        mAppWeatherService = ServiceUtils.getService("http://192.168.1.106:3000");

        findViewsCurrentWeatherId();

        setButton();
        setCurrentWeatherData();
        setRecyclerView();
        setSpinner();
        setRefreshLayout();


    }

    /**
     * Служит для инициализации и установки параметров
     * Button, необходимого для перехода к карте.
     */
    private void setButton() {
        mButtonOpenMap = findViewById(R.id.btnOpenMap);
        mButtonOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMap();
            }
        });
    }

    /**
     * Осуществляет переход к карте.
     */
    private void goToMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(MainActivity.LAT_LON_ADDRESS_DATA, currentWeather);
        startActivity(intent);
    }

    /**
     * Метод служит для инициализации и установки параметров
     * {@link SwipeRefreshLayout}, необходимого для обновления информации по свайпу сверху вниз.
     */
    private void setRefreshLayout() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSpinner.setSelection(0);
                sendPostCurrentWeather(currentWeather.getCoord());
                sendPostForecast(currentWeather.getCoord());
            }
        });
    }

    /**
     * Служит для установки текста в {@link TextView}.
     */
    private void setTextData(TextView textView, String data) {
        textView.setText(data);
    }

    /**
     * Инициализирует Views по их ID.
     */
    private void findViewsCurrentWeatherId() {
        mTextViewCity = findViewById(R.id.tvCityName);
        mTextViewSunRise = findViewById(R.id.tvSunRise);
        mTextViewSunSet = findViewById(R.id.tvSunSet);
        mTextViewTemp = findViewById(R.id.tvTemp);
        mTextViewDescription = findViewById(R.id.tvDescription);
        mTextViewWindDir = findViewById(R.id.tvWind_description_current);
        mTextViewWindSpeed = findViewById(R.id.tvWind_speed_current);
        mImageViewWindDir = findViewById(R.id.wind_direction_icon_current);
        mImageViewWeatherIcon = findViewById(R.id.ivWeatherIcon);
    }

    /**
     * Заполняет {@link View}s необходимыми данными из полученной текущей погоды.
     */
    private void setCurrentWeatherData() {
        setTextData(mTextViewCity, currentWeather.getCoord().getCityName());
        setTextData(mTextViewSunRise, currentWeather.getSunriseTime());
        setTextData(mTextViewSunSet, currentWeather.getSunsetTime());
        setTextData(mTextViewTemp, String.valueOf(currentWeather.getWeatherPart().getTemprInt()));
        setTextData(mTextViewDescription, currentWeather.getWeatherPart().getDescrip());
        setTextData(mTextViewWindDir, currentWeather.getWeatherPart().getDirectWind());
        setTextData(mTextViewWindSpeed, String.valueOf(currentWeather.getWeatherPart().getSpeedWind()));
        Utils.setImageByResource(mImageViewWindDir, currentWeather.getWeatherPart().getWindResource(), 60, 60, this);
        Utils.setImageByURL(this, mImageViewWeatherIcon, 60, 60, currentWeather.getWeatherPart().getWeatherIconURL());
    }

    /**
     * Метод служит для инициализации и установки параметров
     * {@link Spinner}ner, необходимого для выбора интервала времени между прогнозами.
     */
    private void setSpinner() {
        mSpinner = findViewById(R.id.spinner);
        ArrayAdapter<?> adapter =
                ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateWeather(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Устанавливает для списка новые данные в зависимомти от выбранного интервала времени в {@link Spinner}ner.
     *
     * @param i идентификатор времени:
     *          0 - 3ч
     *          1 - 6ч
     *          2 - 9ч
     */
    private void updateWeather(int i) {

        switch (i) {
            case 0:
                setupAdapter(weathersThreeHours);
                break;
            case 1:
                setupAdapter(weathersSixHours);
                break;
            case 2:
                setupAdapter(weathersNineHours);
                break;
        }
    }

    /**
     * Метод служит для установки адаптера для {@link RecyclerView}erView.
     */
    private void setupAdapter(List<WeatherInfo> listWeather) {
        mForecastAdapter = new ForecastAdapter(listWeather, this);
        mRecyclerView.setAdapter(mForecastAdapter);
    }

    /**
     * Метод служит для инициализации и установки параметров
     * {@link RecyclerView}ew, необходимого для отображения списка с элементами прогноза погоды.
     */
    private void setRecyclerView() {
        mRecyclerView = findViewById(R.id.rvForecast);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        setupAdapter(weathersThreeHours);
    }


    /**
     * Посылает запрос на сервер для получения прогноза
     * погоды на местности и заполняет список прогноза погоды.
     *
     * @param cityInfo содержит координаты, нужные для получения погоды
     */
    private void sendPostForecast(CityInfo cityInfo) {

        disposable.add(mAppWeatherService.getForecast(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherInfo[]>() {

                    @Override
                    public void onSuccess(WeatherInfo[] forecastInfo) {
                        getForecastFromArray(forecastInfo);
                        setupAdapter(weathersThreeHours);
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                        showSnack(e.getMessage());
                    }
                }));
    }

    /**
     * Посылает запрос на сервер для получения текущей (на момент API)
     * статуса погоды на местности и заполянет соответсвующими данными
     * окно текущей погоды.
     *
     * @param cityInfo содержит координаты, нужные для получения погоды
     */
    private void sendPostCurrentWeather(CityInfo cityInfo) {

        disposable.add(mAppWeatherService.getCurrentWeather(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherCurrentInfo>() {

                    @Override
                    public void onSuccess(WeatherCurrentInfo response) {
                        WeatherCurrentInfo currentInfo = response;
                        currentInfo.getCoord().setCityName(cityName);
                        currentInfo.getCoord().setCountryName(countryName);
                        currentWeather = currentInfo;
                        setCurrentWeatherData();
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mSwipeRefreshLayout.isRefreshing())
                            mSwipeRefreshLayout.setRefreshing(false);
                        showSnack(e.getMessage());
                    }
                }));
    }


    /**
     * Преобразовует массив прогноза погоды в список, нужный для адаптера.
     *
     * @param weathersArr массив прогноза погоды, полученный на запрос о прогнозе погоды на 5 дней.
     */
    private void getForecastFromArray(WeatherInfo[] weathersArr) {
        weathersThreeHours = Arrays.asList(weathersArr);
        weathersSixHours = new ArrayList<>();
        weathersNineHours = new ArrayList<>();
        int k = 0;
        int p = 0;
        int size = weathersThreeHours.size();
        for (int i = 0; i < size; i++) {


            if (p > size - 1 || k > size - 1) {
                break;
            }
            weathersSixHours.add(weathersThreeHours.get(k));

            weathersNineHours.add(weathersThreeHours.get(p));


            k += 2;
            p += 3;
        }
    }

    /**
     * Показывает {@link Snackbar}Bar с заданным сообщением.
     *
     * @param message сообщение (инфо, ошибка и т.п)
     */
    private void showSnack(String message) {
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }


    /**
     * Вызывается при уничтожении активити. Освобождает ресурсы.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    /**
     * Метод используется для установки адреса сервера с настроек
     * приложения и обращение к серверу для получения данных
     * прогноза на 5 дней.
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
        sendPostForecast(currentWeather.getCoord());
    }
}
