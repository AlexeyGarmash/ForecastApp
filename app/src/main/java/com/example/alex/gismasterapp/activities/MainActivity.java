package com.example.alex.gismasterapp.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
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
import android.widget.TextView;

import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.adapters.HistoryAdapter;
import com.example.alex.gismasterapp.models.CityInfo;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;
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

public class MainActivity extends AppCompatActivity {

    public static String TAG = "MainActivity";
    public static String LAT_LON_ADDRESS_DATA;

    private static final int  UPDATE_WEATHER = 1;
    private static final int  DATABASE_DOWNLOAD = 2;
    private static final int  ADD_TO_LIST = 3;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextViewListEmpty;

    private HistoryAdapter mHistoryAdapter;

    private Gson gson;
    private CompositeDisposable disposable = new CompositeDisposable();

    private AppWeatherService mAppWeatherService;

    private String address = null;

    private List<WeatherCurrentInfo> currentInfos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.rvHistory);
        mTextViewListEmpty = findViewById(R.id.tvListEmpty);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
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

        mAppWeatherService = ServiceUtils.getService("http://192.168.1.106:3000");

        setAutoCompleteFragment();
        //new DownloadCities().execute();
        setRefreshLayout();



    }

    @Override
    protected void onResume() {
        super.onResume();
        /*SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        String baseUrl = prefs.getString("base_url", "http://192.168.1.106:3000");
        ServiceUtils.setNewUrl(baseUrl);*/
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
                new UpdateWeather().execute();
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
                sendPostLocation(address);
                autocompleteFragment.setText(place.getAddress().toString());
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
                        //mHistoryDb.insertCity(gson.toJson(cityInfo));
                        //realmDb.insertCity(cityInfo);
                        //new InsertCitiy(cityInfo).execute();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnack(e.getMessage());
                    }
                }));

    }

    private void sendPostCurrentWeather(final CityInfo cityInfo, final int type, final int index){

        disposable.add(mAppWeatherService.getCurrentWeather(cityInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherCurrentInfo>(){

                    @Override
                    public void onSuccess(WeatherCurrentInfo currentWeatherInfo) {

                        switch (type){
                            case UPDATE_WEATHER:
                                updateCurrentWeather(currentWeatherInfo, currentInfos.get(index));
                                break;
                            case ADD_TO_LIST:
                                addToList(currentWeatherInfo, cityInfo);
                                break;

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSnack(e.getMessage());
                    }
                }));
    }

    private void updateCurrentWeather(WeatherCurrentInfo newCurrentWeatherInfo, WeatherCurrentInfo currentWeatherInfo){
        newCurrentWeatherInfo = currentWeatherInfo;
        mHistoryAdapter.notifyDataSetChanged();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private class UpdateWeather extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... voids) {
            for(int i =0; i < currentInfos.size(); i++){
                sendPostCurrentWeather(currentInfos.get(i).getCoord(), UPDATE_WEATHER, i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(mSwipeRefreshLayout.isRefreshing())
                mSwipeRefreshLayout.setRefreshing(false);
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        //realmDb.getRealm().close();
    }


}
