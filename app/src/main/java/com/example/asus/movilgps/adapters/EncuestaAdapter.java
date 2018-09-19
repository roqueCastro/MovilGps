package com.example.asus.movilgps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.models.Encuesta;

import java.util.List;

/**
 * Created by roque on 17/09/2018.
 */

public class EncuestaAdapter extends BaseAdapter {

    private Context context;
    private List<Encuesta> list;
    private int layout;

    public EncuestaAdapter(Context context, List<Encuesta> list, int layout){
        this.context =context;
        this.list =list;
        this.layout =layout;
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
            vh = new EncuestaAdapter.ViewHolder();
            vh.titulo = (TextView) convertView.findViewById(R.id.tituloSpinnerE);

            convertView.setTag(vh);

        } else {
            vh = (EncuestaAdapter.ViewHolder) convertView.getTag();
        }

        Encuesta encuesta = list.get(position);
        vh.titulo.setText(encuesta.getNombre_encuesta());

        return convertView;


    }
    public class ViewHolder {
        TextView titulo;
    }
}
