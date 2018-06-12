package com.example.asus.movilgps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.models.Pregunta;

import java.util.List;

/**
 * Created by ASUS on 08/06/2018.
 */

public class PreguntaAdapte extends BaseAdapter {

    private Context context;
    private List<Pregunta> list;
    private int layout;

    public PreguntaAdapte(Context context, List<Pregunta> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.nombrePregunta = (TextView) convertView.findViewById(R.id.textViewPregunta);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Pregunta pregunta = list.get(position);
        vh.nombrePregunta.setText(pregunta.getNombre_pre());


        return convertView;
    }

    public class ViewHolder {
        TextView nombrePregunta;
    }
}
