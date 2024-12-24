package com.example.doancuoiky.Product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.Models.User;
import com.example.doancuoiky.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductActivity extends AppCompatActivity {

    ImageView img_Product;
    TextView productName, productPrice, productState, productLocation,tv_description,username;
    ToggleButton toggle_btn_favorite;
    Button btn_call, btn_sms;
    FirestoreHelper firestoreHelper;
    CircleImageView img_user;

    String userID;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestoreHelper = new FirestoreHelper();
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            return; // Thoát nếu không có dữ liệu
        }

        Product product = (Product) bundle.get("product");

        if (product == null) {
            return; // Thoát nếu không có đối tượng product
        }

        img_Product = findViewById(R.id.img_product);
        productName = findViewById(R.id.tv_product_name);
        productPrice = findViewById(R.id.tv_product_price);
        productState = findViewById(R.id.tv_product_state);
        productLocation = findViewById(R.id.tv_product_location);
        toggle_btn_favorite = findViewById(R.id.toggle_btn_favorite);
        btn_call = findViewById(R.id.btn_call);
        btn_sms = findViewById(R.id.btn_sms);
        username = findViewById(R.id.tv_username);
        img_user = findViewById(R.id.img_user);
        tv_description = findViewById(R.id.tv_description);

        userID = product.getUserID();
        firestoreHelper.getUserData(userID, new FirestoreHelper.UserDataCallback() {
            @Override
            public void onSuccess(User user) {
                username.setText(user.getUsername());
                phone = user.getPhone();
                Bitmap img = decodeBase64ToBitmap(user.getImgProfileUrl());
                img_user.setImageBitmap(img);
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

        toggle_btn_favorite.setOnClickListener(v -> {
            if (toggle_btn_favorite.isChecked()) {
                // Đặt trạng thái ON (Yêu thích)
                toggle_btn_favorite.setChecked(true); // Đảm bảo trạng thái ON

                // Tăng giá trị "favorite" của sản phẩm
                firestoreHelper.incrementFavorite(product.getId(), new FirestoreHelper.FavoriteProductCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                // Thêm sản phẩm vào danh sách yêu thích
                firestoreHelper.addProductToFavorites(product.getId(), new FirestoreHelper.FavoriteProductCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                // Đặt trạng thái OFF (Hủy yêu thích)
                toggle_btn_favorite.setChecked(false); // Đảm bảo trạng thái OFF

                // Giảm giá trị "favorite" của sản phẩm
                firestoreHelper.decrementFavorite(product.getId(), new FirestoreHelper.FavoriteProductCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

                // Xóa sản phẩm khỏi danh sách yêu thích
                firestoreHelper.removeProductFromFavorites(product.getId(), new FirestoreHelper.FavoriteProductCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        Bitmap imgProduct = decodeBase64ToBitmap(product.getProductImageSource());
        img_Product.setImageBitmap(imgProduct);

        productName.setText(product.getProductName()); // Gán tên sản phẩm


        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getProductPrice());
        productPrice.setText(formattedPrice);

        productState.setText(product.getProductState());

        productLocation.setText(product.getLocation());

        tv_description.setText(product.getDescription());

        firestoreHelper.updateUserHabits(product.getCategory(), getPriceRange(product.getProductPrice()), new FirestoreHelper.HabitUpdateCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(ProductActivity.this, "Habits updated.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


        btn_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phone));
                startActivity(intent);
            }
        });
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("sms:"+phone));
                startActivity(intent);
            }
        });

        checkIfProductIsFavorite(FirebaseAuth.getInstance().getCurrentUser().getUid(), product.getId());

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
    // Giải mã Base64 thành ảnh Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void checkIfProductIsFavorite(String uid, String productId) {
        firestoreHelper.getFavoriteProductIds(uid, new FirestoreHelper.FavoriteFetchCallback() {
            @Override
            public void onSuccess(List<String> favoriteIds) {
                // Kiểm tra nếu productId nằm trong danh sách yêu thích
                if (favoriteIds.contains(productId)) {
                    toggle_btn_favorite.setChecked(true);
                } else {
                    toggle_btn_favorite.setChecked(false);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(ProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}