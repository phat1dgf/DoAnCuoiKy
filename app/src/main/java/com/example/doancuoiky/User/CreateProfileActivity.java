package com.example.doancuoiky.User;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.MainActivity;
import com.example.doancuoiky.R;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {

    EditText et_birthday, et_email, et_phone, et_username;
    CircleImageView img_profile;
    Button btn_create;
    ImageButton btn_gallery;
    FirestoreHelper firestoreHelper;

    int SELECT_PICTURE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firestoreHelper = new FirestoreHelper();

        img_profile = findViewById(R.id.img_profile);
        btn_gallery = findViewById(R.id.btn_gallery);

        et_phone = findViewById(R.id.et_phone);
        et_username = findViewById(R.id.et_username);
        et_birthday = findViewById(R.id.et_birthday);
        et_birthday.setOnClickListener(v -> {
            showDatePicker();
        });
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });
        btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateProfileActivity.this,
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
    private void saveUserProfile() {
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
            firestoreHelper.saveUserData(this, username, phone, birthday, imgProfileBitmap);

            goToHomePage();
        } else {
            Toast.makeText(CreateProfileActivity.this, "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHomePage() {
        Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }
}