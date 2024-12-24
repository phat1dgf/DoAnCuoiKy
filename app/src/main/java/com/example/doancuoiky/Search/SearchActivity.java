package com.example.doancuoiky.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.doancuoiky.Adapter.SearchedProductAdapter;

import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Interface.IClickProductItemListener;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.Product.ProductActivity;
import com.example.doancuoiky.Product.UserProductActivity;
import com.example.doancuoiky.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    Button btn_filter_category, btn_filter_brand, btn_filter_price, btn_filter_state, btn_filter_guarantee, btn_filter_location;
    RecyclerView lvProduct;

    List<Product> productList; // Danh sách tất cả sản phẩm
    List<Product> filteredList;
    FirestoreHelper firestoreHelper;
    SearchedProductAdapter productAdapter;

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

        firestoreHelper = new FirestoreHelper();
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();

        String keyword = getIntent().getStringExtra("keyword");


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_searchbar, new SearchFragment())
                    .commit();
        }
        //-----------------------------------------
        btn_filter_category = findViewById(R.id.btn_filter_category);
        btn_filter_brand = findViewById(R.id.btn_filter_brand);
        btn_filter_price = findViewById(R.id.btn_filter_price);
        btn_filter_state = findViewById(R.id.btn_filter_state);
        btn_filter_guarantee = findViewById(R.id.btn_filter_guarantee);


        btn_filter_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryFilterDialog();
            }
        });
        btn_filter_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBrandFilterDialog();
            }
        });
        btn_filter_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPriceFilterDialog();
            }
        });
        btn_filter_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStateFilterDialog();
            }
        });
        btn_filter_guarantee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGuaranteeFilterDialog();
            }
        });
        //-----------------------------------------
        lvProduct = findViewById(R.id.lv_product);
        productAdapter = new SearchedProductAdapter();
        LinearLayoutManager productLinearLayoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        lvProduct.setLayoutManager(productLinearLayoutManager);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        lvProduct.addItemDecoration(itemDecoration);

        productAdapter.setData(filteredList, new IClickProductItemListener() {
            @Override
            public void onClickProductItem(Product product) {
                onClickGoToProductPage(product);
            }
        });
        lvProduct.setAdapter(productAdapter);

        fetchAndFilterProducts(keyword);
    }



    private void fetchAndFilterProducts(String keyword) {
        firestoreHelper.getAllProducts(new FirestoreHelper.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> allProducts) {
                productList.clear();
                productList.addAll(allProducts);

                // Lọc sản phẩm theo keyword
                filteredList.clear();
                for (Product product : productList) {
                    if (product.getProductName().toLowerCase().contains(keyword.toLowerCase()) ||
                            product.getCategory().toLowerCase().contains(keyword.toLowerCase())) {
                        filteredList.add(product);
                    }
                }

                productAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickGoToProductPage(Product product){
        Intent intent;
        if(product.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            intent = new Intent(SearchActivity.this, UserProductActivity.class);
        }
        else {
            intent = new Intent(SearchActivity.this, ProductActivity.class);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void showCategoryFilterDialog() {
        // Tạo BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_category_dialog, null);
        dialog.setContentView(dialogView);

        // Ánh xạ các nút trong dialog
        Button btn_phone = dialogView.findViewById(R.id.btn_phone);
        Button btn_laptop = dialogView.findViewById(R.id.btn_laptop);
        Button btn_speaker = dialogView.findViewById(R.id.btn_speaker);
        Button btn_headphone = dialogView.findViewById(R.id.btn_headphone);
        Button btn_accessories = dialogView.findViewById(R.id.btn_accessories);
        Button btn_monitor = dialogView.findViewById(R.id.btn_monitor);
        Button btn_wearable_device = dialogView.findViewById(R.id.btn_wearable_device);
        Button btn_tablet = dialogView.findViewById(R.id.btn_tablet);
        Button btn_desktop_computer = dialogView.findViewById(R.id.btn_desktop_computer);
        Button btn_tv = dialogView.findViewById(R.id.btn_tv);
        Button btn_camera = dialogView.findViewById(R.id.btn_camera);
        Button btn_computer_parts = dialogView.findViewById(R.id.btn_computer_parts);

        Button btn_other = dialogView.findViewById(R.id.btn_other);

        // Xử lý sự kiện khi người dùng chọn danh mục
        View.OnClickListener categoryClickListener = v -> {
            String category = ((Button) v).getText().toString();
            filterProductsByCategory(category);
            btn_filter_category.setText(category); // Cập nhật text của nút
            dialog.dismiss(); // Đóng dialog
        };

        btn_laptop.setOnClickListener(categoryClickListener);
        btn_phone.setOnClickListener(categoryClickListener);
        btn_camera.setOnClickListener(categoryClickListener);
        btn_accessories.setOnClickListener(categoryClickListener);
        btn_speaker.setOnClickListener(categoryClickListener);
        btn_headphone.setOnClickListener(categoryClickListener);
        btn_monitor.setOnClickListener(categoryClickListener);
        btn_wearable_device.setOnClickListener(categoryClickListener);
        btn_tablet.setOnClickListener(categoryClickListener);
        btn_desktop_computer.setOnClickListener(categoryClickListener);
        btn_tv.setOnClickListener(categoryClickListener);
        btn_desktop_computer.setOnClickListener(categoryClickListener);
        btn_computer_parts.setOnClickListener(categoryClickListener);
        btn_other.setOnClickListener(categoryClickListener);

        dialog.show();
    }
    private void filterProductsByCategory(String category) {
        List<Product> categoryFilteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                categoryFilteredList.add(product);
            }
        }
        // Cập nhật danh sách trong adapter
        filteredList.clear();
        filteredList.addAll(categoryFilteredList);
        productAdapter.notifyDataSetChanged();
        checkEmptyList();
    }

    private void checkEmptyList(){
        if (filteredList.isEmpty()) {
            Toast.makeText(SearchActivity.this, "Không có sản phẩm phù hợp", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBrandFilterDialog() {
        // Tạo BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_brand_dialog, null);
        dialog.setContentView(dialogView);

        // Ánh xạ các nút trong dialog (brand options)
        Button btn_apple = dialogView.findViewById(R.id.btn_apple);
        Button btn_samsung = dialogView.findViewById(R.id.btn_samsung);
        Button btn_xiaomi = dialogView.findViewById(R.id.btn_xiaomi);
        Button btn_huawei = dialogView.findViewById(R.id.btn_huawei);
        Button btn_oppo = dialogView.findViewById(R.id.btn_oppo);
        Button btn_vivo = dialogView.findViewById(R.id.btn_vivo);
        Button btn_realme = dialogView.findViewById(R.id.btn_realme);
        Button btn_oneplus = dialogView.findViewById(R.id.btn_oneplus);
        Button btn_google = dialogView.findViewById(R.id.btn_google);
        Button btn_nokia = dialogView.findViewById(R.id.btn_nokia);
        Button btn_lg = dialogView.findViewById(R.id.btn_lg);
        Button btn_other_brand = dialogView.findViewById(R.id.btn_other_brand);

        // Xử lý sự kiện khi người dùng chọn thương hiệu
        View.OnClickListener brandClickListener = v -> {
            String brand = ((Button) v).getText().toString();
            filterProductsByBrand(brand);
            btn_filter_brand.setText(brand); // Cập nhật text của nút
            dialog.dismiss(); // Đóng dialog
        };

        btn_apple.setOnClickListener(brandClickListener);
        btn_samsung.setOnClickListener(brandClickListener);
        btn_xiaomi.setOnClickListener(brandClickListener);
        btn_huawei.setOnClickListener(brandClickListener);
        btn_oppo.setOnClickListener(brandClickListener);
        btn_vivo.setOnClickListener(brandClickListener);
        btn_realme.setOnClickListener(brandClickListener);
        btn_oneplus.setOnClickListener(brandClickListener);
        btn_google.setOnClickListener(brandClickListener);
        btn_nokia.setOnClickListener(brandClickListener);
        btn_lg.setOnClickListener(brandClickListener);
        // Xử lý sự kiện cho nút "Khác"
        btn_other_brand.setOnClickListener(v -> {
            filterProductsByOtherBrands(); // Lọc sản phẩm không thuộc các thương hiệu đã nêu
            btn_filter_brand.setText("Khác"); // Cập nhật text của nút
            dialog.dismiss(); // Đóng dialog
        });

        dialog.show();
    }
    private void filterProductsByOtherBrands() {
        List<Product> otherBrandFilteredList = new ArrayList<>();

        // Danh sách các thương hiệu đã nêu
        List<String> predefinedBrands = new ArrayList<>();
        predefinedBrands.add("Apple");
        predefinedBrands.add("Samsung");
        predefinedBrands.add("Xiaomi");
        predefinedBrands.add("Huawei");
        predefinedBrands.add("Oppo");
        predefinedBrands.add("Vivo");
        predefinedBrands.add("Realme");
        predefinedBrands.add("OnePlus");
        predefinedBrands.add("Google");
        predefinedBrands.add("Nokia");
        predefinedBrands.add("LG");

        for (Product product : productList) {
            if (!predefinedBrands.contains(product.getBrandName())) {
                otherBrandFilteredList.add(product); // Thêm sản phẩm không thuộc các thương hiệu đã nêu
            }
        }

        // Cập nhật danh sách trong adapter
        filteredList.clear();
        filteredList.addAll(otherBrandFilteredList);
        productAdapter.notifyDataSetChanged();

        // Kiểm tra nếu không có sản phẩm
        checkEmptyList();
    }
    private void filterProductsByBrand(String brand) {
        List<Product> brandFilteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getBrandName().equalsIgnoreCase(brand)) {
                brandFilteredList.add(product);
            }
        }
        // Cập nhật danh sách trong adapter
        filteredList.clear();
        filteredList.addAll(brandFilteredList);
        productAdapter.notifyDataSetChanged();

        // Kiểm tra nếu không có sản phẩm
        checkEmptyList();
    }

    private void showStateFilterDialog() {
        // Tạo BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_state_dialog, null);
        dialog.setContentView(dialogView);

        // Ánh xạ các nút trong dialog (state options)
        Button btn_used = dialogView.findViewById(R.id.btn_used);
        Button btn_new = dialogView.findViewById(R.id.btn_new);

        // Xử lý sự kiện khi người dùng chọn trạng thái
        View.OnClickListener stateClickListener = v -> {
            String state = ((Button) v).getText().toString();
            filterProductsByState(state);
            btn_filter_state.setText(state); // Cập nhật text của nút
            dialog.dismiss(); // Đóng dialog
        };

        btn_used.setOnClickListener(stateClickListener);
        btn_new.setOnClickListener(stateClickListener);

        dialog.show();
    }
    private void filterProductsByState(String state) {
        List<Product> stateFilteredList = new ArrayList<>();

        for (Product product : productList) {
            if (state.equalsIgnoreCase("Mới") && product.getProductState().equalsIgnoreCase("Mới")) {
                stateFilteredList.add(product);
            } else if (state.equalsIgnoreCase("Đã sử dụng") && product.getProductState().equalsIgnoreCase("Đã sử dụng")) {
                stateFilteredList.add(product);
            }
        }

        // Cập nhật danh sách trong adapter
        filteredList.clear();
        filteredList.addAll(stateFilteredList);
        productAdapter.notifyDataSetChanged();

        checkEmptyList();
    }

    private void showGuaranteeFilterDialog() {
        // Tạo BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_guarantee_dialog, null);
        dialog.setContentView(dialogView);

        // Ánh xạ các nút trong dialog (guarantee options)
        Button btn_no_warranty = dialogView.findViewById(R.id.btn_no_warranty);
        Button btn_has_warranty = dialogView.findViewById(R.id.btn_has_warranty);

        // Xử lý sự kiện khi người dùng chọn bảo hành
        View.OnClickListener guaranteeClickListener = v -> {
            String guarantee = ((Button) v).getText().toString();
            filterProductsByGuarantee(guarantee);
            btn_filter_guarantee.setText(guarantee); // Cập nhật text của nút
            dialog.dismiss(); // Đóng dialog
        };

        btn_no_warranty.setOnClickListener(guaranteeClickListener);
        btn_has_warranty.setOnClickListener(guaranteeClickListener);

        dialog.show();
    }
    private void filterProductsByGuarantee(String guarantee) {
        List<Product> guaranteeFilteredList = new ArrayList<>();

        for (Product product : productList) {
            if (guarantee.equalsIgnoreCase("Còn bảo hành") && product.getGuarantee().equalsIgnoreCase("Còn bảo hành")) {
                guaranteeFilteredList.add(product);
            } else if (guarantee.equalsIgnoreCase("Hết bảo hành") && product.getGuarantee().equalsIgnoreCase("Hết bảo hành")) {
                guaranteeFilteredList.add(product);
            }
        }

        // Cập nhật danh sách trong adapter
        filteredList.clear();
        filteredList.addAll(guaranteeFilteredList);
        productAdapter.notifyDataSetChanged();

        // Kiểm tra nếu không có sản phẩm
        checkEmptyList();
    }

    private void showPriceFilterDialog() {
        // Tạo BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.filter_price_dialog, null);
        dialog.setContentView(dialogView);

        // Ánh xạ các nút trong dialog (price options)
        Button btn_0_3m = dialogView.findViewById(R.id.btn_0_3m);
        Button btn_3m_7m = dialogView.findViewById(R.id.btn_3m_7m);
        Button btn_7m_12m = dialogView.findViewById(R.id.btn_7m_12m);
        Button btn_above_12m = dialogView.findViewById(R.id.btn_above_12m);

        // Xử lý sự kiện khi người dùng chọn khoảng giá
        View.OnClickListener priceClickListener = v -> {
            String priceRange = ((Button) v).getText().toString();
            filterProductsByPrice(priceRange);
            btn_filter_price.setText(priceRange); // Cập nhật text của nút
            dialog.dismiss(); // Đóng dialog
        };

        btn_0_3m.setOnClickListener(priceClickListener);
        btn_3m_7m.setOnClickListener(priceClickListener);
        btn_7m_12m.setOnClickListener(priceClickListener);
        btn_above_12m.setOnClickListener(priceClickListener);

        dialog.show();
    }
    private void filterProductsByPrice(String priceRange) {
        List<Product> priceFilteredList = new ArrayList<>();

        // Lọc theo khoảng giá
        for (Product product : productList) {
            double price = product.getProductPrice();
            switch (priceRange) {
                case "0 - 3.000.000":
                    if (price >= 0 && price <= 3000000) {
                        priceFilteredList.add(product);
                    }
                    break;
                case "3.000.000 - 7.000.000":
                    if (price > 3000000 && price <= 7000000) {
                        priceFilteredList.add(product);
                    }
                    break;
                case "7.000.000 - 12.000.000":
                    if (price > 7000000 && price <= 12000000) {
                        priceFilteredList.add(product);
                    }
                    break;
                case "> 12.000.000":
                    if (price > 12000000) {
                        priceFilteredList.add(product);
                    }
                    break;
            }
        }

        // Cập nhật danh sách trong adapter
        filteredList.clear();
        filteredList.addAll(priceFilteredList);
        productAdapter.notifyDataSetChanged();

       checkEmptyList();
    }


}