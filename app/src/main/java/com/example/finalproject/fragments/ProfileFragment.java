package com.example.finalproject.fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalproject.EditProfile;
import com.example.finalproject.MainActivity;
import com.example.finalproject.R;
import com.example.finalproject.model.CompleteUser;
import com.example.finalproject.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView username;
    ImageView profilePicture, edit_profile, kebab;

    // Information
    TextView bio, age, location, immigrated, gender, hobbies, values, likes, languages, timeAsImmigrated;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    CompleteUser completeUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Complete User set up
        completeUser = MainActivity.currentCompleteUser;

        AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        // Set xml up
        profilePicture = view.findViewById(R.id.profile_profile_image);
        username = view.findViewById(R.id.profile_username);
        kebab = view.findViewById(R.id.profile_menu);

        bio = view.findViewById(R.id.profile_bio);
        age = view.findViewById(R.id.profile_age);
        location = view.findViewById(R.id.profile_location);
        gender = view.findViewById(R.id.profile_gender);
        hobbies = view.findViewById(R.id.profile_hobbies);
        values = view.findViewById(R.id.profile_values);
        immigrated = view.findViewById(R.id.profile_immigrated);
        likes = view.findViewById(R.id.profile_likes);
        timeAsImmigrated = view.findViewById(R.id.profile_time_as_immigrant);
        languages = view.findViewById(R.id.profile_languages);

        edit_profile = view.findViewById(R.id.profile_edit_profile);

        // Set up info from complete user
        username.setText(completeUser.getUser().getUsername());

        if (completeUser.getUser().getImageURL().equals("default")) {
            profilePicture.setImageResource(R.drawable.ic_default_user);
        } else {
            Glide.with(getContext()).load(completeUser.getUser().getImageURL()).into(profilePicture);
        }

        String timeAs = completeUser.getUser().getTimeAsImmigrant() + " months";

        bio.setText(completeUser.getUser().getBio());
        age.setText(completeUser.getUser().getAge());
        location.setText(completeUser.getUser().getLocation());
        gender.setText(completeUser.getUser().getGender());
        immigrated.setText(completeUser.getUser().getImmigratedFrom());
        hobbies.setText(arrayListToComma(completeUser.getHobbies()));
        languages.setText(arrayListToComma(completeUser.getLanguages()));
        values.setText(arrayListToComma(completeUser.getValues()));
        timeAsImmigrated.setText(timeAs);
        likes.setText(arrayListToComma(completeUser.getLikes()));

        edit_profile.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            Intent i = new Intent(getContext(), EditProfile.class);
            startActivity(i);
        });

        // Logout
        kebab.setOnClickListener(this::showPopup);

        return view;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.log_out_menu, popup.getMenu());
        popup.show();
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
        //remove last comma from String if you want
        if( strList.length() > 0 )
            strList = strList.substring(0, strList.length() - 2);

        return strList;
    }

    /**
     * Log out functionality (won't work thought cause we don't have login page)
     * @param item menu item
     * @return if item is selected
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            //FirebaseAuth.getInstance().signOut();
            return true;
        }
        return false;
    }

}