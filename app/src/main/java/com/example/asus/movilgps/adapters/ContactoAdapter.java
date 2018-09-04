package com.example.asus.movilgps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.models.Contacto;

import java.util.List;

/**
 * Created by roque on 03/09/2018.
 */

public class ContactoAdapter extends BaseAdapter {

    private Context context;
    private List<Contacto> list;
    private int layout;

    public ContactoAdapter(Context context, List<Contacto> list, int layout){
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
            vh = new ViewHolder();
            vh.titulo = (TextView) convertView.findViewById(R.id.tituloSpinnerC);

            convertView.setTag(vh);

        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        Contacto contacto = list.get(position);
        vh.titulo.setText(contacto.getEncuesta());

        return convertView;


    }
    public class ViewHolder {
        TextView titulo;
    }
}
