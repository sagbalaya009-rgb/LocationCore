package dev.locationcore;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Foreground-only, read-only provider diagnostic client. No coordinates are logged or stored. */
public final class MainActivity extends Activity {
    private static final String[] PROVIDERS = {"gps", "network", "fused", "passive"};
    private final Map<String, TextView> panels = new LinkedHashMap<>();
    private final Map<String, Integer> counts = new LinkedHashMap<>();
    private final Map<String, LocationListener> listeners = new LinkedHashMap<>();
    private LocationManager manager;
    private TextView permissionBanner;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ScrollView scroll = new ScrollView(this);
        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(dp(20), dp(24), dp(20), dp(24));
        column.setBackgroundColor(0xfff5f7f8);
        scroll.addView(column);

        TextView title = label("LocationCore", 28, 0xff132a36);
        title.setTypeface(null, 1);
        column.addView(title);
        TextView subtitle = label("Provider diagnostics · foreground only", 14, 0xff526671);
        column.addView(subtitle);

        TextView status = label("● REAL", 22, 0xff0b7452);
        status.setTypeface(null, 1);
        status.setPadding(dp(16), dp(15), dp(16), dp(15));
        status.setBackgroundColor(0xffe1f2e9);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.topMargin = dp(22);
        statusParams.bottomMargin = dp(16);
        column.addView(status, statusParams);

        permissionBanner = label("Requesting location permission…", 14, 0xff526671);
        column.addView(permissionBanner);
        TextView note = label("This build observes live Android provider updates. It does not change location or retain coordinates.", 14, 0xff526671);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(-1, -2);
        noteParams.topMargin = dp(8);
        noteParams.bottomMargin = dp(16);
        column.addView(note, noteParams);

        for (String provider : PROVIDERS) {
            counts.put(provider, 0);
            TextView panel = label(provider.toUpperCase(Locale.ROOT) + "\nWaiting for update", 14, 0xff132a36);
            panel.setPadding(dp(16), dp(14), dp(16), dp(14));
            panel.setBackgroundColor(0xffffffff);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.bottomMargin = dp(10);
            column.addView(panel, params);
            panels.put(provider, panel);
        }
        setContentView(scroll);
    }

    @Override public void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            subscribe();
        }
    }

    @Override public void onPause() {
        super.onPause();
        for (LocationListener listener : listeners.values()) manager.removeUpdates(listener);
        listeners.clear();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            subscribe();
        } else {
            permissionBanner.setText("Precise location permission is required for provider diagnostics.");
        }
    }

    private void subscribe() {
        permissionBanner.setText("Listening while this screen is open");
        for (String provider : PROVIDERS) {
            TextView panel = panels.get(provider);
            if (panel == null) continue;
            if (!manager.getAllProviders().contains(provider)) {
                panel.setText(provider.toUpperCase(Locale.ROOT) + "\nUnavailable on this device");
                continue;
            }
            try {
                LocationListener listener = new LocationListener() {
                    @Override public void onLocationChanged(Location location) { showLocation(provider, location); }
                    @Override public void onProviderEnabled(String name) { updateProviderState(provider, "Enabled; waiting for live update"); }
                    @Override public void onProviderDisabled(String name) { updateProviderState(provider, "Disabled"); }
                };
                manager.requestLocationUpdates(provider, 1000L, 0f, listener, Looper.getMainLooper());
                listeners.put(provider, listener);
                panel.setText(provider.toUpperCase(Locale.ROOT) + "\nSubscribed; waiting for live update");
            } catch (SecurityException | IllegalArgumentException error) {
                panel.setText(provider.toUpperCase(Locale.ROOT) + "\nSubscription failed: " + error.getClass().getSimpleName());
            }
        }
    }

    private void showLocation(String subscription, Location location) {
        TextView panel = panels.get(subscription);
        if (panel == null) return;
        int count = counts.get(subscription) + 1;
        counts.put(subscription, count);
        long ageMs = (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()) / 1000000L;
        StringBuilder value = new StringBuilder();
        value.append(subscription.toUpperCase(Locale.ROOT)).append(" · ").append(count).append(" updates\n");
        if (!subscription.equals(location.getProvider())) value.append("Source: ").append(location.getProvider()).append("\n");
        value.append(String.format(Locale.US, "%.6f, %.6f", location.getLatitude(), location.getLongitude()));
        value.append("\nAccuracy: ").append(location.hasAccuracy() ? String.format(Locale.US, "%.1f m", location.getAccuracy()) : "n/a");
        value.append(" · Altitude: ").append(location.hasAltitude() ? String.format(Locale.US, "%.1f m", location.getAltitude()) : "n/a");
        value.append("\nSpeed: ").append(location.hasSpeed() ? String.format(Locale.US, "%.2f m/s", location.getSpeed()) : "n/a");
        value.append(" · Bearing: ").append(location.hasBearing() ? String.format(Locale.US, "%.1f°", location.getBearing()) : "n/a");
        value.append("\nTime: ").append(DateFormat.getDateTimeInstance().format(new Date(location.getTime())));
        value.append(" · Age: ").append(ageMs).append(" ms");
        value.append("\nMock: ").append(location.isMock());
        panel.setText(value.toString());
    }

    private void updateProviderState(String provider, String state) {
        TextView panel = panels.get(provider);
        if (panel != null) panel.setText(provider.toUpperCase(Locale.ROOT) + "\n" + state);
    }

    private TextView label(String text, int sp, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(sp);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER_VERTICAL);
        return view;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}

