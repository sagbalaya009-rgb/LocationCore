package dev.locationcore;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Small local store for user-created favorites and recent selections. */
final class PlaceStore {
    static final class Place {
        final String name;
        final double latitude;
        final double longitude;

        Place(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override public String toString() {
            return name + "\n" + String.format(java.util.Locale.US, "%.6f, %.6f", latitude, longitude);
        }
    }

    private static final String PREFS = "locationcore_places";
    private static final String FAVORITES = "favorites";
    private static final String RECENTS = "recents";
    private final SharedPreferences preferences;

    PlaceStore(Context context) {
        preferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    List<Place> favorites() { return load(FAVORITES); }
    List<Place> recents() { return load(RECENTS); }

    void addFavorite(String name, double latitude, double longitude) {
        List<Place> values = load(FAVORITES);
        values.add(0, new Place(name.trim().isEmpty() ? "Favorite" : name.trim(), latitude, longitude));
        save(FAVORITES, values, 30);
    }

    void addRecent(String name, double latitude, double longitude) {
        List<Place> values = load(RECENTS);
        values.removeIf(place -> Math.abs(place.latitude - latitude) < 1e-9
                && Math.abs(place.longitude - longitude) < 1e-9);
        values.add(0, new Place(name, latitude, longitude));
        save(RECENTS, values, 20);
    }

    private List<Place> load(String key) {
        List<Place> values = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(preferences.getString(key, "[]"));
            for (int i = 0; i < json.length(); i++) {
                JSONObject item = json.getJSONObject(i);
                values.add(new Place(item.getString("name"), item.getDouble("latitude"), item.getDouble("longitude")));
            }
        } catch (Exception ignored) {
            preferences.edit().remove(key).apply();
        }
        return values;
    }

    private void save(String key, List<Place> values, int maximum) {
        JSONArray json = new JSONArray();
        for (int i = 0; i < Math.min(values.size(), maximum); i++) {
            Place place = values.get(i);
            JSONObject item = new JSONObject();
            try {
                item.put("name", place.name);
                item.put("latitude", place.latitude);
                item.put("longitude", place.longitude);
                json.put(item);
            } catch (Exception ignored) { }
        }
        preferences.edit().putString(key, json.toString()).apply();
    }
}
