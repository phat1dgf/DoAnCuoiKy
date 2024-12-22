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
import android.widget.Toast;

import com.example.doancuoiky.Adapter.CategoryAdapter;
import com.example.doancuoiky.Adapter.DiscoverProductAdapter;
import com.example.doancuoiky.Adapter.PopularProductAdapter;
import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Interface.IClickProductItemListener;
import com.example.doancuoiky.Models.Category;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.Product.ProductActivity;
import com.example.doancuoiky.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class HomePageFragment extends Fragment {

    RecyclerView gvCategory;
    RecyclerView lvPopular;
    RecyclerView gvDiscover;

    private FirestoreHelper firestoreHelper;
    private List<Product> productList;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    List<Product> discoverProducts;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        firestoreHelper = new FirestoreHelper();
        productList = new ArrayList<>();
        discoverProducts = new ArrayList<>();

        gvCategory = view.findViewById(R.id.gv_category);
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext());
        GridLayoutManager categoryGridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        gvCategory.setLayoutManager(categoryGridLayoutManager);
        categoryAdapter.setData(getListCategory());
        gvCategory.setAdapter(categoryAdapter);

        lvPopular = view.findViewById(R.id.lv_popular);
        PopularProductAdapter popularAdapter = new PopularProductAdapter(getListPopularProduct(), new IClickProductItemListener() {
            @Override
            public void onClickProductItem(Product product) {
                onClickGoToProductPage(product);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        lvPopular.setLayoutManager(linearLayoutManager);
        lvPopular.setAdapter(popularAdapter);

        gvDiscover = view.findViewById(R.id.gv_discover);
        DiscoverProductAdapter discoverAdapter = new DiscoverProductAdapter(discoverProducts, new IClickProductItemListener() {
            @Override
            public void onClickProductItem(Product product) {
                onClickGoToProductPage(product);
            }
        });
        GridLayoutManager discoverGridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        gvDiscover.setLayoutManager(discoverGridLayoutManager);
        gvDiscover.setAdapter(discoverAdapter);

        fetchProductList(); // Gọi fetchProductList, sau đó fetchUserHabits trong onSuccess

        return view;
    }


    private List<Product> getListDiscoverProduct(Map<String, Long> categories, Map<String, Long> priceRanges) {
        List<Product> list = new ArrayList<>();

        for (Product product : productList) {
            String productCategory = product.getCategory();
            String priceRange = getPriceRange(product.getProductPrice()); // Hàm xác định price range

            // Kiểm tra nếu category và price range có trong habits
            if (categories.containsKey(productCategory) && priceRanges.containsKey(priceRange)) {
                list.add(product);
            }

            // Giới hạn số lượng sản phẩm tối đa là 20
            if (list.size() >= 20) {
                break;
            }
        }
        return list;
    }



    private List<Product> getListPopularProduct() {
        // Sao chép danh sách gốc để tránh thay đổi productList
        List<Product> sortedList = new ArrayList<>(productList);

        // Sắp xếp danh sách theo số lượng favorite (giảm dần)
        sortedList.sort((p1, p2) -> Integer.compare(p2.getFavorite(), p1.getFavorite()));

        // Trả về tối đa 20 sản phẩm
        return sortedList.size() > 20 ? sortedList.subList(0, 20) : sortedList;
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

    private void fetchProductList() {

        firestoreHelper.getAllProducts(new FirestoreHelper.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productList.clear();
                productList.addAll(products);

                // Chỉ gọi fetchUserHabits sau khi productList đã được tải xong
                //fetchUserHabits();
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserHabits() {
        firestoreHelper.getUserHabits(uid, new FirestoreHelper.HabitCallback() {
            @Override
            public void onSuccess(Map<String, Long> categories, Map<String, Long> priceRanges) {
                discoverProducts.clear(); // Xóa danh sách cũ nếu có
                discoverProducts.addAll(getListDiscoverProduct(categories, priceRanges));

                // Cập nhật adapter
                gvDiscover.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getPriceRange(int price) {
        if (price <= 3000000) {
            return "0-3.000.000";
        } else if (price <= 7000000) {
            return "3.000.000-7.000.000";
        } else if (price <= 12000000) {
            return "7.000.000-12.000.000";
        } else {
            return ">12.000.000";
        }
    }

}