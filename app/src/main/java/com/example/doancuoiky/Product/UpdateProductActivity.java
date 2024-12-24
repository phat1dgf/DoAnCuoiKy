package com.example.doancuoiky.Product;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UpdateProductActivity extends AppCompatActivity {

    TextInputLayout til_category, til_state, til_brand,til_guarantee,til_location;
    MaterialAutoCompleteTextView act_category, act_state,act_brand,act_guarantee,act_location;
    Button btnAccept;
    EditText etProductName, etProductPrice, etProductDescription;
    TextView tvErrorTittle,tvErrorPrice,tvErrorDescription, tvErrorImage;
    //----------------------------------------------------------
    Uri selectedImageUri;
    ImageView imgSelected;
    Button btnSelectImg;
    int SELECT_PICTURE = 200;
    boolean isImageSelected = false;
    //-----------------------------------------------------
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirestoreHelper firestoreHelper = new FirestoreHelper();

    String productID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_product);
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
        productID = product.getId();
        if (product == null) {
            return; // Thoát nếu không có đối tượng product
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String currentUid = currentUser.getUid();

        //---------------------------------------------------------------
        til_category = findViewById(R.id.menu_category);
        til_state = findViewById(R.id.menu_state);
        til_brand = findViewById(R.id.menu_brand);
        til_guarantee = findViewById(R.id.menu_guarantee);
        til_location = findViewById(R.id.menu_location);

        act_category = findViewById(R.id.input_category);
        act_state = findViewById(R.id.input_state);
        act_brand = findViewById(R.id.input_brand);
        act_guarantee = findViewById(R.id.input_guarantee);
        act_location = findViewById(R.id.input_location);

        etProductName = findViewById(R.id.et_product_name);
        etProductPrice = findViewById(R.id.et_product_price);
        etProductDescription = findViewById(R.id.et_product_description);

        tvErrorTittle = findViewById(R.id.tv_error_title);
        tvErrorPrice = findViewById(R.id.tv_error_price);
        tvErrorDescription = findViewById(R.id.tv_error_description);

        btnAccept = findViewById(R.id.btn_accept);

        //------------------------------------------------------------------
        imgSelected = findViewById(R.id.img_selected_image);
        btnSelectImg = findViewById(R.id.btn_select_image);
        tvErrorImage = findViewById(R.id.tv_error_image);

        setAllInput(product);

        btnSelectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid = validateAllInputs();

                if (isValid) {
                    String productName = etProductName.getText().toString().trim();
                    int productPrice = Integer.parseInt(etProductPrice.getText().toString().trim());
                    String productDescription = etProductDescription.getText().toString().trim();
                    String productCategory = act_category.getText().toString().trim();
                    String productState = act_state.getText().toString().trim();
                    String productBrand = act_brand.getText().toString().trim();
                    String productGuarantee = act_guarantee.getText().toString().trim();
                    String productLocation = act_location.getText().toString().trim();




                    // Lấy ảnh đã chọn
                    BitmapDrawable drawable = (BitmapDrawable) imgSelected.getDrawable();
                    Bitmap imgBitmap = drawable.getBitmap();


                    // Tạo đối tượng Product mới
                    Product updatedProduct = new Product(
                            currentUid,
                            encodeImageToBase64(imgBitmap), // Truyền Bitmap vào Product
                            productState,
                            productName,
                            productPrice,
                            productLocation,
                            productCategory,
                            productBrand,
                            productGuarantee,
                            productDescription
                    );

                    updateProduct(updatedProduct);
                    finish();
                }
            }

        });

    }

    private void updateProduct(Product product) {

        firestoreHelper.updateProductData(product,productID, new FirestoreHelper.UpdateProductCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(UpdateProductActivity.this,message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UpdateProductActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllInput(Product product) {
        // Set the product name, price, description
        etProductName.setText(product.getProductName());
        etProductPrice.setText(String.valueOf(product.getProductPrice()));
        etProductDescription.setText(product.getDescription());

        // Set the category, state, brand, guarantee, and location dropdown values
        act_category.setText(product.getCategory(), false);  // 'false' prevents the dropdown from showing
        act_state.setText(product.getProductState(), false);
        act_brand.setText(product.getBrandName(), false);
        act_guarantee.setText(product.getGuarantee(), false);
        act_location.setText(product.getLocation(), false);

        // Decode the Base64 image and set it to the ImageView
        Bitmap img = decodeBase64ToBitmap(product.getProductImageSource());
        imgSelected.setImageBitmap(img);

        // Set the image as selected (this will ensure the validation works for image presence)
        isImageSelected = true;
    }


    // Chuyển ảnh Bitmap thành chuỗi Base64
    private String encodeImageToBase64(Bitmap imgProfile) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgProfile.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Giải mã Base64 thành ảnh Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private boolean validateAllInputs() {
        boolean isTextInputValid = textinputHandle();
        boolean isDropdownValid = dropdownHandle();
        if (!isImageSelected) {
            tvErrorImage.setText("Vui lòng chọn ảnh");
        } else {
            tvErrorImage.setText(null);
        }

        return isTextInputValid && isDropdownValid && isImageSelected;
    }

    private boolean textinputHandle() {
        boolean isValid = true;

        EditText[] editTexts = new EditText[]{
                etProductName, etProductPrice, etProductDescription
        };
        TextView[] textViewsError = new TextView[]{
                tvErrorTittle, tvErrorPrice, tvErrorDescription
        };
        for (int i = 0; i < editTexts.length; i++) {
            String input = editTexts[i].getText().toString().trim();
            if (input.isEmpty()) {
                textViewsError[i].setText("Vui lòng nhập đầy đủ");
                isValid = false; // Mark invalid if any field is empty
            } else {
                textViewsError[i].setText(null);
            }
        }
        return isValid;
    }

    private boolean dropdownHandle() {
        boolean isValid = true;

        TextInputLayout[] inputLayouts = new TextInputLayout[]{
                til_category, til_state, til_brand, til_guarantee, til_location
        };

        MaterialAutoCompleteTextView[] autoCompleteTextViews = new MaterialAutoCompleteTextView[]{
                act_category, act_state, act_brand, act_guarantee, act_location
        };

        for (int i = 0; i < autoCompleteTextViews.length; i++) {
            String input = autoCompleteTextViews[i].getText().toString().trim();
            if (input.isEmpty()) {
                inputLayouts[i].setError("Please select an option");
                isValid = false; // Mark invalid if any dropdown is empty
            } else {
                inputLayouts[i].setError(null); // Clear the error
            }
        }
        return isValid;
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                // Get the image Uri
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Reduce image size before setting it
                    Bitmap resizedBitmap = getResizedBitmap(selectedImageUri, 800); // You can adjust the size (e.g. 800)
                    imgSelected.setImageBitmap(resizedBitmap); // Set resized image to ImageView
                    isImageSelected = true;
                }
            }
        }
    }

    private void goToUserProductPage(Product product){
        Intent intent =new Intent(UpdateProductActivity.this, UserProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product",product);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private Bitmap getResizedBitmap(Uri imageUri, int maxSize) {
        try {
            // Đọc thông tin về kích thước ảnh mà không tải toàn bộ ảnh vào bộ nhớ
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = this.getContentResolver().openInputStream(imageUri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Tính toán tỷ lệ giảm kích thước ảnh (scale)
            int scale = 1;
            while (options.outWidth / scale > maxSize || options.outHeight / scale > maxSize) {
                scale *= 2;
            }

            // Đọc ảnh thực tế với tỷ lệ đã tính toán
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            inputStream = this.getContentResolver().openInputStream(imageUri);
            Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return resizedBitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}