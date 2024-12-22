package com.example.doancuoiky.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.doancuoiky.Adapter.UserOptionAdapter;
import com.example.doancuoiky.Auth.LoginActivity;
import com.example.doancuoiky.Helper.FirestoreHelper;
import com.example.doancuoiky.Models.User;
import com.example.doancuoiky.Models.UserOption;
import com.example.doancuoiky.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserFragment extends Fragment {

    ArrayList<UserOption> feedOptions,accountOptions;
    ListView lv_feedOption, lv_accountOption;
    UserOptionAdapter feedOptionAdapter, accountOptionAdapter;

    TextView tv_username;
    CircleImageView img_avatar;

    FirestoreHelper firestoreHelper = new FirestoreHelper();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);



        lv_feedOption = view.findViewById(R.id.lv_feed_option);
        lv_accountOption = view.findViewById(R.id.lv_account_option);

        feedOptions = new ArrayList<>();
        accountOptions = new ArrayList<>();

        feedOptions.add(new UserOption("Quản lý tin đăng",R.drawable.feed_icon));
        feedOptions.add(new UserOption("Tin đã lưu",R.drawable.favorite_icon));
        accountOptions.add(new UserOption("Hồ sơ của bạn",R.drawable.user_icon));
        accountOptions.add(new UserOption("Đổi mật khẩu",R.drawable.setting_icon));
        accountOptions.add(new UserOption("Đăng xuất",R.drawable.signout_icon));

        feedOptionAdapter = new UserOptionAdapter(getActivity(),R.layout.user_option_item,feedOptions);
        accountOptionAdapter = new UserOptionAdapter(getActivity(),R.layout.user_option_item,accountOptions);

        lv_feedOption.setAdapter(feedOptionAdapter);
        lv_accountOption.setAdapter(accountOptionAdapter);

        lv_feedOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: // Quản lý tin đăng
                        Intent intent1 = new Intent(getActivity(), UserFeedActivity.class);
                        startActivity(intent1);
                        break;
                    case 1: // Tin đã lưu
                        Intent intent2 = new Intent(getActivity(), FavoriteFeedActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        });
        lv_accountOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: // Hồ sơ
                        Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                        startActivity(intent);
                        break;
                    case 1: // change pass
                        Intent intent1 = new Intent(getActivity(), ChangePasswordActivity.class);
                        startActivity(intent1);
                        break;
                    case 2: // Sign out
                        signOut();
                        break;
                }
            }
        });

        tv_username = view.findViewById(R.id.tv_username);
        img_avatar = view.findViewById(R.id.img_avatar);
        fetchUserData();
        return view;
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent2 = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent2);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
    private void fetchUserData() {
        firestoreHelper.getUserData(uid ,new FirestoreHelper.UserDataCallback() {
            @Override
            public void onSuccess(User user) {
                // Dữ liệu người dùng được lấy thành công
                if (user != null) {
                    tv_username.setText(user.getUsername());

                    // Giải mã Base64 và set ảnh vào ImageView
                    if (user.getImgProfileUrl() != null && !user.getImgProfileUrl().isEmpty()) {
                        Bitmap imgProfile = decodeBase64ToBitmap(user.getImgProfileUrl());
                        img_avatar.setImageBitmap(imgProfile);
                    } else {
                        // Nếu không có ảnh profile, hiển thị ảnh mặc định
                        Glide.with(getActivity())
                                .load(R.drawable.blank_user)  // Ảnh mặc định khi không có URL
                                .into(img_avatar);
                    }
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}