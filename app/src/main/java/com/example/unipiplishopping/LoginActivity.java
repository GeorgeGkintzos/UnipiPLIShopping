package com.example.unipiplishopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {
    private EditText editTextUsername, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        applyFontSize();

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button back = findViewById(R.id.back);

        buttonLogin.setOnClickListener(v -> checkLogin());

        back.setOnClickListener(v -> {
            backToHome();
            finish(); // Κλείνει το τρέχον activity
        });
    }

    private void backToHome() {
        Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
        startActivity(intent);
    }

    private void saveUserToPreferences() {
        String username = editTextUsername.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    private void checkLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isValid = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbUsername = userSnapshot.child("username").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    if (dbUsername != null && dbPassword != null &&
                            dbUsername.equals(username) &&
                            dbPassword.equals(password)) {
                        isValid = true;
                        break;
                    }
                }

                if (isValid) {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                    saveUserToPreferences();
                    Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
