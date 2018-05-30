package com.example.asus.movilgps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asus.movilgps.R;
import com.example.asus.movilgps.models.usuario;

import java.util.List;

/**
 * Created by ASUS on 23/04/2018.
 */

public class UsuarioAdapter extends BaseAdapter {


    private Context context;
    private List<usuario> list;
    private int layout;

    public UsuarioAdapter(Context context, List<usuario> usuarios, int layout) {
        this.context = context;
        this.list = usuarios;
        this.layout = layout;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public usuario getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        if(convertView == null) {
           /* convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.nombre = (TextView) convertView.findViewById(R.id.textViewNombre);
            vh.apellido = (TextView) convertView.findViewById(R.id.textViewApellido);
            vh.telefono = (TextView) convertView.findViewById(R.id.textViewTelefono);
            vh.correo = (TextView) convertView.findViewById(R.id.textViewCorreo);
            vh.password = (TextView) convertView.findViewById(R.id.textViewPass);
            vh.login = (TextView) convertView.findViewById(R.id.textViewLogin);
            convertView.setTag(vh);*/
        }else {
            vh = (ViewHolder) convertView.getTag();
        }

        usuario usuario = list.get(position);
       /* vh.nombre.setText(usuario.getNombre());
        vh.apellido.setText(usuario.getApellido());
        vh.telefono.setText(usuario.getTelefono());
        vh.correo.setText(usuario.getCorreo());
        vh.password.setText(usuario.getPass());
        vh.login.setText(usuario.getLogin());*/


        return convertView;
    }

    public class ViewHolder {
        TextView nombre;
        TextView apellido;
        TextView telefono;
        TextView correo;
        TextView password;
        TextView login;
    }

}
