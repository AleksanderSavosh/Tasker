package com.aleksander.savosh.tasker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.aleksander.savosh.tasker.beans.Type;

import java.util.List;

public class TypeSpinnerAdapter extends ArrayAdapter<Type> {

    public TypeSpinnerAdapter(Context context, List<Type> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

//    public TypeSpinnerAdapter(Context context, List<Type> objects, Type selectType) {
//        super(context, android.R.layout.simple_spinner_item, objects);
//        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        textView.setText(getItem(position).getName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        textView.setText(getItem(position).getName());
        return textView;
    }

}
