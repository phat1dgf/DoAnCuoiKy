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
import com.example.doancuoiky.Product.UserProductActivity;
import com.example.doancuoiky.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class UserFeedActivity extends AppCompatActivity {
    ImageButton btn_back;
    ListView lv_user_feeds;
    UserFeedAdapter userFeedAdapter;
    ArrayList<Product> productList, userProducts;
    FirestoreHelper firestoreHelper;
    String uid;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_user_feed);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        productList = new ArrayList<>();
//        userProducts = new ArrayList<>();
//        firestoreHelper = new FirestoreHelper();
//        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        btn_back = findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        lv_user_feeds = findViewById(R.id.lv_user_feeds);
//        userFeedAdapter = new UserFeedAdapter(UserFeedActivity.this,R.layout.product_item,productList);
//        lv_user_feeds.setAdapter(userFeedAdapter);
//        lv_user_feeds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Product selectedProduct = userProducts.get(i);
//                goToUserProductPage(selectedProduct);
//            }
//        });
//
//        fetchProductList();
//        getListUserFeed();
//    }
//
//    private void getListUserFeed() {
//        for (Product product: productList) {
//            if(product.getUserID().equals(uid)){
//                userProducts.add(product);
//            }
//        }
//        userFeedAdapter.notifyDataSetChanged();
//    }
//    private void goToUserProductPage(Product product){
//        Intent intent =new Intent(UserFeedActivity.this, UserProductActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("product",product);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }
//    private void fetchProductList() {
//        firestoreHelper.getAllProducts(new FirestoreHelper.ProductListCallback() {
//            @Override
//            public void onSuccess(List<Product> products) {
//                productList.clear();
//                productList.addAll(products);
//
//                if (productList.isEmpty()) {
//                    Toast.makeText(UserFeedActivity.this, "Không có sản phẩm nào.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(String errorMessage) {
//                Toast.makeText(UserFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this);
    setContentView(R.layout.activity_user_feed);

    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });

    productList = new ArrayList<>();
    userProducts = new ArrayList<>();
    firestoreHelper = new FirestoreHelper();
    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    btn_back = findViewById(R.id.btn_back);
    btn_back.setOnClickListener(view -> finish());

    lv_user_feeds = findViewById(R.id.lv_user_feeds);
    userFeedAdapter = new UserFeedAdapter(UserFeedActivity.this, R.layout.product_item, userProducts);
    lv_user_feeds.setAdapter(userFeedAdapter);

    lv_user_feeds.setOnItemClickListener((adapterView, view, i, l) -> {
        Product selectedProduct = userProducts.get(i);
        goToUserProductPage(selectedProduct);
    });

    fetchProductList(); // Fetch danh sách sản phẩm từ Firestore
}

    private void fetchProductList() {
        firestoreHelper.getAllProducts(new FirestoreHelper.ProductListCallback() {
            @Override
            public void onSuccess(List<Product> products) {
                productList.clear();
                productList.addAll(products);
                getListUserFeed(); // Cập nhật danh sách người dùng
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserFeedActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getListUserFeed() {
        userProducts.clear();
        for (Product product : productList) {
            if (product.getUserID().equals(uid)) {
                userProducts.add(product);
            }
        }
        userFeedAdapter.notifyDataSetChanged();
    }

    private void goToUserProductPage(Product product) {
        Intent intent = new Intent(UserFeedActivity.this, UserProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}