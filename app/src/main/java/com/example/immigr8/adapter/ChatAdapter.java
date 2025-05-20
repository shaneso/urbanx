package com.example.immigr8.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.immigr8.MainActivity;
import com.example.immigr8.MessageActivity;
import com.example.immigr8.R;
import com.example.immigr8.model.Chat;
import com.example.immigr8.model.ChatList;
import com.example.immigr8.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final Context context;
    private List<String> mChat;
    private List<String> mChatCopy;
    private ArrayList<Users> usersList;
    private ArrayList<Users> usersListCopy;
    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<String> mChat) {
        this.mChat = mChat;
        this.context = context;
        mChatCopy = mChat;
        usersList = new ArrayList<>();
        usersListCopy = new ArrayList<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_users,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mChat.get(position));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList.clear();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                holder.username.setText(user.getUsername());

                usersList.add(user);

                if (user.getImageURL().equals("default")) {
                    holder.profile_image.setImageResource(R.drawable.ic_default_user);
                } else {
                    Glide.with(context)
                            .load(user.getImageURL())
                            .into(holder.profile_image);
                }

                if (user.getStatus().equals("online")) {
                    holder.online_offline.setImageResource(R.drawable.ic_green_online);
                } else {
                    holder.online_offline.setImageResource(R.drawable.ic_gray_offline);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // remap reference
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!mChat.isEmpty()) {
                    String message = "";
                    String date = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(mChat.get(position)) && chat.getSender().equals(firebaseUser.getUid()) ||
                                chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(mChat.get(position))) {
                            message = chat.getMessage();
                            date = chat.getDate();
                        }
                    }
                    holder.message.setText(message);
                    holder.date.setText(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);


        holder.itemView.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            Intent i = new Intent(context, MessageActivity.class);
            i.putExtra("userid", mChat.get(position));
            context.startActivity(i);
        });



    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message, username, date;
        public ImageView profile_image, online_offline;

        public ViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.chat_image);
            message = itemView.findViewById(R.id.chat_glimpse);
            username = itemView.findViewById(R.id.chat_username);
            online_offline = itemView.findViewById(R.id.chat_online_offline);
            date = itemView.findViewById(R.id.chat_date);
        }

    }

    /**
     * Filters through the text
     * @param text key to filter with
     */
    public void filter(String text) {
        //usersListCopy.addAll(usersList);
        //usersList.clear();
        mChat.clear();
        if(text.isEmpty()){
            mChat.addAll(mChatCopy);
        } else{
            text = text.toLowerCase();
            for(Users user: usersList){
                if(user.getUsername().toLowerCase().contains(text)){
                    mChat.add(user.getId());
                }
            }
        }
        notifyDataSetChanged();
    }
}
