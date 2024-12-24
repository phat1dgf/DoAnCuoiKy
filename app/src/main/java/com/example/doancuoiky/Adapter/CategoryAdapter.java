package com.example.doancuoiky.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doancuoiky.Interface.IClickCategoryItemListener;
import com.example.doancuoiky.Models.Category;
import com.example.doancuoiky.R;
import com.example.doancuoiky.Search.SearchActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private Context context;
    private List<Category> listCategory;
    private IClickCategoryItemListener iClickCategoryItemListener;

    public CategoryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Category> list,IClickCategoryItemListener listener){
        this.listCategory = list;
        this.iClickCategoryItemListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCategory.get(position);
        if(category ==null){
            return;
        }
        holder.imgCategory.setImageResource(category.getCategoryImageSource());
        holder.categoryName.setText(category.getCategoryName());
        holder.category_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iClickCategoryItemListener.onClickCategoryItem(category.getCategoryName());
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listCategory != null){
            return listCategory.size();
        }
        return 0;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout category_item;
        private ImageView imgCategory;
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            category_item = itemView.findViewById(R.id.category_item);
            imgCategory = itemView.findViewById(R.id.img_category);
            categoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
