package com.example.immigr8.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.immigr8.adapter.AlertAdapter;
import com.example.immigr8.model.Alert;
import com.example.immigr8.model.Chat;
import com.example.immigr8.model.ChatList;
import com.example.immigr8.model.CompleteUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AlertsFragment extends Fragment {

    ArrayList<Alert> alertIds = new ArrayList<>();

    // Firebase
    FirebaseUser firebaseUser;

    RecyclerView recyclerView;

    ImageView profile_picture;
    CompleteUser completeUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        /*
         * How alerts work: once user wants to be friends, he sends friend request, which logs under
         * the users firebase id. now, once user opens this fragment up, all requests will be
         * shown. if user accepts request, the user is accepted into friend list. if user does not accept
         * friend request, that user is deleted from the alert firebase database.
         */

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        profile_picture = view.findViewById(R.id.alert_profile_pic);
        completeUser = MainActivity.currentCompleteUser;

        recyclerView = view.findViewById(R.id.alert_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Firebase user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Alerts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Alert alert = dataSnapshot.getValue(Alert.class);
                    assert alert != null;
                    if (alert.getReceiver().equals(firebaseUser.getUid())) {
                        alertIds.add(alert);
                    }
                    recyclerView.setAdapter(new AlertAdapter(getContext(), alertIds));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Profile picture set up
        if (completeUser.getUser().getImageURL().equals("default")) {
            profile_picture.setImageResource(R.drawable.ic_default_user);
        } else {
            Glide.with(getContext()).load(completeUser.getUser().getImageURL()).into(profile_picture);
        }

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