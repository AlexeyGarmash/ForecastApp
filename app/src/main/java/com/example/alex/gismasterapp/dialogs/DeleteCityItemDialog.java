package com.example.alex.gismasterapp.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.alex.gismasterapp.adapters.HistoryAdapter;
import com.example.alex.gismasterapp.realm.RealmDb;

/**
 * Класс-диалог, возникающий при попытке удалить место из истории.
 */
public class DeleteCityItemDialog extends DialogFragment {
    private HistoryAdapter historyAdapter;

    private int index;

    //private HistoryDb historyDb;


    /**
     * Конструктор без параметров.
     */
    public DeleteCityItemDialog() {

    }


    /**
     * Вызывается в момент создания диалога, создавая пользовательский макет.
     *
     * @param savedInstanceState
     * @return диалоговое окно
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Удалить место из истории?")
                .setTitle("Удаление")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RealmDb.removeRealmModel(historyAdapter.getCities().get(index).getCoord().getCityInfoRealm());
                        historyAdapter.getCities().remove(index);
                        historyAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        return builder.create();
    }

    /**
     * Сеттер для {@link HistoryAdapter}
     *
     * @param historyAdapter обьект {@link HistoryAdapter}
     */
    public void setHistoryAdapter(HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
    }

    /**
     * Сеттер для индекса, по которому ведется удаление.
     *
     * @param index индекс в списке
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
