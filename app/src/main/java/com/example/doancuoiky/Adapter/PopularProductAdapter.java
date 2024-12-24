package com.example.doancuoiky.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doancuoiky.Interface.IClickProductItemListener;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.R;

import java.util.List;

public class PopularProductAdapter extends RecyclerView.Adapter<PopularProductAdapter.PopularProductViewHolder> {


    private List<Product> listProduct;
    private IClickProductItemListener iClickProductItemListener;



    public PopularProductAdapter(List<Product> listProduct, IClickProductItemListener iClickProductItemListener) {
        this.listProduct = listProduct;
        this.iClickProductItemListener = iClickProductItemListener;
    }

    @NonNull
    @Override
    public PopularProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_product_item,parent,false);
        return new PopularProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularProductViewHolder holder, int position) {
        Product product = listProduct.get(position);
        if(product ==null){
            return;
        }
        Bitmap imgProduct = decodeBase64ToBitmap(product.getProductImageSource());
        holder.imgProduct.setImageBitmap(imgProduct);
        holder.productName.setText(product.getProductName());
        // Định dạng giá tiền
        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getProductPrice());
        // Gán vào TextView
        holder.productPrice.setText(formattedPrice);
        holder.location.setText(product.getLocation());
        holder.productItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickProductItemListener.onClickProductItem(product);
            }
        });
    }

    // Giải mã Base64 thành ảnh Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    public int getItemCount() {
        if(listProduct != null){
            return listProduct.size();
        }
        return 0;
    }

    public class PopularProductViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout productItem;
        private ImageView imgProduct;
        public TextView productName;
        public TextView location;
        public TextView productPrice;


        public PopularProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productItem = itemView.findViewById(R.id.product_item);
            imgProduct = itemView.findViewById(R.id.img_product);
            productName = itemView.findViewById(R.id.tv_product_name);
            location= itemView.findViewById(R.id.tv_product_location);
            productPrice = itemView.findViewById(R.id.tv_product_price);
        }
    }
}
