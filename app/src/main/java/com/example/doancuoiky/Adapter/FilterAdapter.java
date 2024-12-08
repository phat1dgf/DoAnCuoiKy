package com.example.doancuoiky.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.example.doancuoiky.Instance.Filter;
import com.example.doancuoiky.R;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>{

    private Context context;
    List<Filter> listFilter;

    public FilterAdapter(Context context){
        this.context = context;
    }

    public void setData(List<Filter> list){
        this.listFilter = list;
        notifyDataSetChanged();;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item,parent,false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        Filter filter = listFilter.get(position);
        if(filter ==null){
            return;
        }
        holder.filterName.setText(filter.getFilterName());
    }

    @Override
    public int getItemCount() {
        if(listFilter != null){
            return listFilter.size();
        }
        return 0;
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder{

        private TextView filterName;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.tv_filter_name);
        }
    }
}
