package com.example.doancuoiky.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Models.User;
import com.example.doancuoiky.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    String uid;
    FirestoreHelper firestoreHelper = new FirestoreHelper();
    CircleImageView img_profile;
    ImageButton btn_back, btn_gallery;
    EditText et_birthday, et_email, et_phone, et_username;
    Button btn_update;

    int SELECT_PICTURE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchUserProfile();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        img_profile = findViewById(R.id.img_profile);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_birthday = findViewById(R.id.et_birthday);
        et_birthday.setOnClickListener(v -> {
            showDatePicker();
        });

        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_username = findViewById(R.id.et_username);

        et_email.setText(user.getEmail());

        btn_update = findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
    }
    private void showDatePicker(){
        // Lấy ngày hiện tại
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(UserProfileActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Định dạng và hiển thị ngày sinh trong EditText
                    String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    et_birthday.setText(formattedDate);
                },
                year,
                month,
                day
        );

        // Giới hạn ngày để chỉ chọn được ngày trong quá khứ
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }
    private void updateUserProfile() {
        String username = et_username.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        String birthday = et_birthday.getText().toString().trim();

        // Kiểm tra nếu các trường dữ liệu không rỗng
        if (!username.isEmpty() && !phone.isEmpty() && !birthday.isEmpty()) {
            // Chuyển ảnh trong ImageView thành Bitmap
            img_profile.setDrawingCacheEnabled(true);
            img_profile.buildDrawingCache();
            Bitmap imgProfileBitmap = ((BitmapDrawable) img_profile.getDrawable()).getBitmap();

            // Lưu thông tin người dùng và ảnh vào Firestore
            firestoreHelper.updateUserData(this, username, phone, birthday, imgProfileBitmap);
        } else {
            Toast.makeText(UserProfileActivity.this, "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchUserProfile() {
        firestoreHelper.getUserData(uid,new FirestoreHelper.UserDataCallback() {
            @Override
            public void onSuccess(User user) {
                // Dữ liệu người dùng được lấy thành công
                if (user != null) {
                    et_username.setText(user.getUsername());
                    et_phone.setText(user.getPhone());
                    et_birthday.setText(user.getBirthday());

                    // Giải mã Base64 và set ảnh vào ImageView
                    if (user.getImgProfileUrl() != null && !user.getImgProfileUrl().isEmpty()) {
                        Bitmap imgProfile = decodeBase64ToBitmap(user.getImgProfileUrl());
                        img_profile.setImageBitmap(imgProfile);
                    } else {
                        // Nếu không có ảnh profile, hiển thị ảnh mặc định
                        Glide.with(UserProfileActivity.this)
                                .load(R.drawable.blank_user)  // Ảnh mặc định khi không có URL
                                .into(img_profile);
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(UserProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Giải mã Base64 thành ảnh Bitmap
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // this function is triggered when
    // the Select Image Button is clicked
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

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    img_profile.setImageURI(selectedImageUri);
                }
            }
        }
    }
}