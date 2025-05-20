package com.example.immigr8.fragments;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.immigr8.MainActivity;
import com.example.immigr8.R;
import com.example.immigr8.model.CompleteUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class RecommendedFragment extends Fragment {

    Button send_friend_request, block;
    ImageView check_mark, profilePicture, back;

    CompleteUser completeUser;

    TextView bio, age, location, immigrated, gender, hobbies, values, likes, languages, username;

    // Firebase
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    // Animation
    AlphaAnimation buttonClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recommended, container, false);

        send_friend_request = view.findViewById(R.id.recommended_send_friend_request);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert getArguments() != null;
        completeUser = (CompleteUser)getArguments().getSerializable("completeUser");

        buttonClick = new AlphaAnimation(1F, 0.8F);

        // XML set up
        block = view.findViewById(R.id.recommended_block);
        profilePicture = view.findViewById(R.id.profile_profile_image);
        username = view.findViewById(R.id.profile_username);
        bio = view.findViewById(R.id.profile_bio);
        age = view.findViewById(R.id.profile_age);
        location = view.findViewById(R.id.profile_location);
        gender = view.findViewById(R.id.profile_gender);
        hobbies = view.findViewById(R.id.profile_hobbies);
        values = view.findViewById(R.id.profile_values);
        immigrated = view.findViewById(R.id.profile_immigrated);
        likes = view.findViewById(R.id.profile_likes);
        languages = view.findViewById(R.id.profile_languages);
        back = view.findViewById(R.id.recommended_back);

        // Set up info from complete user
        username.setText(completeUser.getUser().getUsername());
        if (completeUser.getUser().getImageURL().equals("default")) {
            profilePicture.setImageResource(R.drawable.ic_default_user);
        } else {
            Glide.with(getContext()).load(completeUser.getUser().getImageURL()).into(profilePicture);
        }

        bio.setText(completeUser.getUser().getBio());
        age.setText(completeUser.getUser().getAge());
        location.setText(completeUser.getUser().getLocation());
        gender.setText(completeUser.getUser().getGender());
        immigrated.setText(completeUser.getUser().getImmigratedFrom());
        hobbies.setText(arrayListToComma(completeUser.getHobbies()));
        languages.setText(arrayListToComma(completeUser.getLanguages()));
        values.setText(arrayListToComma(completeUser.getValues()));
        likes.setText(arrayListToComma(completeUser.getLikes()));


        // Send friend request functionality
        send_friend_request.setOnClickListener(v -> {
            sendFriendRequest(firebaseUser.getUid(), completeUser.getUser().getId());
            check_mark = view.findViewById(R.id.recommended_check_mark);
            check_mark.setVisibility(View.VISIBLE);
            ((Animatable) check_mark.getDrawable()).start();
            check_mark.setVisibility(View.GONE);
            v.startAnimation(buttonClick);

            // Transfer to home page
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_holder, new HomeFragment())
                    .commit();
        });

        // Block user functionality
        block.setOnClickListener(v -> {
            blockContact(firebaseUser.getUid(), completeUser.getUser().getId());
            v.startAnimation(buttonClick);
            // Transfer to home page
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_holder, new HomeFragment())
                    .commit();
        });

        back.setOnClickListener(v -> {
            // Transfer to home page
            v.startAnimation(buttonClick);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_holder, new HomeFragment())
                    .commit();
        });

        return view;
    }

    /**
     * Blocks contact in firebase
     * @param sender sender
     * @param receiver receiver
     */
    private void blockContact(String sender, String receiver) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);

        reference.child("Blocked").child(receiver).setValue(hashMap);
    }

    /**
     * Sends a friend request through firebase
     * @param sender sender
     * @param receiver receiver
     */
    private void sendFriendRequest(String sender, String receiver) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("date", LocalDate.now().toString());

        reference.child("Alerts").child(receiver).setValue(hashMap);
    }

    private String arrayListToComma(ArrayList<String> list) {
        StringBuilder sbString = new StringBuilder("");

        //iterate through ArrayList
        for(String language : list){
            //append ArrayList element followed by comma
            sbString.append(language).append(", ");
        }

        //convert StringBuffer to String
        String strList = sbString.toString();
        //remove last comma from String
        if( strList.length() > 0 )
            strList = strList.substring(0, strList.length() - 2);

        return strList;
    }
}