package com.example.unipiplishopping;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfile extends BaseActivity {
    private EditText editFirstName, editLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ανάκτηση των πεδίων και του κουμπιού από το layout
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        Button buttonSave = findViewById(R.id.buttonSave);

        // Φόρτωση των δεδομένων του χρήστη
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String firstName = sharedPreferences.getString("first_name", "");
        String lastName = sharedPreferences.getString("last_name", "");

        // Εμφάνιση των δεδομένων του χρήστη
        editFirstName.setText(firstName);
        editLastName.setText(lastName);

        // Εφαρμογή μεγέθους γραμματοσειράς
        applyFontSize();

        // Αποθήκευση αλλαγών
        buttonSave.setOnClickListener(v -> {
            String updatedFirstName = editFirstName.getText().toString();
            String updatedLastName = editLastName.getText().toString();

            if (!updatedFirstName.isEmpty() && !updatedLastName.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("first_name", updatedFirstName);
                editor.putString("last_name", updatedLastName);
                editor.apply();

                Toast.makeText(this, "Οι αλλαγές αποθηκεύτηκαν!", Toast.LENGTH_SHORT).show();
                finish(); // Επιστροφή στην προηγούμενη οθόνη
            } else {
                Toast.makeText(this, "Παρακαλώ συμπληρώστε και τα δύο πεδία!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
