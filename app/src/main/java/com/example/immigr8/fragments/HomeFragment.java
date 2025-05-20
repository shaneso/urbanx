package com.example.immigr8.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.immigr8.MainActivity;
import com.example.immigr8.R;
import com.example.immigr8.adapter.RecommendedAdapter;
import com.example.immigr8.model.CompleteUser;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<CompleteUser> recommendedList = new ArrayList<>();

    ImageView profile_picture;

    RecyclerView recommendedRecycler;
    CompleteUser completeUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recommendedList.addAll(MainActivity.recommendedList);
        completeUser = MainActivity.currentCompleteUser;

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        // XML set up
        profile_picture = view.findViewById(R.id.home_profile_image_image);

        // Profile picture set up
        if (completeUser.getUser().getImageURL().equals("default")) {
            profile_picture.setImageResource(R.drawable.ic_default_user);
        } else {
            Glide.with(getContext()).load(completeUser.getUser().getImageURL()).into(profile_picture);
        }

        recommendedRecycler = view.findViewById(R.id.home_recommended_top_recycler);
        recommendedRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recommendedRecycler.setLayoutManager(linearLayoutManager);
        recommendedRecycler.setAdapter(new RecommendedAdapter(getContext(), recommendedList, R.layout.home_recommended));

        // When user clicks on profile picture
        profile_picture.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            Fragment fragment = new ProfileFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_holder, fragment)
                    .commit();
        });

        return view;
    }
}