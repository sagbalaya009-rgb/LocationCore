package dev.locationcore;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.location.Location;
import android.location.LocationManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

/** User-facing controller. It starts in REAL mode and requires Android's mock-app grant. */
public final class MainActivity extends Activity {
    private static final int PICK_GPX = 42;
    private static final int PICK_MAP = 43;
    private final Handler refreshHandler = new Handler(Looper.getMainLooper());
    private TextView modeCard;
    private TextView setupCard;
    private TextView diagnostics;
    private EditText latitude;
    private EditText longitude;
    private EditText accuracy;
    private EditText destinationLatitude;
    private EditText destinationLongitude;
    private EditText speed;
    private EditText search;
    private TextView searchStatus;
    private PlaceStore placeStore;
    private String currentLabel = "Manual selection";

    private final Runnable refresh = new Runnable() {
        @Override public void run() {
            renderState();
            refreshHandler.postDelayed(this, 1000L);
        }
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        placeStore = new PlaceStore(this);
        if (!serviceRunning()) {
            getSharedPreferences(VirtualLocationService.PREFS, MODE_PRIVATE).edit()
                    .putBoolean(VirtualLocationService.KEY_ACTIVE, false)
                    .putBoolean(VirtualLocationService.KEY_STALE_MOCK_CACHE, hasMockLastKnown())
                    .putString(VirtualLocationService.KEY_MODE, "REAL")
                    .apply();
        }
        setContentView(buildUi());
    }

    @Override public void onResume() {
        super.onResume();
        refreshHandler.post(refresh);
    }

