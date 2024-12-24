package com.example.doancuoiky.Add;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Models.Product;
import com.example.doancuoiky.Product.UserProductActivity;
import com.example.doancuoiky.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class AddFragment extends Fragment {
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

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String currentUid = currentUser.getUid();

        //---------------------------------------------------------------
        til_category = view.findViewById(R.id.menu_category);
        til_state = view.findViewById(R.id.menu_state);
        til_brand = view.findViewById(R.id.menu_brand);
        til_guarantee = view.findViewById(R.id.menu_guarantee);
        til_location = view.findViewById(R.id.menu_location);

        act_category = view.findViewById(R.id.input_category);
        act_state = view.findViewById(R.id.input_state);
        act_brand = view.findViewById(R.id.input_brand);
        act_guarantee = view.findViewById(R.id.input_guarantee);
        act_location = view.findViewById(R.id.input_location);

        etProductName = view.findViewById(R.id.et_product_name);
        etProductPrice = view.findViewById(R.id.et_product_price);
        etProductDescription = view.findViewById(R.id.et_product_description);

        tvErrorTittle = view.findViewById(R.id.tv_error_title);
        tvErrorPrice = view.findViewById(R.id.tv_error_price);
        tvErrorDescription = view.findViewById(R.id.tv_error_description);

        btnAccept = view.findViewById(R.id.btn_accept);

        //------------------------------------------------------------------
        imgSelected = view.findViewById(R.id.img_selected_image);
        btnSelectImg = view.findViewById(R.id.btn_select_image);
        tvErrorImage = view.findViewById(R.id.tv_error_image);


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
                    Product newProduct = new Product(
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

                    // Lưu sản phẩm vào Firestore
                    firestoreHelper.saveProductData(getActivity(), newProduct);

                    // Chuyển sang trang sản phẩm người dùng
                    goToUserProductPage(newProduct);
                }
            }
        });

        return view;
    }


    // Chuyển ảnh Bitmap thành chuỗi Base64
    private String encodeImageToBase64(Bitmap imgProfile) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imgProfile.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
//    private void saveProduct(String userID,
//                             Bitmap productImageSource,
//                             String productState,
//                             String productName,
//                             int productPrice,
//                             String location,
//                             String category,
//                             String brandName,
//                             String guarantee,
//                             String description) {
//        firestoreHelper.saveProductData(getActivity(),userID,productImageSource,productState,productName,productPrice,location,category,brandName,guarantee,description);
//    }

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
        Intent intent =new Intent(getActivity(), UserProductActivity.class);
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
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
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
            inputStream = getActivity().getContentResolver().openInputStream(imageUri);
            Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return resizedBitmap;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}