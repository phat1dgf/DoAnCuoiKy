package com.example.doancuoiky.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doancuoiky.Adapter.UserFeedAdapter;
import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.Product.ProductActivity;
import com.example.doancuoiky.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFeedActivity extends AppCompatActivity {
    ImageButton btn_back;
    ListView lv_favorite_feeds;
    UserFeedAdapter userFeedAdapter;
    List<Product> productList;
    List<String> favoriteProductIds; // Lưu trữ danh sách các productId mà người dùng yêu thích
    FirestoreHelper firestoreHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_feed);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firestoreHelper = new FirestoreHelper();
        productList = new ArrayList<>();
        favoriteProductIds = new ArrayList<>(); // Khởi tạo danh sách các sản phẩm yêu thích
        fetchProductList();
        fetchFavoriteProductIds();

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(view -> finish());

        lv_favorite_feeds = findViewById(R.id.lv_favorite_feeds);
        userFeedAdapter = new UserFeedAdapter(FavoriteFeedActivity.this, R.layout.product_item, getListFavoriteFeed());
        lv_favorite_feeds.setAdapter(userFeedAdapter);

        lv_favorite_feeds.setOnItemClickListener((adapterView, view, i, l) -> {
            Product selectedProduct = getListFavoriteFeed().get(i);
            goToUserProductPage(selectedProduct);
        });
    }

    // Lấy danh sách các sản phẩm yêu thích
    private ArrayList<Product> getListFavoriteFeed() {
        ArrayList<Product> list = new ArrayList<>();
        for (Product product : productList) {
            if (favoriteProductIds.contains(product.getId())) {
                list.add(product);
            }
        }
        return list;
    }

    // Chuyển đến trang chi tiết sản phẩm
    private void goToUserProductPage(Product product) {
        Intent intent = new Intent(FavoriteFeedActivity.this, ProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Fetch danh sách tất cả sản phẩm từ Firestore
    private void fetchProductList() {
        firestoreHelper.getAllProducts(new FirestoreHelper.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productList.clear();
                productList.addAll(products);
                userFeedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(FavoriteFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Fetch danh sách các sản phẩm mà người dùng đã yêu thích
    private void fetchFavoriteProductIds() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreHelper.getFavoriteProductIds(uid, new FirestoreHelper.FavoriteFetchCallback(){
            @Override
            public void onSuccess(List<String> favoriteIds) {
                favoriteProductIds.clear();
                favoriteProductIds.addAll(favoriteIds);
                userFeedAdapter.notifyDataSetChanged();  // Cập nhật lại adapter khi dữ liệu yêu thích đã có
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(FavoriteFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}