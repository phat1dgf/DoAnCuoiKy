package com.example.doancuoiky.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doancuoiky.Adapter.CategoryAdapter;
import com.example.doancuoiky.Adapter.DiscoverProductAdapter;
import com.example.doancuoiky.Adapter.PopularProductAdapter;
import com.example.doancuoiky.Interface.IClickProductItemListener;
import com.example.doancuoiky.Instance.Category;
import com.example.doancuoiky.Instance.Product;
import com.example.doancuoiky.Product.ProductActivity;
import com.example.doancuoiky.R;

import java.util.ArrayList;
import java.util.List;


public class HomePageFragment extends Fragment {

    RecyclerView gvCategory;
    RecyclerView lvPopular;
    RecyclerView gvDiscover;


    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        gvCategory = view.findViewById(R.id.gv_category);
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext());
        GridLayoutManager categoryGridLayoutManager = new GridLayoutManager(getContext(), 2,GridLayoutManager.HORIZONTAL, false);
        gvCategory.setLayoutManager(categoryGridLayoutManager);

        categoryAdapter.setData(getListCategory());
        gvCategory.setAdapter(categoryAdapter);


        //-------------------------------------------
        lvPopular= view.findViewById(R.id.lv_popular);
        PopularProductAdapter popularAdapter = new PopularProductAdapter(getListPopularProduct(),new IClickProductItemListener() {
            @Override
            public void onClickProductItem(Product product) {
                onClickGoToProductPage(product);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        lvPopular.setLayoutManager(linearLayoutManager);
        lvPopular.setAdapter(popularAdapter);

        

        //-------------------------------------------------
        gvDiscover = view.findViewById(R.id.gv_discover);
        DiscoverProductAdapter discoverAdapter = new DiscoverProductAdapter(getListDiscoverProduct(), new IClickProductItemListener() {
            @Override
            public void onClickProductItem(Product product) {
                onClickGoToProductPage(product);
            }
        });
        GridLayoutManager discoverGridLayoutManager = new GridLayoutManager(getContext(), 2,GridLayoutManager.VERTICAL, false);
        gvDiscover.setLayoutManager(discoverGridLayoutManager);
        gvDiscover.setAdapter(discoverAdapter);


        return view;
    }

    private List<Product> getListDiscoverProduct() {
        List<Product> list = new ArrayList<>();

        return list;
    }

    private List<Product> getListPopularProduct() {
        List<Product> list = new ArrayList<>();


        return list;
    }

    private List<Category> getListCategory() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(R.drawable.phone_icon,"Điện thoại"));
        list.add(new Category(R.drawable.laptop_icon,"Laptop"));
        list.add(new Category(R.drawable.speaker_icon,"Loa"));
        list.add(new Category(R.drawable.headphone_icon,"Tai nghe"));
        list.add(new Category(R.drawable.accessory_icon,"Phụ kiện(chuột, bàn phím...)"));
        list.add(new Category(R.drawable.monitor_icon,"Màn hình"));
        list.add(new Category(R.drawable.watch_icon,"Thiết bị đeo thông minh"));
        list.add(new Category(R.drawable.tablet_icon,"Máy tính bảng"));
        list.add(new Category(R.drawable.desktop_icon,"Máy tính bàn"));
        list.add(new Category(R.drawable.tv_icon,"TV"));
        list.add(new Category(R.drawable.camera_icon,"Camera"));
        list.add(new Category(R.drawable.memory_icon,"Linh kiện máy tính"));
        return list;
    }
    private void onClickGoToProductPage(Product product){
        Intent intent =new Intent(getActivity(), ProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product",product);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}