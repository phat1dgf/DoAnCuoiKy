package com.example.doancuoiky.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.R;

import java.util.ArrayList;

public class UserFeedAdapter extends ArrayAdapter<Product> {
    Activity context;
    int layout;
    ArrayList<Product> list;

    public UserFeedAdapter(Activity context, int layout, ArrayList<Product> list) {
        super(context, layout,list);
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        convertView = layoutInflater.inflate(layout,null);
        Product product = list.get(position);

        ImageView img_product = convertView.findViewById(R.id.img_product);
        Bitmap imgProduct = decodeBase64ToBitmap(product.getProductImageSource());
        img_product.setImageBitmap(imgProduct);
        TextView tv_product_name = convertView.findViewById(R.id.tv_product_name);
        tv_product_name.setText(product.getProductName());
        TextView tv_product_price = convertView.findViewById(R.id.tv_product_price);
        tv_product_price.setText(String.valueOf(product.getProductPrice()));

        TextView tv_product_state = convertView.findViewById(R.id.tv_product_state);
        tv_product_state.setText(product.getProductState());
        TextView tv_product_location = convertView.findViewById(R.id.tv_product_location);
        tv_product_location.setText(product.getLocation());
        return convertView;
    }

    // Giải mã Base64 thành ảnh Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
