package com.example.doancuoiky.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doancuoiky.Models.UserOption;
import com.example.doancuoiky.R;

import java.util.ArrayList;

public class UserOptionAdapter extends ArrayAdapter<UserOption> {
    Activity context;
    int layout;
    ArrayList<UserOption> list;

    public UserOptionAdapter(Activity context, int layout, ArrayList<UserOption> list) {
        super(context, layout, list);
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layout,null);

        UserOption userOption = list.get(position);
        // set data cho layout
        ImageView imageView = convertView.findViewById(R.id.img_option_icon);
        imageView.setImageResource(userOption.getImage());
        TextView textView = convertView.findViewById(R.id.tv_option_title);
        textView.setText(userOption.getName());
        return convertView;
    }
}
