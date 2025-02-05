package com.example.unipiplishopping;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONException;
import org.json.JSONObject;

public class ProductListActivity extends BaseActivity {
    private List<Product> productList;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference storesRef;
    private final List<Store> storesList = new ArrayList<>();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Εφαρμογή του font size στα στοιχεία της δραστηριότητας
        applyFontSize();

        // Αρχικοποίηση των FloatingActionButtons
        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);
        FloatingActionButton fabOption1 = findViewById(R.id.fabOption1);
        FloatingActionButton fabOption2 = findViewById(R.id.fabOption2);

        // Λειτουργία για το κύριο κουμπί Settings
        fabSettings.setOnClickListener(v -> {
            if (fabOption1.getVisibility() == View.GONE) {
                // Εμφάνιση των επιπλέον κουμπιών
                fabOption1.setVisibility(View.VISIBLE);
                fabOption2.setVisibility(View.VISIBLE);
            } else {
                // Απόκρυψη των επιπλέον κουμπιών
                fabOption1.setVisibility(View.GONE);
                fabOption2.setVisibility(View.GONE);
            }
        });

        // Λειτουργία για το πρώτο επιπλέον κουμπί
        fabOption1.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, EditProfile.class);
            startActivity(intent);
    });

        // Λειτουργία για το δεύτερο επιπλέον κουμπί
        fabOption2.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        // Έλεγχος και αίτημα δικαιωμάτων
        checkAndRequestPermissions();

        // Παρέχεται από Google
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // **Αρχικοποίηση της storesRef**
        storesRef = FirebaseDatabase.getInstance().getReference("stores");

        getUserLocation();

        loadStoresFromFirebase();

        checkNearbyStores();

        loadProducts();

        if (productList != null && !productList.isEmpty()) {
            //Accessed only within the class itself
            ProductAdapter productAdapter = new ProductAdapter(this, productList);
            recyclerView.setAdapter(productAdapter); //RecyclerView, you need to connect it to an Adapter and a LayoutManager
        }

        com.google.android.material.floatingactionbutton.FloatingActionButton buttonGoToCart = findViewById(R.id.buttonGoToCart);
        buttonGoToCart.setOnClickListener(v -> startActivity(new Intent(ProductListActivity.this, CartActivity.class)));
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        productList.add(new Product("1", getString(R.string.lady_gaga_mayhem), getString(R.string.dark_pop), getString(R.string.date_7_march_2025), 43.00, 1, getString(R.string.velona_records)));
        productList.add(new Product("2", getString(R.string.the_beatles_abbey_road), getString(R.string.rock), getString(R.string.date_26_september_1969), 35.00, 1, getString(R.string.diskorama)));
        productList.add(new Product("3", getString(R.string.the_beatles_let_it_be), getString(R.string.rock), getString(R.string.date_8_may_1970), 35.00, 1, getString(R.string.diskorama)));
        productList.add(new Product("4", getString(R.string.panic_death_of_a_bachelor), getString(R.string.alternative_indie), getString(R.string.date_15_january_2016), 20.00, 1, getString(R.string.velona_records)));
        productList.add(new Product("5", getString(R.string.mac_miller_balloonerism), getString(R.string.hip_hop_rap), getString(R.string.date_17_january_2025), 40.00, 1, getString(R.string.underground_tales_records)));
        productList.add(new Product("6", getString(R.string.manos_chatzidakis_megalos_erotikos), getString(R.string.folk_classical_entekhno), getString(R.string.date_march_1972), 45.00, 1, getString(R.string.joe_records)));
        productList.add(new Product("7", getString(R.string.tania_tsanaklidou_mama_gernao), getString(R.string.folk_classical_entekhno), getString(R.string.date_october_1981), 15.00, 1, getString(R.string.joe_records)));
        productList.add(new Product("8", getString(R.string.queen_a_night_at_the_opera), getString(R.string.rock), getString(R.string.date_21_november_1975), 38.00, 1, getString(R.string.vinyl_city)));

        SharedPreferences sharedPreferences = getSharedPreferences("ProductData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Product product : productList) {
            editor.putString(product.getId() + "_title", product.getTitle());
            editor.putString(product.getId() + "_description", product.getDescription());
            editor.putString(product.getId() + "_price", String.valueOf(product.getPrice()));
        }
        editor.apply();

        SharedPreferences imagePrefs = getSharedPreferences("ProductImages", MODE_PRIVATE);
        SharedPreferences.Editor imageEditor = imagePrefs.edit();
        imageEditor.putInt("1", R.drawable.mayhem);
        imageEditor.putInt("2", R.drawable.abbey);
        imageEditor.putInt("3", R.drawable.let);
        imageEditor.putInt("4", R.drawable.death);
        imageEditor.putInt("5", R.drawable.ball);
        imageEditor.putInt("6", R.drawable.megalos);
        imageEditor.putInt("7", R.drawable.mama);
        imageEditor.putInt("8", R.drawable.queen);
        imageEditor.apply();
    }

    private void checkAndRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        // Έλεγχος αν το δικαίωμα τοποθεσίας δεν έχει δοθεί
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Έλεγχος αν το δικαίωμα ειδοποιήσεων δεν έχει δοθεί (για Android 13+)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Αν υπάρχουν δικαιώματα που λείπουν, τα ζητάμε από τον χρήστη
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Αν έχουμε ήδη δικαιώματα, προχωράμε στην απόκτηση τοποθεσίας
            getUserLocation();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            boolean locationGranted = false;
            boolean notificationsGranted = false;

            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    locationGranted = true;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        permissions[i].equals(Manifest.permission.POST_NOTIFICATIONS) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    notificationsGranted = true;
                }
            }

            if (locationGranted) {
                getUserLocation();
            } else {
                Toast.makeText(this, getString(R.string.location_permission_required), Toast.LENGTH_SHORT).show();
            }

            if (!notificationsGranted) {
                Log.d("NotificationTest", "Ο χρήστης δεν έδωσε δικαίωμα ειδοποιήσεων.");
            }
        }
    }

    private void getUserLocation() {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.d("Location", "Lat: " + latitude + ", Lng: " + longitude);
                        } else {
                            Log.e("Location", "Location is null");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Location", "Failed to get location", e));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void checkNearbyStores() {
        Log.d("LocationTest", "checkNearbyStores started");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LocationTest", "No location permission granted");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d("LocationTest", "Got location: " + location.getLatitude() + ", " + location.getLongitude());

                        for (Store store : storesList) {
                            float[] results = new float[1];
                            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                    store.getLatitude(), store.getLongitude(), results);

                            Log.d("LocationTest", "Distance to " + store.getName() + ": " + results[0] + "m");

                            if (results[0] < 200) {
                                Log.d("LocationTest", "Nearby store found: " + store.getName());
                                sendNotification(store.getName(), results[0]);
                            }
                        }
                    } else {
                        Log.d("LocationTest", "Location is null");
                    }
                });
    }

    private void sendNotification(String storeName, float distance) {
        Log.d("NotificationTest", "Sending notification for " + storeName + " at distance: " + distance);

        createNotificationChannel();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("NotificationTest", "Notification permission not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
            return;
        }

        Intent intent = new Intent(this, ProductListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Χρησιμοποιούμε το getString για να φορτώσουμε τα μεταφρασμένα κείμενα
        String contentTitle = getString(R.string.nearby_store_title);
        String contentText = getString(R.string.nearby_store_text, (int) distance, storeName);

        // Δημιουργία της ειδοποίησης
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "store_channel")
                .setSmallIcon(R.drawable.cart)
                .setContentTitle(contentTitle) // Τίτλος από τα strings.xml
                .setContentText(contentText)   // Κείμενο από τα strings.xml
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = new Random().nextInt();
        notificationManager.notify(notificationId, builder.build());
        Log.d("NotificationTest", "Notification sent with id " + notificationId);
    }

    private void loadStoresFromFirebase() {
        storesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storesList.clear();

                for (DataSnapshot storeSnapshot : snapshot.getChildren()) {

                    // Προσπάθεια ανάγνωσης ως String της κάθε γραμμής στη ΒΔ
                    String rawData = storeSnapshot.getValue(String.class);

                    if (rawData != null) {
                        // Αν το rawData δεν έχει τις αγκύλες, τις προσθέτουμε για να γίνει έγκυρο JSON
                        String jsonStr = rawData.trim();
                        if (!jsonStr.startsWith("{")) {
                            jsonStr = "{" + jsonStr;
                        }
                        if (!jsonStr.endsWith("}")) {
                            jsonStr = jsonStr + "}";
                        }

                        try {
                            JSONObject jsonObject = new JSONObject(jsonStr);
                            String name = jsonObject.optString("name", null);
                            String address = jsonObject.optString("address", null);

                            storesList.add(new Store(name, address));
                            //Log.d("FirebaseTest", "Loaded store: " + name + " - Address: " + address);
                        } catch (JSONException e) {
                            Log.e("FirebaseTest", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }
                Log.d("FirebaseTest", "Loaded " + storesList.size() + " stores from Firebase");
                checkNearbyStores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseTest", "Error loading stores: " + error.getMessage());
            }
        });
    }

    private void createNotificationChannel() {
        String channelId = "store_channel";
        String channelName = "Store Notifications";
        String channelDescription = "Notifications for nearby stores";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }


}
