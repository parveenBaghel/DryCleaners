package com.superdrycleaners.drycleaners.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.superdrycleaners.R;
import com.superdrycleaners.drycleaners.beans.PincodeModel;

import java.util.List;

public class PincodeAdapter extends ArrayAdapter<PincodeModel> {
    String type;

    public PincodeAdapter(Context context, List<PincodeModel> pincodeList, String codeType) {
        super(context, 0, pincodeList);
        this.type = codeType;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable
    View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
    View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        // It is used to set our custom view.
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_adapter, parent, false);
        TextView textViewName = convertView.findViewById(R.id.spinnerText);
        PincodeModel currentItem = getItem(position);

        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            if (type == "pincode") {
                textViewName.setText(currentItem.getPincodeId());
            } else {
                textViewName.setText(currentItem.getLocation());
            }
        }
        return convertView;
    }
}

