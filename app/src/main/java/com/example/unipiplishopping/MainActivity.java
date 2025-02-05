package com.example.unipiplishopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Εφαρμογή του αποθηκευμένου μεγέθους γραμματοσειράς στα στοιχεία της δραστηριότητας
        applyFontSize();

        // Εύρεση του κουμπιού "Login" και ρύθμιση συμβάντος κλικ
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            // Μετάβαση στη δραστηριότητα LoginActivity όταν πατηθεί το κουμπί
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Εύρεση του κουμπιού "Signup" και ρύθμιση συμβάντος κλικ
        Button btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(v -> {
            // Μετάβαση στη δραστηριότητα SignupActivity όταν πατηθεί το κουμπί
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }
}
