package com.example.warehouseuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.warehouseuser.data.Instrument;

import java.util.List;

public class InstrumentAdapter extends ArrayAdapter<Instrument> {
    public InstrumentAdapter(Context context, List<Instrument> instruments) {
        super(context, 0, instruments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Instrument instrument = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        TextView manufacturer = (TextView) convertView.findViewById(R.id.manufacturer);
        TextView model = (TextView) convertView.findViewById(R.id.model);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView quantity = (TextView) convertView.findViewById(R.id.quantity);

        manufacturer.setText(instrument.getManufacturer());
        model.setText(instrument.getModel());
        price.setText(String.format("%.2f", instrument.getPrice()));
        quantity.setText(String.valueOf(instrument.getQuantity()));

        return convertView;
    }
}
