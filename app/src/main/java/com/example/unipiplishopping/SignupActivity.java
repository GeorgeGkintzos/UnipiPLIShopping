package com.example.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class SignupActivity extends BaseActivity {

    private EditText edtUsername, edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Εφαρμογή του font size στα στοιχεία της δραστηριότητας
        applyFontSize();

        // Σύνδεση των στοιχείων του UI με τα αντίστοιχα views
        edtUsername = findViewById(R.id.edtUsername);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button back = findViewById(R.id.back);


        // Διαχείριση πατήματος του κουμπιού "Register"
        btnRegister.setOnClickListener(v -> {
            registerUser();
        });

        // Διαχείριση πατήματος του ImageView "backImage"
        back.setOnClickListener(this::backToHome);
    }

    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("users");

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Έλεγχος αν ο κωδικός είναι έγκυρος
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), getString(R.string.passwords_must_match), Toast.LENGTH_SHORT).show();
            return;
        }

        // Έλεγχος αν το email είναι έγκυρο
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        // Έλεγχος αν το username υπάρχει ήδη
        checkUsernameAvailability(username, isUnavailable -> {
            if (isUnavailable) {
                Toast.makeText(this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, SignupActivity.class);
                startActivity(intent);
            } else {
                // Επιτυχής εγγραφή
                addUser(username, firstName, lastName, email, password);
            }
        });
    }

    private void addUser(String username, String firstName, String lastName, String email, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        // Δημιουργία μοναδικού userId
        String userId = usersRef.push().getKey();

        if (userId != null) {
            // Δημιουργία HashMap με τα στοιχεία του χρήστη
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", username);
            userData.put("firstName", firstName);
            userData.put("lastName", lastName);
            userData.put("email", email);
            userData.put("password", password);


            // Εισαγωγή στη Firebase
            usersRef.child(userId).setValue(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, getString(R.string.user_added_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, getString(R.string.error_adding_user), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void backToHome(View view) {
        Intent intent=new Intent(SignupActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void checkUsernameAvailability(String username, UsernameCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");

        databaseRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isUnavailable = dataSnapshot.exists();
                        callback.onResult(isUnavailable);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SignupActivity.this, getString(R.string.error_checking_username), Toast.LENGTH_SHORT).show();
                        callback.onResult(true); // Assume unavailable on error
                    }
                });
    }
    public interface UsernameCallback {
        void onResult(boolean isUnavailable);
    }

}