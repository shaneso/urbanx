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
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.example.immigr8.MainActivity;
import com.example.immigr8.R;
import com.example.immigr8.adapter.ChatAdapter;
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

public class MessagingFragment extends Fragment {

    ArrayList<String> friendIds = new ArrayList<>();

    FirebaseUser firebaseUser;

    ChatAdapter chatAdapter;

    RecyclerView recyclerView;
    SearchView searchView;
    ImageView profile_picture;
    CompleteUser completeUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        // Firebase user set up
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        completeUser = MainActivity.currentCompleteUser;

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        // Finds user in the Friends list then gets all friends.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    if (chatList.getReceiver().equals(firebaseUser.getUid()) || chatList.getSender().equals(firebaseUser.getUid())) {
                        if (chatList.getReceiver().equals(firebaseUser.getUid())) {
                            friendIds.add(chatList.getSender());
                        } else {
                            friendIds.add(chatList.getReceiver());
                        }
                    }
                }

                recyclerView = view.findViewById(R.id.messages_friends_recycler);
                recyclerView.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                chatAdapter = new ChatAdapter(getContext(), friendIds);
                recyclerView.setAdapter(chatAdapter);

                // Search view set up
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        chatAdapter.filter(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        chatAdapter.filter(newText);
                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // XML set up
        searchView = view.findViewById(R.id.messages_search);
        profile_picture = view.findViewById(R.id.messages_profile_image_image);

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