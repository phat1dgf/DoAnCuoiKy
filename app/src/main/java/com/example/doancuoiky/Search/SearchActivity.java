package com.example.doancuoiky.Search;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doancuoiky.Adapter.FilterAdapter;
import com.example.doancuoiky.Adapter.SearchedProductAdapter;
import com.example.doancuoiky.Instance.Filter;
import com.example.doancuoiky.Instance.Product;
import com.example.doancuoiky.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView lvFilter;
    RecyclerView lvProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_searchbar, new SearchFragment())
                    .commit();
        }

        lvFilter = findViewById(R.id.lv_filter);
        FilterAdapter filterAdapter = new FilterAdapter(this);
        LinearLayoutManager filterLinearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        lvFilter.setLayoutManager(filterLinearLayoutManager);
        filterAdapter.setData(getFilterList());
        lvFilter.setAdapter(filterAdapter);

        //-----------------------------------------
        lvProduct = findViewById(R.id.lv_product);
        SearchedProductAdapter productAdapter = new SearchedProductAdapter();
        LinearLayoutManager productLinearLayoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        lvProduct.setLayoutManager(productLinearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        lvProduct.addItemDecoration(itemDecoration);

        productAdapter.setData(getProdcutList());
        lvProduct.setAdapter(productAdapter);
    }

    private List<Product> getProdcutList() {
        List<Product> list = new ArrayList<>();

        return list;
    }

    private List<Filter> getFilterList() {
        List<Filter> list = new ArrayList<>();
        list.add(new Filter("Danh mục"));
        list.add(new Filter("Hãng"));
        list.add(new Filter("Giá"));
        list.add(new Filter("Tình trạng"));
        list.add(new Filter("Bảo hành"));
        list.add(new Filter("Khu vực"));
        return list;
    }
}