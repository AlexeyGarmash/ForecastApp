package com.example.alex.gismasterapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    public static String LAT_LON_ADDRESS_DATA;

    private static final int  UPDATE_WEATHER = 1;
    private static final int  DATABASE_DOWNLOAD = 2;
    private static final int  ADD_TO_LIST = 3;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextViewListEmpty;
    private ProgressBar mProgressBarStatus;
    private Switch mSwitch;

    private HistoryAdapter mHistoryAdapter;

    private Gson gson;
    private CompositeDisposable disposable = new CompositeDisposable();

    private AppWeatherService mAppWeatherService;

    private String address = null;

    private List<WeatherCurrentInfo> currentInfos;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setAutoCompleteFragment();

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
                if(mHistoryAdapter.getItemCount()==0){
                    mTextViewListEmpty.setVisibility(View.VISIBLE);
                }
                else{
                    mTextViewListEmpty.setVisibility(View.INVISIBLE);
                }
            }
        });
        gson = new Gson();




        //new DownloadCities().execute();
        setRefreshLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString("url_text", "http://192.168.1.106:3000");
        try{
            mAppWeatherService = ServiceUtils.getService(baseUrl);
            //ServiceUtils.setNewUrl(baseUrl);
        }catch (Exception ex){
            showSnack(ex.getMessage());
        }
        readDatabase();
    }

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



    private void sendPostLocation(String addr) {
        disposable.add(mAppWeatherService.getLocation(new CityInfo(addr))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CityInfo>(){

                    @Override
                    public void onSuccess(CityInfo cityInfo) {
                        sendPostCurrentWeather(cityInfo, ADD_TO_LIST, 0);
                        RealmDb.insertRealmModel(cityInfo.getCityInfoRealm());
                        mProgressBarStatus.setVisibility(View.INVISIBLE);
                        //mHistoryDb.insertCity(gson.toJson(cityInfo));
                        //realmDb.insertCity(cityInfo);
                        //new InsertCitiy(cityInfo).execute();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnack(e.getMessage());
                        mProgressBarStatus.setVisibility(View.INVISIBLE);
                    }
                }));

    }

    private void sendPostCurrentWeather(final CityInfo cityInfo, final int type, final int index){
        mProgressBarStatus.setVisibility(View.VISIBLE);
        disposable.add(mAppWeatherService.getCurrentWeather(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherCurrentInfo>(){

                    @Override
                    public void onSuccess(WeatherCurrentInfo currentWeatherInfo) {

                        switch (type){
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

    private void readDatabase(){
        mProgressBarStatus.setVisibility(View.VISIBLE);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentInfos.clear();
                Realm realm = null;
                try {
                    realm = Realm.getDefaultInstance();
                    List<CityInfoRealm> models = realm.where(CityInfoRealm.class).findAll();
                    for(CityInfoRealm city : models){
                        sendPostCurrentWeather(city.getCityInfo(), DATABASE_DOWNLOAD, 0);
                        Log.i(TAG, "ID = " + city.getCityInfo().getId());
                    }
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                    if(mSwipeRefreshLayout.isRefreshing())
                        mSwipeRefreshLayout.setRefreshing(false);
                    mProgressBarStatus.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addFromDatabase(WeatherCurrentInfo currentWeatherInfo, CityInfo cityInfo){
        currentWeatherInfo.getCoord().setCityName(cityInfo.getCityName());
        currentWeatherInfo.getCoord().setCountryName(cityInfo.getCountryName());
        currentWeatherInfo.getCoord().setId(cityInfo.getId());
        currentInfos.add(currentWeatherInfo);
        mHistoryAdapter.notifyDataSetChanged();

    }

    private void updateCurrentWeather(WeatherCurrentInfo newCurrentWeatherInfo, int index){
        currentInfos.get(index).setNewCurrentWeather(newCurrentWeatherInfo);
    }

    private void addToList(WeatherCurrentInfo currentWeatherInfo, CityInfo cityInfo){
        WeatherCurrentInfo currentInfo = currentWeatherInfo;
        currentInfo.getCoord().setCityName(cityInfo.getCityName());
        currentInfo.getCoord().setCountryName(cityInfo.getCountryName());

        boolean isContain = currentInfos.contains(currentInfo);
        if(!isContain){
            currentInfos.add(currentInfo);
        }
        mHistoryAdapter.notifyDataSetChanged();
    }


    private void showSnack(String message){
        Snackbar.make(mRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSwitch = (Switch)menu.findItem(R.id.myswitch)
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




    private void updateWeather(){
        for(int i =0; i < currentInfos.size(); i++){
            sendPostCurrentWeather(currentInfos.get(i).getCoord(), UPDATE_WEATHER, i);
        }
        if(mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
    }
    /*private class UpdateWeather extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            for(int i =0; i < currentInfos.size(); i++){
                sendPostCurrentWeather(currentInfos.get(i).getCoord(), UPDATE_WEATHER, i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }*/





    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        //realmDb.getRealm().close();
    }


}
