package com.example.doancuoiky.Product;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.R;

import java.text.NumberFormat;
import java.util.Locale;

public class UserProductActivity extends AppCompatActivity {

    ImageView imgProduct;
    TextView tvProductName, tvProductPrice, tvProductState,tvProductLocation,tvProductDescription;
    Button btnUpdate, btnDelete;

    FirestoreHelper firestoreHelper;

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
        firestoreHelper = new FirestoreHelper();
        imgProduct = findViewById(R.id.img_product);
        tvProductName = findViewById(R.id.tv_product_name);
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvProductState = findViewById(R.id.tv_product_state);
        tvProductLocation = findViewById(R.id.tv_product_location);
        tvProductDescription = findViewById(R.id.tv_product_description);

        btnDelete = findViewById(R.id.btn_delete);
        btnUpdate = findViewById(R.id.btn_update);

        //---------------------------------------------------------
        Bitmap img = decodeBase64ToBitmap(product.getProductImageSource());
        imgProduct.setImageBitmap(img);
        tvProductName.setText(product.getProductName());
        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getProductPrice());
        tvProductPrice.setText(formattedPrice);
        tvProductState.setText(product.getProductState());
        tvProductLocation.setText(product.getLocation());
        tvProductDescription.setText(product.getDescription());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product != null) {

                    goToUpdateProductPage(product);
                } else {
                    Toast.makeText(UserProductActivity.this,"Product is null",Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestoreHelper.deleteProductById(product.getId(), new FirestoreHelper.DeleteProductCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(UserProductActivity.this, message, Toast.LENGTH_SHORT).show();
                        // Kết thúc activity sau khi xóa
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(UserProductActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void goToUpdateProductPage(Product product) {
        Intent intent = new Intent(UserProductActivity.this, UpdateProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Giải mã Base64 thành ảnh Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}