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

/**
 * Класс-адаптер для привязки данных о прогнозе погоды на 5 дней к RecyclerView.
 *
 * @author Alex
 * @version 1.0
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.WeatherViewHolder> {


    /**
     * Внутренний класс для инициализации View по их ID.
     */
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


        /**
         * Конструктор, выполняющий поиск View по ID в родительском View.
         *
         * @param itemView родительский элемент.
         */
        WeatherViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            weatherDesc = (TextView) itemView.findViewById(R.id.weather_description);
            weatherTemp = (TextView) itemView.findViewById(R.id.weather_temp);
            weatherImg = (ImageView) itemView.findViewById(R.id.weather_pic);
            weatherDate = (TextView) itemView.findViewById(R.id.weather_date);
            weatherTime = (TextView) itemView.findViewById(R.id.weather_time);
            weatherDayWeek = (TextView) itemView.findViewById(R.id.weather_day);

            windDirection = (TextView) itemView.findViewById(R.id.wind_description);
            humidityDescription = (TextView) itemView.findViewById(R.id.humidity_description);
            windIcon = (ImageView) itemView.findViewById(R.id.wind_direction_icon);
            speedwind = itemView.findViewById(R.id.wind_speed);
        }
    }


    List<WeatherInfo> forecast;
    private Context mContext;

    /**
     * Конструктор с параметрами.
     *
     * @param forecast список прогноза погоды
     * @param context  контекст ForecastActivity
     */
    public ForecastAdapter(List<WeatherInfo> forecast, Context context) {
        this.forecast = forecast;
        mContext = context;
    }

    /**
     * Создает новый RecyclerView.ViewHolder и инициализирует некоторые частные поля, которые будут использоваться RecyclerView.
     *
     * @param parent   ViewGroup, в которую будет добавлен новый View после привязки к позиции адаптера
     * @param viewType тип представления нового View
     * @return
     */
    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false);
        WeatherViewHolder pvh = new WeatherViewHolder(v);
        return pvh;
    }

    /**
     * Этот метод должен обновлять содержимое itemView, чтобы отразить элемент в данной позиции.
     *
     * @param holder   ViewHolder, который должен быть обновлен для представления содержимого элемента в данной позиции в наборе данных
     * @param position Позиция элемента в наборе данных адаптера
     */
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

    /**
     * Возвращает общее количество элементов в наборе данных, хранящемся в адаптере.
     *
     * @return размер набора данных.
     */
    @Override
    public int getItemCount() {
        return forecast.size();
    }

    /**
     * Вызывается RecyclerView, когда он начинает наблюдать за этим адаптером.
     *
     * @param recyclerView экземпляр RecyclerView, который начал наблюдать за этим адаптером
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
