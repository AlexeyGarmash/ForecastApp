package com.example.alex.gismasterapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.Utils;
import com.example.alex.gismasterapp.models.WeatherInfo;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.WeatherViewHolder> {


    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView weatherDesc;
        TextView weatherTemp;
        ImageView weatherImg;

        TextView weatherDate;
        TextView weatherTime;
        TextView weatherDayWeek;

        TextView windDirection;
        TextView humidityDescription;
        TextView speedwind;

        ImageView windIcon;


        WeatherViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            weatherDesc = (TextView)itemView.findViewById(R.id.weather_description);
            weatherTemp = (TextView)itemView.findViewById(R.id.weather_temp);
            weatherImg = (ImageView)itemView.findViewById(R.id.weather_pic);
            weatherDate = (TextView)itemView.findViewById(R.id.weather_date);
            weatherTime = (TextView)itemView.findViewById(R.id.weather_time);
            weatherDayWeek = (TextView)itemView.findViewById(R.id.weather_day);

            windDirection = (TextView)itemView.findViewById(R.id.wind_description);
            humidityDescription = (TextView)itemView.findViewById(R.id.humidity_description);
            windIcon = (ImageView)itemView.findViewById(R.id.wind_direction_icon);
            speedwind = itemView.findViewById(R.id.wind_speed);
        }
    }

    List<WeatherInfo> forecast;
    private Context mContext;

    public ForecastAdapter(List<WeatherInfo> forecast, Context context){
        this.forecast = forecast;
        mContext = context;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false);
        WeatherViewHolder pvh = new WeatherViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        holder.weatherDesc.setText(forecast.get(position).getWeatherPart().getDescrip());
        holder.weatherTemp.setText(forecast.get(position).getWeatherPart().getFormatedTemp());
        Utils.setImageByURL(mContext, holder.weatherImg, 60, 60, forecast.get(position).getWeatherPart().getWeatherIconURL());
        holder.weatherDate.setText(forecast.get(position).getDateShort());
        holder.weatherDayWeek.setText(forecast.get(position).getDayWeek());
        holder.weatherTime.setText(forecast.get(position).getTimeShort());

        holder.windDirection.setText(forecast.get(position).getWeatherPart().getDirectWind());
        holder.humidityDescription.setText(forecast.get(position).getWeatherPart().getFormatedHumidity());
        Utils.setImageByResource(holder.windIcon, forecast.get(position).getWeatherPart().getWindResource(), 60, 60, mContext);
        holder.speedwind.setText(String.valueOf(forecast.get(position).getWeatherPart().getSpeedWind()));
    }

    @Override
    public int getItemCount() {
        return forecast.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
