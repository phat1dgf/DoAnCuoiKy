package com.example.doancuoiky.Adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doancuoiky.Instance.Product;
import com.example.doancuoiky.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SearchedProductAdapter extends RecyclerView.Adapter<SearchedProductAdapter.ProductViewHolder> {

    private List<Product> listProduct;


    public void setData(List<Product> list){
        this.listProduct = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = listProduct.get(position);
        if(product ==null){
            return;
        }
        holder.imgProduct.setImageURI(Uri.parse(product.getProductImageSource()));
        holder.productName.setText(product.getProductName());
        // Định dạng giá tiền
        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getProductPrice());
        // Gán vào TextView
        holder.productPrice.setText(formattedPrice);
        holder.location.setText(product.getLocation());
        holder.productState.setText(product.getProductState());
    }

    @Override
    public int getItemCount() {
        if(listProduct != null){
            return listProduct.size();
        }
        return 0;
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgProduct;
        public TextView productName;
        public TextView location;
        public TextView productPrice;
        public TextView productState;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            productName = itemView.findViewById(R.id.tv_product_name);
            location= itemView.findViewById(R.id.tv_product_location);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            productState = itemView.findViewById(R.id.tv_product_state);
        }
    }
}