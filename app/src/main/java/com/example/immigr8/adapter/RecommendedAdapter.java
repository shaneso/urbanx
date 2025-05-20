package com.example.immigr8.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.immigr8.R;
import com.example.immigr8.fragments.RecommendedFragment;
import com.example.immigr8.model.CompleteUser;
import com.example.immigr8.model.CompleteUser;

import java.util.ArrayList;
import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> {

    private Context context;
    private List<CompleteUser> mRecommended;

    // Layout
    final int layout;

    public RecommendedAdapter(Context context, ArrayList<CompleteUser> mRecommended, int layout) {
        this.mRecommended = mRecommended;
        this.context = context;
        this.layout = layout;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_recommended,
                    parent,
                    false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CompleteUser completeUser = mRecommended.get(position);
        holder.username.setText(completeUser.getUser().getUsername());

        String temp = "Age: " + completeUser.getUser().getAge();
        if (completeUser.getUser().getAge().equals("")) {
            holder.age.setText("Age: N/A");
        } else {
            holder.age.setText(temp);
        }

        if (completeUser.getUser().getGender().equals("")) {
            holder.gender.setText("N/A");
        } else {
            holder.gender.setText(completeUser.getUser().getGender());
        }

        if (completeUser.getUser().getBio().equals("")) {
            holder.bio.setText("No biography.");
        } else {
            holder.bio.setText(completeUser.getUser().getBio());
        }

        if (Double.parseDouble(completeUser.getUser().getTimeAsImmigrant()) >= 12) {
            holder.star.setVisibility(View.VISIBLE);
        }

        if (completeUser.getUser().getImageURL().equals("default")) {
            holder.profile_image.setImageResource(R.drawable.ic_default_user);
        } else {
            Glide.with(context).load(completeUser.getUser().getImageURL()).into(holder.profile_image);
        }

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        holder.itemView.setOnClickListener(v -> {
            // When user clicks on holder
            v.startAnimation(buttonClick);
            Fragment fragment = new RecommendedFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("completeUser", completeUser);
            fragment.setArguments(bundle);
            ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_holder, fragment)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return mRecommended.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, age, gender, bio;
        public ImageView profile_image, star;

        public ViewHolder(View itemView) {
            super(itemView);
            star = itemView.findViewById(R.id.home_star);
            username = itemView.findViewById(R.id.home_username);
            profile_image = itemView.findViewById(R.id.home_profile_image_image_image);
            age = itemView.findViewById(R.id.home_age);
            gender = itemView.findViewById(R.id.home_gender);
            bio = itemView.findViewById(R.id.home_bio);
        }

    }
}
