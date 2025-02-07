package com.example.unipiplishopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class EditProfile extends BaseActivity {
    private EditText editFirstName, editLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        // Ανάκτηση των πεδίων και του κουμπιού από το layout
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);

        // Ανάκτηση username μέσω των ηδη υπάρχοντων SharedPreferences UserSession
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Nobody");
        getUserDetailsByUsername(username, userDetails -> {
            if (userDetails != null) {
                editFirstName.setText(userDetails.get("firstName"));
                editLastName.setText(userDetails.get("lastName"));
            }
        });


            // Εμφάνιση των δεδομένων του χρήστη
        //editFirstName.setText(map.get("firstName"));
        //editLastName.setText(map.get("lastName"));

        // Εφαρμογή μεγέθους γραμματοσειράς
        applyFontSize();

    }
        private void getUserDetailsByUsername(String username, Consumer<Map<String, String>> callback) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
            databaseRef.orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, String> map = new HashMap<>();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    String firstName = userSnapshot.child("firstName").getValue(String.class);
                                    String lastName = userSnapshot.child("lastName").getValue(String.class);
                                    map.put("firstName", firstName);
                                    map.put("lastName", lastName);
                                }
                                // Επιστροφή των δεδομένων μέσω callback
                                callback.accept(map);
                            } else {
                                Toast.makeText(EditProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("FirebaseError", "Error while fetching user data", databaseError.toException());
                        }
                    });
        }


}
