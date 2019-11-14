package com.jadebyte.jadeplayer.main.shazam.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Hashtable;

public class Screen {

    private static final Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static int getWidth(Context context, Configuration newConfiguration) {
        return getDisplaySize(context, newConfiguration).x;
    }

    public static int dp(Context context, float value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * value);
    }


    public static Typeface getTypeface(Context context, String assetPath) {
        synchronized (fontCache) {
            if (!fontCache.containsKey(assetPath)) {
                try {
                    Typeface t = Typeface.createFromAsset(context.getAssets(), assetPath);
                    fontCache.put(assetPath, t);
                } catch (Throwable e) {
                    Log.e("ErrorGetTypeface", String.valueOf(e));
                    return null;
                }
            }
            return fontCache.get(assetPath);
        }
    }

    public static Point getDisplaySize(Context context, Configuration newConfiguration) {
        Point displaySize = new Point();
            float density = context.getResources().getDisplayMetrics().density;
            Configuration configuration = newConfiguration;
            if (configuration == null) {
                configuration = context.getResources().getConfiguration();
            }
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (manager != null) {
                Display display = manager.getDefaultDisplay();
                if (display != null) {
                    display.getSize(displaySize);
                }
            }
            if (configuration.screenWidthDp != Configuration.SCREEN_WIDTH_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenWidthDp * density);
                if (Math.abs(displaySize.x - newSize) > 3) {
                    displaySize.x = newSize;
                }
            }
            if (configuration.screenHeightDp != Configuration.SCREEN_HEIGHT_DP_UNDEFINED) {
                int newSize = (int) Math.ceil(configuration.screenHeightDp * density);
                if (Math.abs(displaySize.y - newSize) > 3) {
                    displaySize.y = newSize;
                }
            }
        return displaySize;
    }
}
