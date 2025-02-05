package com.example.unipiplishopping;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
    private static final String FONT_SIZE_KEY = "font_size"; // Κλειδί για αποθήκευση του μεγέθους γραμματοσειράς

    /**
     * Ορίζει τη γλώσσα της εφαρμογής.
     * Διαβάζει τη γλώσσα από τα SharedPreferences και την εφαρμόζει στο σύστημα.
     */
    public void applyLanguage(String language) {
        Locale locale = new Locale(language); // Δημιουργία νέας γλώσσας
        Locale.setDefault(locale); // Ορισμός της ως προεπιλεγμένη
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale); // Εφαρμογή νέας γλώσσας

        // Δημιουργία νέου Configuration Context
        getBaseContext().createConfigurationContext(config);
        res.updateConfiguration(config, res.getDisplayMetrics()); // Ενημέρωση διαμόρφωσης της εφαρμογής
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Ανάκτηση της αποθηκευμένης γλώσσας, αν δεν υπάρχει, χρησιμοποιεί την αγγλική (en)
        String language = prefs.getString("language", "en");
        applyLanguage(language); // Εφαρμογή της γλώσσας

        applyFontSize(); // Εφαρμογή του αποθηκευμένου μεγέθους γραμματοσειράς
    }

    /**
     * Διαβάζει το αποθηκευμένο μέγεθος γραμματοσειράς και το εφαρμόζει σε όλα τα κείμενα της εφαρμογής.
     */
    void applyFontSize() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        float fontSize = sharedPreferences.getFloat(FONT_SIZE_KEY, 16); // Προεπιλεγμένο μέγεθος 16

        // Εντοπισμός της κύριας διάταξης της δραστηριότητας
        ViewGroup rootView = findViewById(android.R.id.content);

        // Αναδρομική ενημέρωση όλων των TextView με το νέο μέγεθος
        updateFontSizeRecursively(rootView, fontSize);
    }

    /**
     * Αναδρομικά αλλάζει το μέγεθος γραμματοσειράς σε όλα τα TextView ενός ViewGroup.
     * @param viewGroup Το γονικό ViewGroup που περιέχει τα στοιχεία
     * @param fontSize Το μέγεθος γραμματοσειράς που θα εφαρμοστεί
     */
    private void updateFontSizeRecursively(ViewGroup viewGroup, float fontSize) {
        if (viewGroup == null) return; // Αν είναι null, δεν κάνουμε τίποτα

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);

            if (child instanceof TextView) {
                ((TextView) child).setTextSize(fontSize); // Ρύθμιση του νέου μεγέθους γραμματοσειράς
            } else if (child instanceof ViewGroup) {
                // Αν το στοιχείο είναι άλλο ViewGroup, το εξετάζουμε αναδρομικά
                updateFontSizeRecursively((ViewGroup) child, fontSize);
            }
        }
    }
}
