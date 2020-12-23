package com.example.warehouseuser;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.warehouseuser.data.InstrumentWrapper;

import java.util.List;

public class InstrumentAdapter extends ArrayAdapter<InstrumentWrapper> {
    public InstrumentAdapter(Context context, List<InstrumentWrapper> instruments) {
        super(context, 0, instruments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstrumentWrapper instrument = getItem(position);

        Resources res = getContext().getResources();
        String[] categoryList = res.getStringArray(R.array.category_list);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        TextView manufacturer = (TextView) convertView.findViewById(R.id.manufacturer);
        TextView model = (TextView) convertView.findViewById(R.id.model);
        TextView category = convertView.findViewById(R.id.category);
        TextView price = (TextView) convertView.findViewById(R.id.price);
        TextView quantity = (TextView) convertView.findViewById(R.id.quantity);

        manufacturer.setText(instrument.getManufacturer());
        model.setText(instrument.getModel());
       category.setText(categoryList[instrument.getCategory()]);
        price.setText(String.format("%.2f", instrument.getPrice()));
        quantity.setText(String.valueOf(instrument.getQuantity()));

        return convertView;
    }
}
