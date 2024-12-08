package com.example.doancuoiky.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doancuoiky.R;
import com.example.doancuoiky.Search.SearchFragment;

public class HomeFragment extends Fragment {



    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        if (savedInstanceState == null) { // Tránh gắn lại khi xoay màn hình
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_homepage, new HomePageFragment())
                    .commit();
        }
        if (savedInstanceState == null) { // Tránh gắn lại khi xoay màn hình
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_searchbar, new SearchFragment())
                    .commit();
        }

        return view;
    }
}