package com.example.immigr8.adapter;

import android.content.Context;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.immigr8.R;
import com.example.immigr8.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private List<Chat> mChat;
    private String imageURL;

    // Firebase
    FirebaseUser firebaseUser;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(Context context, List<Chat> mChat, String imageURL) {
        this.mChat = mChat;
        this.context = context;
        this.imageURL = imageURL;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_right,
                    parent,
                    false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_left,
                    parent,
                    false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.message.setText(chat.getMessage());

        if (imageURL.equals("default")) {
            holder.profile_image.setImageResource(R.drawable.ic_default_user);
        } else {
            Glide.with(context).load(imageURL).into(holder.profile_image);
        }

        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.seen_txt.setText("Seen");
            } else {
                holder.seen_txt.setText("Delivered");
            }
        } else {
            holder.seen_txt.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message, seen_txt;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messages_message);
            profile_image = itemView.findViewById(R.id.messages_profile);
            seen_txt = itemView.findViewById(R.id.chat_seen_status);
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
