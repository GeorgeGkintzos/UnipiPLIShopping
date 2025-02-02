package com.example.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextPassword;
    private ImageView back_image;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        back_image = findViewById(R.id.Back_image);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkLogin();
            }
        });



        back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome(v);
                finish(); // Κλείνει το τρέχον activity
            }
        });
    }

    private void backToHome(View view) {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void checkLogin() {
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, ProductListActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
