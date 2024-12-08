package com.example.doancuoiky.Product;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doancuoiky.Instance.Product;
import com.example.doancuoiky.R;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductActivity extends AppCompatActivity {

    ImageView imgProduct;
    TextView productName, productPrice, productState, productLocation;

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

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            return; // Thoát nếu không có dữ liệu
        }

        Product product = (Product) bundle.get("product");

        if (product == null) {
            return; // Thoát nếu không có đối tượng product
        }

        imgProduct = findViewById(R.id.img_product);
        productName = findViewById(R.id.tv_product_name);
        productPrice = findViewById(R.id.tv_product_price);
        productState = findViewById(R.id.tv_product_state);
        productLocation = findViewById(R.id.tv_product_location);


        imgProduct.setImageURI(Uri.parse(product.getProductImageSource())); // Gán ảnh
        productName.setText(product.getProductName()); // Gán tên sản phẩm


        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getProductPrice());
        productPrice.setText(formattedPrice);

        productState.setText(product.getProductState());

        productLocation.setText(product.getLocation());

    }
}