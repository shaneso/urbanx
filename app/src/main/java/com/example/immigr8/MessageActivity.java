package com.example.immigr8;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.immigr8.adapter.MessageAdapter;
import com.example.immigr8.model.Chat;
import com.example.immigr8.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    TextView username;
    ImageView imageView, back, kebab, video;

    RecyclerView recyclerView;
    EditText msg_edit, code;
    Button join;
    ImageButton sendBtn;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String userId;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    ValueEventListener seenListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        sendBtn      = findViewById(R.id.messages_send);
        msg_edit     = findViewById(R.id.messages_textsend);
        recyclerView = findViewById(R.id.messages_recyclerview);
        kebab        = findViewById(R.id.messages_toolbar_three);

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        // RecyclerView
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageView    = findViewById(R.id.toolbar_image);
        username     = findViewById(R.id.toolbar_title);
        back         = findViewById(R.id.toolbar_back);
        video        = findViewById(R.id.messages_toolbar_video);

        intent = getIntent();
        userId = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Video conferencing
//        URL serverURL;

//        try {
//            serverURL = new URL("https://meet.jit.si");
//            JitsiMeetConferenceOptions defaultOptions =
//                    new JitsiMeetConferenceOptions.Builder()
//                            .setServerURL(serverURL)
//                            .setWelcomePageEnabled(false)
//                            .build();
//            JitsiMeet.setDefaultConferenceOptions(defaultOptions);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }

        // Dialog setup
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.video_dialog);

        join = dialog.findViewById(R.id.dialog_joinBtn);
        code = dialog.findViewById(R.id.dialog_codeBox);

//        // Once join button is pressed
//        join.setOnClickListener(v -> {
//            v.startAnimation(buttonClick);
//            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
//                    .setRoom(code.getText().toString())
//                    .setWelcomePageEnabled(false)
//                    .build();
//
//            JitsiMeetActivity.launch(MessageActivity.this, options);
//        });

        // Video icon listener
        video.setOnClickListener(v -> {
            // Transparent window
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        // Sets username and image and displays all messages
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.ic_default_user);
                } else {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);
                }

                readMessages(firebaseUser.getUid(), userId, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Send button
        sendBtn.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            String msg = msg_edit.getText().toString();
            if (!msg.equals("")) {
                sendMessage(firebaseUser.getUid(), userId, msg);
            } else {

            }

            msg_edit.setText("");
        });

        // Back button
        back.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

        // Kebab
        kebab.setOnClickListener(this::showPopup);

        seenMessage(userId);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.block_menu, popup.getMenu());
        popup.show();
    }

    /**
     * Block functionality
     * @param menu menu
     * @return true when created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.block_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_block) {
            blockContact(firebaseUser.getUid(), userId);
            return true;
        }
        return false;
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
     * Checks if user has seen message, if so, change value in firebase to seen.
     * @param userId refers to the sender of the message
     */
    private void seenMessage(String userId) {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * @param sender - userId of person sending message
     * @param receiver - userId of person receiving message
     * @param message - main body of message.
     *
     * This method puts message data into the Firebase Database.
     */
    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hashMap.put("date", LocalDate.now().toString());
        }

        reference.child("Chats").push().setValue(hashMap);
    }

    /**
     * @param id - userId of person sending message
     * @param userId - userId of person receiving message
     * @param imageURL - path of image of person sending the message
     *
     * This method searches the Firebase Database and finds the messages that correspond to
     * the dialogue.
     */
    private void readMessages(String id, String userId, String imageURL) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(id) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(id)) {
                        mChat.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageURL);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Updates Firebase with status of user
     * @param status online or not
     */
    private void checkStatus(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    /**
     * If user is in the app, change user's status to online.
     */
    @Override
    protected void onResume() {
        super.onResume();
        checkStatus("online");
    }

    /**
     * If user is not in the app, change user's status to offline
     */
    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        checkStatus("offline");
    }
}