    @Override public void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refresh);
    }

    private View buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(22), dp(24), dp(22), dp(32));
        column.setBackgroundColor(0xfff4f6f7);
        scroll.addView(column);

        TextView title = text("LocationCore", 30, 0xff142b36);
        title.setTypeface(null, 1);
        column.addView(title);
        column.addView(text("FOSSiBOT M20 controller · development build", 14, 0xff5b6d76));

        modeCard = text("● REAL", 24, 0xff0b7452);
        modeCard.setTypeface(null, 1);
        modeCard.setPadding(dp(16), dp(18), dp(16), dp(18));
        column.addView(modeCard, fullWidth(dp(22), dp(12)));

        setupCard = text("Checking Android mock-location access…", 14, 0xff3d515b);
        setupCard.setPadding(dp(14), dp(14), dp(14), dp(14));
        setupCard.setBackgroundColor(Color.WHITE);
        column.addView(setupCard, fullWidth(0, dp(16)));

        Button settings = button("Open developer options");
        settings.setOnClickListener(v -> {
            try { startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)); }
            catch (Exception ignored) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
        });
        column.addView(settings, fullWidth(0, dp(22)));

        TextView chooseTitle = text("CHOOSE LOCATION", 13, 0xff5b6d76);
        chooseTitle.setTypeface(null, 1);
        column.addView(chooseTitle);
        search = textField("Search for a place", "");
        column.addView(search, fullWidth(dp(10), dp(8)));
        Button searchButton = button("Search");
        searchButton.setOnClickListener(v -> searchPlace());
        column.addView(searchButton, fullWidth(0, dp(8)));
        Button mapButton = button("Choose on interactive map");
        mapButton.setOnClickListener(v -> startActivityForResult(new Intent(this, MapPickerActivity.class), PICK_MAP));
        column.addView(mapButton, fullWidth(0, dp(8)));
        searchStatus = text("Search requires internet; the map, manual coordinates and engine work offline.", 13, 0xff5b6d76);
        column.addView(searchStatus, fullWidth(0, dp(5)));
        column.addView(text("Search data © OpenStreetMap contributors.", 12, 0xff5b6d76), fullWidth(0, dp(22)));

        TextView staticTitle = text("STATIC LOCATION", 13, 0xff5b6d76);
        staticTitle.setTypeface(null, 1);
        column.addView(staticTitle);

        latitude = field("Latitude (-90 to 90)", "0.000000");
        longitude = field("Longitude (-180 to 180)", "0.000000");
        accuracy = field("Accuracy in metres", "5.0");
        column.addView(latitude, fullWidth(dp(10), dp(8)));
        column.addView(longitude, fullWidth(0, dp(8)));
        column.addView(accuracy, fullWidth(0, dp(12)));

        Button saveFavorite = button("Save as favorite");
        saveFavorite.setOnClickListener(v -> saveFavorite());
        column.addView(saveFavorite, fullWidth(0, dp(8)));
        Button favorites = button("Open favorites");
        favorites.setOnClickListener(v -> choosePlace("Favorites", placeStore.favorites()));
        column.addView(favorites, fullWidth(0, dp(8)));
        Button recents = button("Open recent locations");
        recents.setOnClickListener(v -> choosePlace("Recent locations", placeStore.recents()));
        column.addView(recents, fullWidth(0, dp(12)));

        Button start = button("Start static location");
        start.setTextColor(Color.WHITE);
        start.setBackgroundColor(0xff176b87);
        start.setOnClickListener(v -> startStatic());
        column.addView(start, fullWidth(0, dp(10)));

        Button stop = button("Stop and return to real location");
        stop.setTextColor(0xff8a1c1c);
        stop.setOnClickListener(v -> stopVirtual());
        column.addView(stop, fullWidth(0, dp(24)));

        TextView movementTitle = text("MOVEMENT", 13, 0xff5b6d76);
        movementTitle.setTypeface(null, 1);
        column.addView(movementTitle);
        destinationLatitude = field("Destination latitude", "0.000000");
        destinationLongitude = field("Destination longitude", "0.001000");
        speed = field("Speed in metres per second", "10.0");
        column.addView(destinationLatitude, fullWidth(dp(10), dp(8)));
        column.addView(destinationLongitude, fullWidth(0, dp(8)));
        column.addView(speed, fullWidth(0, dp(12)));

        Button movement = button("Start movement to destination");
        movement.setOnClickListener(v -> startMovement());
        column.addView(movement, fullWidth(0, dp(10)));

        Button gpx = button("Import and replay GPX route");
        gpx.setOnClickListener(v -> pickGpx());
        column.addView(gpx, fullWidth(0, dp(24)));

        TextView diagTitle = text("DIAGNOSTICS", 13, 0xff5b6d76);
        diagTitle.setTypeface(null, 1);
        column.addView(diagTitle);
        diagnostics = text("", 14, 0xff253c47);
        diagnostics.setPadding(dp(14), dp(14), dp(14), dp(14));
        diagnostics.setBackgroundColor(Color.WHITE);
        column.addView(diagnostics, fullWidth(dp(10), 0));

        return scroll;
    }

    private void startStatic() {
        if (!mockAllowed()) {
            Toast.makeText(this, "Select LocationCore as the mock location app first.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            double lat = Double.parseDouble(latitude.getText().toString().trim());
            double lon = Double.parseDouble(longitude.getText().toString().trim());
            float acc = Float.parseFloat(accuracy.getText().toString().trim());
            if (!Double.isFinite(lat) || lat < -90 || lat > 90) throw new IllegalArgumentException("Latitude is outside -90 to 90");
            if (!Double.isFinite(lon) || lon < -180 || lon > 180) throw new IllegalArgumentException("Longitude is outside -180 to 180");
            if (!Float.isFinite(acc) || acc <= 0 || acc > 100000) throw new IllegalArgumentException("Accuracy must be above 0 and at most 100000");
            placeStore.addRecent(currentLabel, lat, lon);
            startForegroundService(VirtualLocationService.startIntent(this, lat, lon, acc));
        } catch (IllegalArgumentException error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void stopVirtual() {
        startService(VirtualLocationService.stopIntent(this));
        renderState();
    }

    private void startMovement() {
        if (!mockAllowed()) {
            Toast.makeText(this, "Select LocationCore as the mock location app first.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            double fromLat = coordinate(latitude, -90, 90, "Start latitude");
            double fromLon = coordinate(longitude, -180, 180, "Start longitude");
            double toLat = coordinate(destinationLatitude, -90, 90, "Destination latitude");
            double toLon = coordinate(destinationLongitude, -180, 180, "Destination longitude");
            double metresPerSecond = positiveSpeed();
            float acc = positiveAccuracy();
            placeStore.addRecent(currentLabel, fromLat, fromLon);
            startForegroundService(VirtualLocationService.movementIntent(
                    this, fromLat, fromLon, toLat, toLon, metresPerSecond, acc));
        } catch (IllegalArgumentException error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void pickGpx() {
        if (!mockAllowed()) {
            Toast.makeText(this, "Select LocationCore as the mock location app first.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            positiveSpeed();
            positiveAccuracy();
            Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("*/*");
            startActivityForResult(picker, PICK_GPX);
        } catch (IllegalArgumentException error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MAP && resultCode == RESULT_OK && data != null) {
            applyPlace(new PlaceStore.Place("Map selection",
                    data.getDoubleExtra(MapPickerActivity.EXTRA_LATITUDE, 0),
                    data.getDoubleExtra(MapPickerActivity.EXTRA_LONGITUDE, 0)));
            return;
        }
        if (requestCode != PICK_GPX || resultCode != RESULT_OK || data == null || data.getData() == null) return;
        try {
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (SecurityException ignored) { }
        try {
            startForegroundService(VirtualLocationService.routeIntent(
                    this, data.getData(), positiveSpeed(), positiveAccuracy()));
        } catch (IllegalArgumentException error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void searchPlace() {
        String query = search.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a place name first.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!Geocoder.isPresent()) {
            searchNominatim(query);
            return;
        }
        searchStatus.setText("Searching…");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if (Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocationName(query, 5, new Geocoder.GeocodeListener() {
                @Override public void onGeocode(List<Address> addresses) {
                    runOnUiThread(() -> acceptSearch(query, addresses));
                }

                @Override public void onError(String errorMessage) {
                    searchNominatim(query);
                }
            });
        } else {
            new Thread(() -> {
                try {
                    List<Address> addresses = geocoder.getFromLocationName(query, 5);
                    runOnUiThread(() -> acceptSearch(query, addresses));
                } catch (IOException error) {
                    searchNominatim(query);
                }
            }, "locationcore-geocoder").start();
        }
    }

    private void acceptSearch(String query, List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            searchNominatim(query);
            return;
        }
        Address result = addresses.get(0);
        String label = result.getMaxAddressLineIndex() >= 0 ? result.getAddressLine(0) : query;
        applyPlace(new PlaceStore.Place(label, result.getLatitude(), result.getLongitude()));
        searchStatus.setText("Selected: " + label);
    }

    private void searchNominatim(String query) {
        runOnUiThread(() -> searchStatus.setText("Searching OpenStreetMap…"));
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                String encoded = URLEncoder.encode(query, "UTF-8");
                connection = (HttpURLConnection) new URL(
                        "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=5&q=" + encoded)
                        .openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("User-Agent", "LocationCore/0.3 (private device controller)");
                if (connection.getResponseCode() != 200) throw new IOException("HTTP " + connection.getResponseCode());
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (body.length() + line.length() > 262144) throw new IOException("Search response too large");
                        body.append(line);
                    }
                }
                JSONArray results = new JSONArray(body.toString());
                if (results.length() == 0) {
                    runOnUiThread(() -> searchStatus.setText("No result found for “" + query + "”."));
                    return;
                }
                JSONObject first = results.getJSONObject(0);
                PlaceStore.Place place = new PlaceStore.Place(first.getString("display_name"),
                        Double.parseDouble(first.getString("lat")),
                        Double.parseDouble(first.getString("lon")));
                runOnUiThread(() -> applyPlace(place));
            } catch (Exception error) {
                runOnUiThread(() -> searchStatus.setText("Search unavailable: " + error.getMessage()));
            } finally {
                if (connection != null) connection.disconnect();
            }
        }, "locationcore-nominatim").start();
    }

    private void saveFavorite() {
        try {
            double lat = coordinate(latitude, -90, 90, "Latitude");
            double lon = coordinate(longitude, -180, 180, "Longitude");
            EditText name = textField("Favorite name", currentLabel.equals("Manual selection") ? "" : currentLabel);
            new AlertDialog.Builder(this)
                    .setTitle("Save favorite")
                    .setView(name)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Save", (dialog, which) -> {
                        placeStore.addFavorite(name.getText().toString(), lat, lon);
                        Toast.makeText(this, "Favorite saved.", Toast.LENGTH_SHORT).show();
                    }).show();
        } catch (IllegalArgumentException error) {
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void choosePlace(String title, List<PlaceStore.Place> places) {
        if (places.isEmpty()) {
            Toast.makeText(this, title + " is empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        CharSequence[] labels = new CharSequence[places.size()];
        for (int i = 0; i < places.size(); i++) labels[i] = places.get(i).toString();
        new AlertDialog.Builder(this).setTitle(title).setItems(labels,
                (dialog, which) -> applyPlace(places.get(which))).show();
    }

    private void applyPlace(PlaceStore.Place place) {
        latitude.setText(String.format(Locale.US, "%.6f", place.latitude));
        longitude.setText(String.format(Locale.US, "%.6f", place.longitude));
        currentLabel = place.name;
        searchStatus.setText("Selected: " + place.name);
    }

    private double coordinate(EditText field, double minimum, double maximum, String name) {
        double value = Double.parseDouble(field.getText().toString().trim());
        if (!Double.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(name + " is outside " + minimum + " to " + maximum);
        }
        return value;
    }

    private double positiveSpeed() {
        double value = Double.parseDouble(speed.getText().toString().trim());
        if (!Double.isFinite(value) || value <= 0 || value > 100) {
            throw new IllegalArgumentException("Speed must be above 0 and at most 100 m/s");
        }
        return value;
    }

    private float positiveAccuracy() {
        float value = Float.parseFloat(accuracy.getText().toString().trim());
        if (!Float.isFinite(value) || value <= 0 || value > 100000) {
            throw new IllegalArgumentException("Accuracy must be above 0 and at most 100000");
        }
        return value;
    }

    private void renderState() {
        boolean allowed = mockAllowed();
        android.content.SharedPreferences state = getSharedPreferences(VirtualLocationService.PREFS, MODE_PRIVATE);
        boolean active = state.getBoolean(VirtualLocationService.KEY_ACTIVE, false);
        boolean staleMockCache = state.getBoolean(VirtualLocationService.KEY_STALE_MOCK_CACHE, false);
        String mode = state.getString(VirtualLocationService.KEY_MODE, "REAL");
        String error = state.getString(VirtualLocationService.KEY_ERROR, null);
        if (active) {
            modeCard.setText("● VIRTUAL · " + mode);
            modeCard.setTextColor(0xff7b4e00);
            modeCard.setBackgroundColor(0xffffedc5);
        } else if (staleMockCache) {
            modeCard.setText("● REAL · STALE MOCK CACHE");
            modeCard.setTextColor(0xff8a1c1c);
            modeCard.setBackgroundColor(0xffffdfdf);
        } else {
            modeCard.setText("● REAL");
            modeCard.setTextColor(0xff0b7452);
            modeCard.setBackgroundColor(0xffdff2e8);
        }
        setupCard.setText(allowed
                ? "Ready: LocationCore is selected as Android's mock location app."
                : "Setup required: open Developer options → Select mock location app → LocationCore.");
        diagnostics.setText(String.format(Locale.US,
                "Engine: %s\nMock permission: %s\nLast-known cache: %s\nProviders: GPS, network, fused, passive%s\nSafety: REAL on normal start; STOP removes test providers",
                active ? mode + " at 1 Hz" : "stopped", allowed ? "allowed" : "not allowed",
                staleMockCache ? "still contains a mock sample" : "no mock sample returned",
                error == null ? "" : "\nLast error: " + error));
    }

    private boolean mockAllowed() {
        AppOpsManager ops = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        return ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_MOCK_LOCATION, Process.myUid(), getPackageName())
                == AppOpsManager.MODE_ALLOWED;
    }

    private boolean serviceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (VirtualLocationService.class.getName().equals(service.service.getClassName())) return true;
        }
        return false;
    }

    private boolean hasMockLastKnown() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) return false;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        for (String provider : Arrays.asList(LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER, LocationManager.FUSED_PROVIDER)) {
            try {
                Location value = manager.getLastKnownLocation(provider);
                if (value != null && value.isMock()) return true;
            } catch (IllegalArgumentException | SecurityException ignored) { }
        }
        return false;
    }

    private TextView text(String value, int sp, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER_VERTICAL);
        return view;
    }

    private EditText field(String hint, String value) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setText(value);
        field.setTextSize(16);
        field.setSingleLine(true);
        field.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                | android.text.InputType.TYPE_NUMBER_FLAG_SIGNED);
        field.setPadding(dp(14), dp(12), dp(14), dp(12));
        field.setBackgroundColor(Color.WHITE);
        return field;
    }

    private EditText textField(String hint, String value) {
        EditText field = new EditText(this);
        field.setHint(hint);
        field.setText(value);
        field.setTextSize(16);
        field.setSingleLine(true);
        field.setPadding(dp(14), dp(12), dp(14), dp(12));
        field.setBackgroundColor(Color.WHITE);
        return field;
    }

    private Button button(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(15);
        button.setAllCaps(false);
        return button;
    }

    private LinearLayout.LayoutParams fullWidth(int top, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = top;
        params.bottomMargin = bottom;
        return params;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
