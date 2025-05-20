package com.example.immigr8;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.immigr8.model.CompleteUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    EditText bio, age, location, immigrated, gender, hobbies, values, likes, languages, username, timeAsImmigrant;
    ImageView picture, back;
    Button save;

    // Firebase
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    // Complete User
    CompleteUser completeUser;

    // Profile image
    private static final int IMAGE_REQUEST = 22;
    private Uri imageUri;
    private StorageTask uploadTask;

    // Animation
    AlphaAnimation buttonClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        completeUser = MainActivity.currentCompleteUser;

        buttonClick = new AlphaAnimation(1F, 0.8F);

        // XML set up
        bio = findViewById(R.id.profile_edit_bio);
        age = findViewById(R.id.profile_edit_age);
        location = findViewById(R.id.profile_edit_location);
        gender = findViewById(R.id.profile_edit_gender);
        hobbies = findViewById(R.id.profile_edit_hobbies);
        values = findViewById(R.id.profile_edit_values);
        immigrated = findViewById(R.id.profile_edit_immigrated);
        likes = findViewById(R.id.profile_edit_likes);
        languages = findViewById(R.id.profile_edit_languages);
        username = findViewById(R.id.profile_edit_username);
        picture = findViewById(R.id.profile_edit_photo);
        back = findViewById(R.id.profile_edit_back);
        timeAsImmigrant = findViewById(R.id.profile_edit_time_as_immigrated);
        save = findViewById(R.id.profile_edit_check);

        bio.setHint(completeUser.getUser().getBio());
        age.setHint(completeUser.getUser().getAge());
        location.setHint(completeUser.getUser().getLocation());
        gender.setHint(completeUser.getUser().getGender());
        immigrated.setHint(completeUser.getUser().getImmigratedFrom());
        hobbies.setHint(arrayListToComma(completeUser.getHobbies()));
        languages.setHint(arrayListToComma(completeUser.getLanguages()));
        values.setHint(arrayListToComma(completeUser.getValues()));
        timeAsImmigrant.setHint(completeUser.getUser().getTimeAsImmigrant());
        likes.setHint(arrayListToComma(completeUser.getLikes()));

        // Profile image reference in storage
        storageReference = FirebaseStorage.getInstance().getReference("Users");

        // When clicked on profile image, search for image in gallery
        picture.setOnClickListener(v -> {
            selectImage();
        });

        // Back to MainActivity
        back.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

        // Saves and goes back to MainActivity
        save.setOnClickListener(v -> {
            saveData();
            v.startAnimation(buttonClick);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        });

    }

    /**
     * Saves data into firebase
     */
    private void saveData() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());
        String text = bio.getText().toString();
        if (!text.equals("")) {
            reference.child("bio").setValue(text);
        }

        text = age.getText().toString();
        if (!text.equals("")) {
            reference.child("age").setValue(text);
        }

        text = gender.getText().toString();
        if (!text.equals("")) {
            reference.child("gender").setValue(text);
        }

        text = location.getText().toString();
        if (!text.equals("")) {
            reference.child("location").setValue(text);
        }

        text = immigrated.getText().toString();
        if (!text.equals("")) {
            reference.child("immigratedFrom").setValue(text);
        }

        text = username.getText().toString();
        if (!text.equals("")) {
            reference.child("username").setValue(text);
        }

        text = timeAsImmigrant.getText().toString();
        if (!text.equals("")) {
            reference.child("timeAsImmigrant").setValue(text);
        }

        text = hobbies.getText().toString();
        List<String> items = Arrays.asList(text.split("\\s*,\\s*"));
        HashMap<String, Boolean> hashMap = new HashMap<>();
        if (!text.equals("")) {
            reference = FirebaseDatabase.getInstance().getReference().child("Hobbies").child(firebaseUser.getUid());
            for (String item : items) {
                hashMap.put(item, true);
            }
            reference.setValue(hashMap);
        }

        text = values.getText().toString();
        if (!text.equals("")) {
            items = Arrays.asList(text.split("\\s*,\\s*"));
            reference = FirebaseDatabase.getInstance().getReference().child("Values").child(firebaseUser.getUid());
            hashMap = new HashMap<>();
            for (String item : items) {
                hashMap.put(item, true);
            }
            reference.setValue(hashMap);
        }

        text = likes.getText().toString();
        if (!text.equals("")) {
            items = Arrays.asList(text.split("\\s*,\\s*"));
            reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(firebaseUser.getUid());
            hashMap = new HashMap<>();
            for (String item : items) {
                hashMap.put(item, true);
            }
            reference.setValue(hashMap);
        }

        text = languages.getText().toString();
            if (!text.equals("")) {
                items = Arrays.asList(text.split("\\s*,\\s*"));
                reference = FirebaseDatabase.getInstance().getReference().child("Languages").child(firebaseUser.getUid());
                hashMap = new HashMap<>();
                for (String item : items) {
                    hashMap.put(item, true);
                }
                reference.setValue(hashMap);
            }
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
     * Open the gallery of users and searches the files for images
     */
    private void selectImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        // i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(i, IMAGE_REQUEST);
    }

    /**
     * Takes the image and puts it into Firebase Storage
     */
    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child("images/"
                    + UUID.randomUUID().toString());


            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {

                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                return fileReference.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", mUri);
                        reference.updateChildren(hashMap);

                        Glide.with(getApplicationContext()).load(imageUri).into(picture);
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            /*
            fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully
                Toast.makeText(EditProfile.this,
                                "Image Uploaded!!",
                                Toast.LENGTH_SHORT).show();

                reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("imageURL", imageUri.toString());
                reference.updateChildren(hashMap);

                Glide.with(this).load(imageUri).into(picture);

            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                Toast.makeText(EditProfile.this,
                                "Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
            });*/
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Tell the app that this is the selected image and upload it
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();
            //getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Upload in progress...", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }
}