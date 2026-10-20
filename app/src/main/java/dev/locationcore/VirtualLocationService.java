package dev.locationcore;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;

import dev.locationcore.engine.GeoPoint;
import dev.locationcore.engine.GpxParser;
import dev.locationcore.engine.Route;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import java.util.Locale;

/** Foreground owner for active virtualization. It never restarts automatically after process death. */
public final class VirtualLocationService extends Service {
    static final String PREFS = "locationcore_state";
    static final String KEY_ACTIVE = "active";
    static final String KEY_STALE_MOCK_CACHE = "stale_mock_cache";
    static final String KEY_MODE = "mode";
    static final String KEY_ERROR = "error";
    private static final String ACTION_START_STATIC = "dev.locationcore.START_STATIC";
    private static final String ACTION_START_MOVEMENT = "dev.locationcore.START_MOVEMENT";
    private static final String ACTION_START_ROUTE = "dev.locationcore.START_ROUTE";
    private static final String ACTION_STOP = "dev.locationcore.STOP";
    private static final String EXTRA_LATITUDE = "latitude";
    private static final String EXTRA_LONGITUDE = "longitude";
    private static final String EXTRA_ACCURACY = "accuracy";
    private static final String EXTRA_DESTINATION_LATITUDE = "destination_latitude";
    private static final String EXTRA_DESTINATION_LONGITUDE = "destination_longitude";
    private static final String EXTRA_SPEED = "speed";
    private static final String EXTRA_ROUTE_URI = "route_uri";
    private static final String CHANNEL = "locationcore_active";
    private static final int NOTIFICATION_ID = 4301;

    private MockLocationController controller;

    static Intent startIntent(Context context, double latitude, double longitude, float accuracy) {
        return new Intent(context, VirtualLocationService.class)
                .setAction(ACTION_START_STATIC)
                .putExtra(EXTRA_LATITUDE, latitude)
                .putExtra(EXTRA_LONGITUDE, longitude)
                .putExtra(EXTRA_ACCURACY, accuracy);
    }

    static Intent stopIntent(Context context) {
        return new Intent(context, VirtualLocationService.class).setAction(ACTION_STOP);
    }

    static Intent movementIntent(Context context, double fromLatitude, double fromLongitude,
                                 double toLatitude, double toLongitude, double speed, float accuracy) {
        return startIntent(context, fromLatitude, fromLongitude, accuracy)
                .setAction(ACTION_START_MOVEMENT)
                .putExtra(EXTRA_DESTINATION_LATITUDE, toLatitude)
                .putExtra(EXTRA_DESTINATION_LONGITUDE, toLongitude)
                .putExtra(EXTRA_SPEED, speed);
    }

    static Intent routeIntent(Context context, Uri uri, double speed, float accuracy) {
        return new Intent(context, VirtualLocationService.class)
                .setAction(ACTION_START_ROUTE)
                .setData(uri)
                .putExtra(EXTRA_ROUTE_URI, uri.toString())
                .putExtra(EXTRA_SPEED, speed)
                .putExtra(EXTRA_ACCURACY, accuracy)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }

    @Override public void onCreate() {
        super.onCreate();
        controller = new MockLocationController((LocationManager) getSystemService(LOCATION_SERVICE));
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(KEY_ACTIVE, false).apply();
        NotificationManager notifications = getSystemService(NotificationManager.class);
        notifications.createNotificationChannel(new NotificationChannel(
                CHANNEL, "Active virtual location", NotificationManager.IMPORTANCE_LOW));
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || ACTION_STOP.equals(intent.getAction())) {
            stopAndExit();
            return START_NOT_STICKY;
        }
        String action = intent.getAction();
        if (!ACTION_START_STATIC.equals(action)
                && !ACTION_START_MOVEMENT.equals(action)
                && !ACTION_START_ROUTE.equals(action)) {
            stopAndExit();
            return START_NOT_STICKY;
        }

        float accuracy = intent.getFloatExtra(EXTRA_ACCURACY, Float.NaN);
        String mode = ACTION_START_STATIC.equals(action) ? "STATIC"
                : ACTION_START_MOVEMENT.equals(action) ? "MOVEMENT" : "ROUTE";
        startForeground(NOTIFICATION_ID, notification(mode));
        try {
            if (ACTION_START_STATIC.equals(action)) {
                controller.startStatic(
                        intent.getDoubleExtra(EXTRA_LATITUDE, Double.NaN),
                        intent.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN), accuracy);
            } else if (ACTION_START_MOVEMENT.equals(action)) {
                Route route = new Route(Arrays.asList(
                        new GeoPoint(intent.getDoubleExtra(EXTRA_LATITUDE, Double.NaN),
                                intent.getDoubleExtra(EXTRA_LONGITUDE, Double.NaN)),
                        new GeoPoint(intent.getDoubleExtra(EXTRA_DESTINATION_LATITUDE, Double.NaN),
                                intent.getDoubleExtra(EXTRA_DESTINATION_LONGITUDE, Double.NaN))));
                controller.startMovement(route, intent.getDoubleExtra(EXTRA_SPEED, Double.NaN), accuracy);
            } else {
                Uri routeUri = Uri.parse(intent.getStringExtra(EXTRA_ROUTE_URI));
                try (InputStream input = getContentResolver().openInputStream(routeUri)) {
                    controller.startRoute(GpxParser.parse(input),
                            intent.getDoubleExtra(EXTRA_SPEED, Double.NaN), accuracy);
                }
            }
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                    .putBoolean(KEY_ACTIVE, true)
                    .putBoolean(KEY_STALE_MOCK_CACHE, false)
                    .putString(KEY_MODE, mode)
                    .remove(KEY_ERROR)
                    .apply();
        } catch (RuntimeException | IOException error) {
            Throwable root = error;
            while (root.getCause() != null) root = root.getCause();
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                    .putString(KEY_ERROR, error.getClass().getSimpleName() + ": "
                            + String.valueOf(error.getMessage()) + " / "
                            + root.getClass().getSimpleName() + ": " + String.valueOf(root.getMessage()))
                    .apply();
            stopAndExit();
        }
        return START_NOT_STICKY;
    }

    @Override public void onTaskRemoved(Intent rootIntent) {
        stopAndExit();
    }

    @Override public void onDestroy() {
        controller.stop();
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(KEY_ACTIVE, false).apply();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }

    private void stopAndExit() {
        controller.stop();
        boolean staleMockCache = controller.hasMockLastKnown();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_ACTIVE, false)
                .putBoolean(KEY_STALE_MOCK_CACHE, staleMockCache)
                .putString(KEY_MODE, "REAL")
                .apply();
        stopForeground(STOP_FOREGROUND_REMOVE);
        stopSelf();
    }

    private Notification notification(String mode) {
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(this, 0, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new Notification.Builder(this, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setContentTitle("LocationCore · " + mode)
                .setContentText("Virtual location active · tap to control")
                .setOngoing(true)
                .setContentIntent(pending)
                .build();
    }
}
