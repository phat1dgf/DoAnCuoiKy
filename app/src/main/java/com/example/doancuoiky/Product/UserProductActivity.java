package com.example.doancuoiky.Product;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.R;

import java.text.NumberFormat;
import java.util.Locale;

public class UserProductActivity extends AppCompatActivity {

    ImageView imgProduct;
    TextView tvProductName, tvProductPrice, tvProductState,tvProductLocation,tvProductDescription;
    Button btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_product);
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
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductState = findViewById(R.id.tv_product_state);
        tvProductLocation = findViewById(R.id.tv_product_location);
        tvProductDescription = findViewById(R.id.tv_product_description);

        btnDelete = findViewById(R.id.btn_delete);
        btnUpdate = findViewById(R.id.btn_update);

        //---------------------------------------------------------
        imgProduct.setImageURI(Uri.parse(product.getProductImageSource()));
        tvProductName.setText(product.getProductName());
        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getProductPrice());
        tvProductPrice.setText(formattedPrice);
        tvProductState.setText(product.getProductState());
        tvProductLocation.setText(product.getLocation());
        tvProductDescription.setText(product.getDescription());


    }


}