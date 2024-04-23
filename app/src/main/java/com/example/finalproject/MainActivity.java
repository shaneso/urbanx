package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.finalproject.fragments.AlertsFragment;
import com.example.finalproject.fragments.HomeFragment;
import com.example.finalproject.fragments.MessagingFragment;
import com.example.finalproject.fragments.ProfileFragment;
import com.example.finalproject.model.Blocked;
import com.example.finalproject.model.CompleteUser;
import com.example.finalproject.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    // Firebase
    FirebaseUser firebaseUser;
    DatabaseReference myRef;

    BottomNavigationView bottomNav;

    public static final int REQUEST_CODE = 99;

    // Weights
    HashMap<String, Double> default_weights = new HashMap<>();

    // Complete user
    public static CompleteUser currentCompleteUser;
    Map<CompleteUser, Double> recommendedListMap = new HashMap<>();
    public static ArrayList<CompleteUser> recommendedList = new ArrayList<>();

    ArrayList<Blocked> blockedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBottomNav();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //registerNow("Mackenzie Dy", "mackenziedy@hotmail.com", "mackenzie");
        //registerNow("Emily Yu", "emily@hotmail.com", "mackenzie");
        //registerNow("Adrian Lo", "adrian@hotmail.com", "mackenzie");
        /*registerNow("Josh Young", "josh@hotmail.com", "mackenzie");
        registerNow("Mason Wu", "mason@hotmail.com", "mackenzie");
        registerNow("Shane So", "shane@hotmail.com", "mackenzie");
        registerNow("Andrew Jackson", "andrew@hotmail.com", "mackenzie");
        registerNow("Tony Stark", "tony@hotmail.com", "mackenzie");
        registerNow("Steve Rogers", "steve@hotmail.com", "mackenzie");
        registerNow("Fred Barkley", "fred@hotmail.com", "mackenzie");
        registerNow("June Iparis", "june@hotmail.com", "mackenzie");
        registerNow("Deborah Jones", "deborah@hotmail.com", "mackenzie");*/

        permissions();
    }

    /**
     * Initializes the recommended array to send to Home Fragment
     */
    private void initRecommended() {

        // Weights
        default_weights.put("hobbies", 1.0);
        default_weights.put("likes", 1.0);
        default_weights.put("values", 5.0);
        default_weights.put("age", 0.2);
        default_weights.put("location", 1.0);
        default_weights.put("language", 3.0);
        default_weights.put("gender", 0.5);
        default_weights.put("immigratedFrom", 1.0);

        recommendedList.clear();

        // Construction of Complete User
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid());

        // Builds current complete user
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                currentCompleteUser = buildCompleteUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Reassign reference to Users
        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Blocked");

        // Get blocked contacts
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        Blocked blocked = dataSnapshot.getValue(Blocked.class);
                        // builds complete  user through method
                        assert blocked != null;
                        blockedList.add(blocked);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Reassign reference to Users
        myRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");

        // Compare current user to every other user in database to find matches
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        if (!(dataSnapshot.getValue(Users.class).getId().equals(firebaseUser.getUid()))) {
                            Users user = dataSnapshot.getValue(Users.class);
                            // builds complete user through method
                            assert user != null;
                            CompleteUser completeUser = buildCompleteUser(user);
                            double score = scoring_function(default_weights, currentCompleteUser, completeUser);
                            recommendedListMap.put(completeUser, score);
                        }
                    }

                }


                Iterator<Map.Entry<CompleteUser, Double>> it = recommendedListMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<CompleteUser, Double> pair = (Map.Entry<CompleteUser, Double>)it.next();
                    if (pair.getValue() <= 0.0)
                        it.remove(); // avoids a ConcurrentModificationException
                }


                recommendedListMap = sortByValue(recommendedListMap, false);
                recommendedList.addAll(recommendedListMap.keySet());

                for (Blocked blocked : blockedList) {
                    for (CompleteUser completeUser : recommendedList) {
                        if (blocked.getSender().equals(firebaseUser.getUid()) && blocked.getReceiver().equals(completeUser.getUser().getId())) {
                            recommendedList.remove(completeUser);
                        }
                    }
                }

                // Home screen
                Fragment fragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_holder, fragment)
                        .commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Code for registering, but since we're not allowed to have a login or register page, I'll
     * just put code here so I can user FirebaseAuth to get my example users.
     */
    private void registerNow(String username, String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = firebaseUser.getUid();

                        myRef = FirebaseDatabase.getInstance()
                                .getReference("Users")
                                .child(userId);

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("id", userId);
                        hashMap.put("username", username);
                        hashMap.put("imageURL", "default");
                        hashMap.put("status", "offline");
                        hashMap.put("location", "");
                        hashMap.put("immigratedFrom", "");
                        hashMap.put("age", "");
                        hashMap.put("gender", "");
                        hashMap.put("dateJoined", LocalDate.now().toString());
                        hashMap.put("bio", "");
                        hashMap.put("timeAsImmigrant", "");

                        myRef.setValue(hashMap);

                        // Another data reference for likes
                        myRef = FirebaseDatabase.getInstance()
                                .getReference("Likes")
                                .child(userId);

                        hashMap = new HashMap<>();
                        hashMap.put("None", true);
                        myRef.setValue(hashMap);

                        // Another data reference for values
                        myRef = FirebaseDatabase.getInstance()
                                .getReference("Values")
                                .child(userId);

                        hashMap = new HashMap<>();
                        hashMap.put("None", true);
                        myRef.setValue(hashMap);

                        // Another data reference for hobbies
                        myRef = FirebaseDatabase.getInstance()
                                .getReference("Hobbies")
                                .child(userId);

                        hashMap = new HashMap<>();
                        hashMap.put("None", true);
                        myRef.setValue(hashMap);

                        // Another data reference for languages
                        myRef = FirebaseDatabase.getInstance()
                                .getReference("Languages")
                                .child(userId);

                        hashMap = new HashMap<>();
                        hashMap.put("None", true);
                        myRef.setValue(hashMap);

                    }
                });
    }

    /**
     * Updates Firebase with status of user
     * @param status online or not
     */
    private void checkStatus(String status) {
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        myRef.updateChildren(hashMap);
    }

    /**
     * If user is in the app, change user's status to online.
     */
    @Override
    protected void onResume() {
        super.onResume();
        //checkStatus("online");
    }

    /**
     * If user is not in the app, change user's status to offline
     */
    @Override
    protected void onPause() {
        super.onPause();
        //checkStatus("offline");
    }

    /**
     * Initializes the bottom navigation bar and assigns fragments to each page
     */
    private void initBottomNav() {

        // Assigns bottom navigation to its id
        bottomNav = findViewById(R.id.main_bottom_navigation);
        bottomNav.setItemIconTintList(null);



        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.nav_message:
                        selectedFragment = new MessagingFragment();
                        break;
                    case R.id.nav_alert:
                        selectedFragment = new AlertsFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_holder, selectedFragment)
                            .commit();
                }

                return true;
            }
        });
    }

    /**
     * Sorts a HashMap by value
     * @param unsortedMap the unsorted Map
     * @param order true is ASCENDING, false is DESCENDING
     * @return sorted map
     */
    private static Map<CompleteUser, Double> sortByValue(Map<CompleteUser, Double> unsortedMap, final boolean order)
    {
        List<Map.Entry<CompleteUser, Double>> list = new LinkedList<>(unsortedMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().getUser().getId().compareTo(o2.getKey().getUser().getId())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().getUser().getId().compareTo(o1.getKey().getUser().getId())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    /**
     * Scores users based on common traits
     * @param weights weights determining which is most important
     * @param user1 current user
     * @param user2 all other users
     * @return double as the score (higher is better)
     */
    private double scoring_function(HashMap<String, Double> weights, CompleteUser user1, CompleteUser user2) {
        double score = 0.0;
        // compare hobbies
        // +add for each hobby * weight
        if (user1.getHobbies().contains("None") && user2.getHobbies().contains("None")) {
            for (String hobby1 : user1.getHobbies()) {
                for (String hobby2 : user2.getHobbies()) {
                    hobby1 = hobby1.toLowerCase();
                    hobby2 = hobby2.toLowerCase();

                    if (hobby1.equals(hobby2)) {
                        Timber.e(" User 1" + user1.getUser().getUsername() + "User 2" + user2.getUser().getUsername());
                        score += weights.get("hobbies");
                    }
                }
            }
        }

        // compare values
        // +add for each value * weight
        if (user1.getValues().contains("None") && user2.getValues().contains("None")) {
            for (String value1 : user1.getValues()) {
                for (String value2 : user2.getValues()) {
                    value1 = value1.toLowerCase().trim();
                    value2 = value2.toLowerCase().trim();

                    if (value1.equals(value2)) {
                        score += weights.get("values");
                    }
                }
            }
        }

        // compare likes
        // +add for each likes * weight
        if (user1.getLikes().contains("None") && user2.getLikes().contains("None")) {
            for (String like1 : user1.getLikes()) {
                for (String like2 : user2.getLikes()) {
                    like1 = like1.toLowerCase().trim();
                    like2 = like2.toLowerCase().trim();

                    if (like1.equals(like2)) {
                        score += weights.get("likes");
                    }
                }
            }
        }

        // compare age
        // -subtract age difference * weight
        if (!(user1.getUser().getAge().equals("")) && !(user2.getUser().getAge().equals(""))) {
            score -= Math.abs(Double.parseDouble(user1.getUser().getAge()) - Double.parseDouble(user2.getUser().getAge())) * weights.get("age");
        }

        // compare languages
        // =add for each language * weight
        if (user1.getLanguages().contains("None") && user2.getLanguages().contains("None")) {
            for (String language1 : user1.getLikes()) {
                for (String language2 : user2.getLikes()) {
                    language1 = language1.toLowerCase().trim();
                    language2 = language2.toLowerCase().trim();

                    if (language1.equals(language2)) {
                        score += weights.get("languages");
                    }
                }
            }
        }

        // Compare gender
        if (!(user1.getUser().getGender().equals("")) && !(user2.getUser().getGender().equals(""))) {
            if (user1.getUser().getGender().toLowerCase().equals(user2.getUser().getGender().toLowerCase()))
                score += weights.get("gender");
        }

        // Compare immigrated from
        if (!(user1.getUser().getImmigratedFrom().equals("")) && !(user2.getUser().getImmigratedFrom().equals(""))) {
            if (user1.getUser().getImmigratedFrom().toLowerCase().equals(user2.getUser().getImmigratedFrom().toLowerCase()))
                score += weights.get("immigratedFrom");
        }

        return score;
    }

    /**
     * Build complete user from user and lists stored on firebase
     * @param user already determined user
     * @return CompleteUser object
     */
    private CompleteUser buildCompleteUser(Users user) {

        // Likes
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(user.getId());
        ArrayList<String> likes = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    likes.add((String)dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Hobbies
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Hobbies").child(user.getId());
        ArrayList<String> hobbies = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    hobbies.add((String)dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Values
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Values").child(user.getId());
        ArrayList<String> values = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    values.add((String)dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Languages
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Languages").child(user.getId());
        ArrayList<String> languages = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    languages.add((String) dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return new CompleteUser(user, hobbies, languages, values, likes);
    }

    /*************************
     * Both of these methods (permissions and onRequestPermissionResult) makes sure the app has permission from the user to access and read
     * the external storage.
     *
     * These also run the getAllAudio method and initializes the RecyclerView.
     */

    private void permissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        else {
            // Set up recommended list
            initRecommended();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Set up recommended list
                initRecommended();
            } else
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }
}