package com.example.immigr8.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.immigr8.R;
import com.example.immigr8.model.Alert;
import com.example.immigr8.model.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.ViewHolder> {

    private Context context;
    private List<Alert> mAlert;

    public AlertAdapter(Context context, ArrayList<Alert> mAlert) {
        this.mAlert = mAlert;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_users,
                parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mAlert.get(position).getSender());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                holder.username.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    holder.profile_image.setImageResource(R.drawable.ic_default_user);
                } else {
                    Glide.with(context)
                            .load(user.getImageURL())
                            .into(holder.profile_image);
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Alerts").child(mAlert.get(position).getReceiver());
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Friends");

                AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

                holder.yes.setOnClickListener(v -> {
                    v.startAnimation(buttonClick);
                    reference.removeValue();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("receiver", mAlert.get(position).getReceiver());
                    hashMap.put("sender", mAlert.get(position).getSender());
                    friendsRef.push().setValue(hashMap);
                    mAlert.remove(position);
                    notifyDataSetChanged();
                });

                holder.no.setOnClickListener(v -> {
                    v.startAnimation(buttonClick);
                    reference.removeValue();
                    mAlert.remove(position);
                    notifyDataSetChanged();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return mAlert.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image, yes, no;

        public ViewHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.alert_image);
            username = itemView.findViewById(R.id.alert_username);
            yes = itemView.findViewById(R.id.alert_yes);
            no = itemView.findViewById(R.id.alert_no);
        }

    }
}
