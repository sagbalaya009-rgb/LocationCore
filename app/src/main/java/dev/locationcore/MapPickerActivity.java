package dev.locationcore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

/** Native offline coordinate-grid picker. */
public final class MapPickerActivity extends Activity {
    static final String EXTRA_LATITUDE = "latitude";
    static final String EXTRA_LONGITUDE = "longitude";
    private double latitude;
    private double longitude;
    private boolean selected;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        FrameLayout root = new FrameLayout(this);
        CoordinateView map = new CoordinateView(this, (lat, lon) -> {
            latitude = lat;
            longitude = lon;
            selected = true;
            value.setText(String.format(Locale.US, "%.6f, %.6f", lat, lon));
            use.setEnabled(true);
        });
        root.addView(map, new FrameLayout.LayoutParams(-1, -1));

        TextView title = new TextView(this);
        title.setText("Offline coordinate map");
        title.setTextSize(18);
        title.setTextColor(0xff193642);
        title.setTypeface(null, 1);
        title.setBackgroundColor(0xeeffffff);
        title.setPadding(dp(14), dp(10), dp(14), dp(10));
        FrameLayout.LayoutParams titleParams = new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.START);
        titleParams.setMargins(dp(16), dp(16), dp(16), 0);
        root.addView(title, titleParams);

        LinearLayout panel = new LinearLayout(this);
        panel.setGravity(Gravity.CENTER_VERTICAL);
        panel.setPadding(dp(14), dp(10), dp(10), dp(10));
        panel.setBackgroundColor(Color.WHITE);
        value = new TextView(this);
        value.setText("Tap the grid to choose a point");
        value.setTextSize(15);
        value.setTextColor(0xff233a45);
        panel.addView(value, new LinearLayout.LayoutParams(0, -2, 1f));
        use = new Button(this);
        use.setText("Use point");
        use.setAllCaps(false);
        use.setEnabled(false);
        use.setOnClickListener(v -> {
            if (!selected) return;
            setResult(RESULT_OK, new Intent()
                    .putExtra(EXTRA_LATITUDE, latitude)
                    .putExtra(EXTRA_LONGITUDE, longitude));
            finish();
        });
        panel.addView(use, new LinearLayout.LayoutParams(-2, -2));
        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM);
        panelParams.setMargins(dp(16), 0, dp(16), dp(18));
        root.addView(panel, panelParams);

        setContentView(root);
    }

    private TextView value;
    private Button use;

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private interface SelectionListener { void onSelected(double latitude, double longitude); }

    private static final class CoordinateView extends View {
        private final Paint grid = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint axis = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint label = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint marker = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final SelectionListener listener;
        private Double latitude;
        private Double longitude;

        CoordinateView(Context context, SelectionListener listener) {
            super(context);
            this.listener = listener;
            grid.setColor(0xff88a9b6);
            grid.setStrokeWidth(1f);
            axis.setColor(0xff176b87);
            axis.setStrokeWidth(3f);
            label.setColor(0xff486773);
            label.setTextSize(13f * getResources().getDisplayMetrics().scaledDensity);
            marker.setColor(0xffb31e2f);
            setBackgroundColor(0xffdbeaf0);
        }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth(), height = getHeight();
            for (int lon = -180; lon <= 180; lon += 30) {
                float x = (lon + 180f) / 360f * width;
                canvas.drawLine(x, 0, x, height, grid);
                if (lon > -180 && lon < 180) canvas.drawText(lon + "°", x + 4, dp(42), label);
            }
            for (int lat = -90; lat <= 90; lat += 30) {
                float y = (90f - lat) / 180f * height;
                canvas.drawLine(0, y, width, y, grid);
                if (lat > -90 && lat < 90) canvas.drawText(lat + "°", dp(5), y - 5, label);
            }
            canvas.drawLine(width / 2, 0, width / 2, height, axis);
            canvas.drawLine(0, height / 2, width, height / 2, axis);
            if (latitude != null && longitude != null) {
                float x = (float) ((longitude + 180) / 360 * width);
                float y = (float) ((90 - latitude) / 180 * height);
                canvas.drawCircle(x, y, dp(8), marker);
            }
        }

        @Override public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_UP) return true;
            longitude = event.getX() / getWidth() * 360.0 - 180.0;
            latitude = 90.0 - event.getY() / getHeight() * 180.0;
            invalidate();
            listener.onSelected(latitude, longitude);
            return true;
        }

        private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    }
}
