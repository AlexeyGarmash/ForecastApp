package com.example.alex.gismasterapp.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.Utils;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private WeatherCurrentInfo currentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        currentWeather = getIntent().getExtras().getParcelable(MainActivity.LAT_LON_ADDRESS_DATA);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View customView = getLayoutInflater().inflate(R.layout.history_item,null);
                TextView tempTv = customView.findViewById(R.id.currWeatherTemp);
                TextView cityTv = customView.findViewById(R.id.currWeatherCity);
                TextView countryTv = customView.findViewById(R.id.currWeatherCountry);
                ImageView iconweather = customView.findViewById(R.id.currWeatherIcon);


                tempTv.setText(currentWeather.getWeatherPart().getFormatedTemp());
                cityTv.setText(currentWeather.getCoord().getCityName());
                countryTv.setText(currentWeather.getCoord().getCountryName());
                Utils.setImageByURL(MapsActivity.this, iconweather, 60, 60, currentWeather.getWeatherPart().getWeatherIconURL());
                return  customView;
            }
        });
        // Add a marker in Sydney and move the camera
        double lat = currentWeather.getCoord().getLat();
        double lon = currentWeather.getCoord().getLon();
        LatLng place = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions()
                .position(place));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 8));
    }
}
