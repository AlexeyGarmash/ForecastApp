package com.example.alex.gismasterapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.gismasterapp.activities.ForecastActivity;
import com.example.alex.gismasterapp.activities.MainActivity;
import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.Utils;
import com.example.alex.gismasterapp.fragments.OptionsBottomSheetFragment;
import com.example.alex.gismasterapp.models.WeatherCurrentInfo;

import java.util.List;

/**
 * Класс-адаптер для привязки данных о текущей погоде по местам, загруженным из БД.
 *
 * @author Alex
 * @version 1.0
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    /**
     * Внутренний класс для инициализации View по их ID.
     */
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView weatherCity;
        TextView weatherTemp;
        TextView weatherCountry;
        ImageView weatherImg;
        RelativeLayout parentLayout;

        /**
         * Конструктор, выполняющий поиск View по ID в родительском View.
         *
         * @param itemView родительский элемент.
         */
        HistoryViewHolder(View itemView) {
            super(itemView);

            weatherTemp = itemView.findViewById(R.id.currWeatherTemp);
            weatherImg = itemView.findViewById(R.id.currWeatherIcon);
            weatherCity = itemView.findViewById(R.id.currWeatherCity);
            weatherCountry = itemView.findViewById(R.id.currWeatherCountry);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

    private List<WeatherCurrentInfo> cities;
    private Context mContext;

    /**
     * Конструктор с параметрами.
     *
     * @param cities  список мест и их текущей погоды
     * @param context контекст MainActivity
     */
    public HistoryAdapter(List<WeatherCurrentInfo> cities, Context context) {
        this.cities = cities;
        mContext = context;
    }

    public List<WeatherCurrentInfo> getCities() {
        return cities;
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
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        HistoryViewHolder vHolder = new HistoryViewHolder(view);
        return vHolder;
    }

    /**
     * Этот метод должен обновлять содержимое itemView, чтобы отразить элемент в данной позиции.
     *
     * @param holder   ViewHolder, который должен быть обновлен для представления содержимого элемента в данной позиции в наборе данных
     * @param position Позиция элемента в наборе данных адаптера
     */
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, final int position) {

        holder.weatherCity.setText(cities.get(position).getCoord().getCityName());
        holder.weatherTemp.setText(cities.get(position).getWeatherPart().getFormatedTemp());
        holder.weatherCountry.setText(cities.get(position).getCoord().getCountryName());
        Utils.setImageByURL(mContext, holder.weatherImg, 60, 60, cities.get(position).getWeatherPart().getWeatherIconURL());
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ForecastActivity.class);
                intent.putExtra(MainActivity.LAT_LON_ADDRESS_DATA, cities.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                OptionsBottomSheetFragment bottomSheetFragment = new OptionsBottomSheetFragment();
                bottomSheetFragment.setHistoryAdapter(getThis());
                bottomSheetFragment.setIndex(position);
                bottomSheetFragment.show(((MainActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());
                //Toast.makeText(mContext, cities.get(position).getCoord().getId(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    public Context getmContext() {
        return mContext;
    }


    private HistoryAdapter getThis() {
        return this;
    }

    /**
     * Возвращает общее количество элементов в наборе данных, хранящемся в адаптере.
     *
     * @return размер набора данных.
     */
    @Override
    public int getItemCount() {
        return cities.size();
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
