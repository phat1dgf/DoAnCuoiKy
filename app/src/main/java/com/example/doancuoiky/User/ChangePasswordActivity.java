package com.example.doancuoiky.User;

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

import com.example.doancuoiky.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {
    ImageButton btn_back;
    EditText et_old_pass, et_new_pass, et_pass_confirmed;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    Button btn_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_new_pass = findViewById(R.id.et_new_password);
        et_old_pass = findViewById(R.id.et_old_password);
        et_pass_confirmed = findViewById(R.id.et_confirm_password);
        btn_confirm = findViewById(R.id.btn_change_password);
        if(user == null){
            finish();
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = et_old_pass.getText().toString().trim();
                String newPassword = et_new_pass.getText().toString().trim();
                String confirmPassword = et_pass_confirmed.getText().toString().trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu xác nhận không trùng khớp.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Xác thực lại người dùng trước khi đổi mật khẩu
                String email = user.getEmail(); // Email đã đăng nhập của người dùng
                if (email == null) {
                    Toast.makeText(ChangePasswordActivity.this, "Không thể xác thực người dùng.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

                user.reauthenticate(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Đổi mật khẩu sau khi xác thực thành công
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(updateTask -> {
                                            if (updateTask.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut(); // Đăng xuất để người dùng đăng nhập lại
                                                finish(); // Đóng activity
                                            } else {
                                                Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thất bại: " + updateTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Xác thực thất bại: Mật khẩu cũ không đúng.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}