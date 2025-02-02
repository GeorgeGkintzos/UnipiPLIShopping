package com.example.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText edtUsername, edtFirstName, edtLastName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Σύνδεση των στοιχείων του UI με τα αντίστοιχα views
        edtUsername = findViewById(R.id.edtUsername);
        edtFirstName = findViewById(R.id.edtFirstName);
        edtLastName = findViewById(R.id.edtLastName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        backImage=findViewById(R.id.backImage);


        // Διαχείριση πατήματος του κουμπιού "Register"
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        // Διαχείριση πατήματος του ImageView "backImage"
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome(v);
            }
        });
    }

    private void registerUser() {
        String username = edtUsername.getText().toString().trim();
        String firstName = edtFirstName.getText().toString().trim();
        String lastName = edtLastName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty() ) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Έλεγχος αν ο κωδικός να είναι έγκυρος
        if(! password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(), "Passwords must be identical!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Έλεγχος αν το email είναι έγκυρο
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Έλεγχος αν το username υπάρχει ήδη
        if (usernameUnavailable(username)) {
            Toast.makeText(this, "Username not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Επιτυχής εγγραφή (προς το παρόν απλά εμφανίζουμε μήνυμα)
        addUser(username,firstName,lastName,email,password);


    }


    private boolean usernameUnavailable(String username){
        return false;
    }
// Προσ
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
                            Toast.makeText(this, "Ο χρήστης προστέθηκε επιτυχώς!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Σφάλμα κατα την εισαγωγή!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void backToHome(View view) {
        Intent intent=new Intent(SignupActivity.this,MainActivity.class);
        startActivity(intent);
    }
}