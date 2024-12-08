package com.example.doancuoiky;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.doancuoiky.Instance.Product;
import com.example.doancuoiky.Product.ProductActivity;
import com.example.doancuoiky.Product.UserProductActivity;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


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
                    int productPrice = Integer.parseInt(etProductPrice.getText().toString().trim()) ;
                    String productDescription = etProductDescription.getText().toString().trim();
                    String productCategory = act_category.getText().toString().trim();
                    String productState = act_state.getText().toString().trim();
                    String productBrand = act_brand.getText().toString().trim();
                    String productGuarantee = act_guarantee.getText().toString().trim();
                    String productLocation = act_location.getText().toString().trim();

//                    imgSelected.setDrawingCacheEnabled(true);
//                    imgSelected.buildDrawingCache();
//                    Bitmap productImage = ((BitmapDrawable) imgSelected.getDrawable()).getBitmap();
                    String productImage = selectedImageUri.toString();
                    Product newProduct = new Product(currentUid,productImage,productState,productName,productPrice,productLocation,productCategory,productBrand,productGuarantee,productDescription);

                    onClickGoToUserProductPage(newProduct);
                }
            }
        });


        return view;
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

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // update the preview image in the layout
                    imgSelected.setImageURI(selectedImageUri);
                    isImageSelected = true;
                }
            }
        }
    }
    private void onClickGoToUserProductPage(Product product){
        Intent intent =new Intent(getActivity(), UserProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("product",product);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}