package com.example.asus.movilgps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.models.Respuesta;

import java.util.List;

/**
 * Created by ASUS on 12/06/2018.
 */

public class RespuestaAdapte extends BaseAdapter {

    private Context context;
    private List<Respuesta> list;
    private int layout;

    public RespuestaAdapte(Context context, List<Respuesta> list, int layout) {
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
            vh.nombreRespuesta = (TextView) convertView.findViewById(R.id.textViewRespuesta);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Respuesta respuesta = list.get(position);
        vh.nombreRespuesta.setText(respuesta.getNombre_resp());


        return convertView;
    }

    public class ViewHolder {
        TextView nombreRespuesta;
    }
}