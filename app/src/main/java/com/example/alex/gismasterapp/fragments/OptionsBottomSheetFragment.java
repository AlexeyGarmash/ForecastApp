package com.example.alex.gismasterapp.fragments;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.alex.gismasterapp.R;
import com.example.alex.gismasterapp.activities.MainActivity;
import com.example.alex.gismasterapp.adapters.HistoryAdapter;
import com.example.alex.gismasterapp.dialogs.DeleteCityItemDialog;

public class OptionsBottomSheetFragment extends BottomSheetDialogFragment {
    private int index;

    private HistoryAdapter historyAdapter;

    private LinearLayout mLinearLayoutItem;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_options_bottom_sheet, container, false);

        mLinearLayoutItem = view.findViewById(R.id.linItemDelete);
        mLinearLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteCityItemDialog dialog = new DeleteCityItemDialog();
                dialog.setHistoryAdapter(historyAdapter);
                dialog.setIndex(index);
                getfragment().dismissAllowingStateLoss();
                dialog.show(((MainActivity)historyAdapter.getmContext()).getSupportFragmentManager(), dialog.getTag());

            }
        });

        return view;
    }


    public OptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setHistoryAdapter(HistoryAdapter historyAdapter) {
        this.historyAdapter = historyAdapter;
    }

    private OptionsBottomSheetFragment getfragment(){
        return this;
    }
}
