package com.example.alex.gismasterapp.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.alex.gismasterapp.adapters.HistoryAdapter;
import com.example.alex.gismasterapp.realm.RealmDb;

public class DeleteCityItemDialog extends DialogFragment {
    private HistoryAdapter historyAdapter;

    private int index;

    //private HistoryDb historyDb;


    public DeleteCityItemDialog(){

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //historyDb = new HistoryDb(new HistoryDbHelper(historyAdapter.getmContext()));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Удалить место из истории?")
                .setTitle("Удаление")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RealmDb.removeRealmModel(historyAdapter.getCities().get(index).getCoord().getCityInfoRealm());
                        historyAdapter.getCities().remove(index);
                        historyAdapter.notifyDataSetChanged();
                        //historyDb.deleteCity(new Gson().toJson(historyAdapter.getCities().get(index).getCoord(), CityInfo.class));
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    public void setHistoryAdapter(HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
