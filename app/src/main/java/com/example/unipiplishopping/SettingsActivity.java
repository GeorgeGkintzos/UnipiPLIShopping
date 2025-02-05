package com.example.unipiplishopping;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {
    private TextView sampleText;
    private SharedPreferences sharedPreferences;
    private static final String FONT_SIZE_KEY = "font_size";
    private static final String LANGUAGE_KEY = "language";
    private Spinner languageSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Εφαρμογή του font size στα στοιχεία της δραστηριότητας
        applyFontSize();

        SeekBar fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar);
        sampleText = findViewById(R.id.sampleText);
        Button applyButton = findViewById(R.id.applyButton);
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Ανάκτηση αποθηκευμένου μεγέθους γραμματοσειράς
        float savedFontSize = sharedPreferences.getFloat(FONT_SIZE_KEY, 16);
        applyFontSize(savedFontSize);
        fontSizeSeekBar.setProgress((int) (savedFontSize - 12));

        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float newSize = 12 + progress; // Το μέγεθος θα κυμαίνεται από 12 έως 36
                applyFontSize(newSize);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Προσωρινή αποθήκευση στο SharedPreferences (αλλά δεν εφαρμόζεται ακόμη)
                float finalSize = 12 + seekBar.getProgress();
                sharedPreferences.edit().putFloat(FONT_SIZE_KEY, finalSize).apply();
            }
        });

        // Γλώσσες διαθέσιμες στο Spinner
        ArrayAdapter<String> adapter = getStringArrayAdapter();

        languageSpinner = findViewById(R.id.languageSpinner);
        languageSpinner.setAdapter(adapter);

        // Φόρτωση αποθηκευμένης γλώσσας
        String savedLanguage = sharedPreferences.getString(LANGUAGE_KEY, "en");
        switch (savedLanguage) {
            case "el":
                languageSpinner.setSelection(1);
                break;
            case "es":
                languageSpinner.setSelection(2);
                break;
            default:
                languageSpinner.setSelection(0);
                break;
        }

        // Button Click Listener
        applyButton.setOnClickListener(v -> {
            // Παίρνουμε το μέγεθος γραμματοσειράς από τον SeekBar
            float newFontSize = 12 + fontSizeSeekBar.getProgress();

            // Παίρνουμε τη γλώσσα από το Spinner
            String selectedLanguage = "en";
            switch (languageSpinner.getSelectedItemPosition()) {
                case 1:
                    selectedLanguage = "el";
                    break;
                case 2:
                    selectedLanguage = "es";
                    break;
            }

            // Αποθήκευση νέου μεγέθους γραμματοσειράς στις SharedPreferences
            sharedPreferences.edit().putFloat(FONT_SIZE_KEY, newFontSize).apply();

            // Αποθήκευση της επιλεγμένης γλώσσας στις SharedPreferences
            sharedPreferences.edit().putString(LANGUAGE_KEY, selectedLanguage).apply();

            // Εφαρμογή αλλαγής γραμματοσειράς
            applyFontSize(newFontSize);

            // Εφαρμογή αλλαγής γλώσσας
            applyLanguage(selectedLanguage);

            // Εμφάνιση διαλόγου επιβεβαίωσης για επανεκκίνηση της εφαρμογής
            showRestartDialog();
        });
    }

    @NonNull
    private ArrayAdapter<String> getStringArrayAdapter() {
        String[] languages = {"English", "Ελληνικά", "Espalier"};
        // Ρυθμίζει το χρώμα του κειμένου σε λευκό για το drop-down
        return new ArrayAdapter<>(SettingsActivity.this, android.R.layout.simple_spinner_item, languages) {
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextColor(Color.WHITE); // Ρυθμίζει το χρώμα του κειμένου σε λευκό για το drop-down
                return view;
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view;
                text.setTextColor(Color.WHITE); // Ρυθμίζει το χρώμα του κειμένου σε λευκό για το επιλεγμένο στοιχείο
                text.setTextColor(Color.WHITE); // Ρυθμίζει το χρώμα του κειμένου σε λευκό για το επιλεγμένο στοιχείο
                return view;
            }
        };
    }

    private void applyFontSize(float size) {
        sampleText.setTextSize(size);
    }

    public void applyLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private void showRestartDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.restart_required))
                .setMessage(getString(R.string.restart_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> restartApp())
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void restartApp() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity(); // Κλείνει όλα τα activity
    }
}
