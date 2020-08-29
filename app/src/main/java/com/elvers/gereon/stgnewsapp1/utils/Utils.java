package com.elvers.gereon.stgnewsapp1.utils;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import com.elvers.gereon.stgnewsapp1.R;
import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.object.Category;
import com.google.android.material.navigation.NavigationView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Utils is a class responsible for holding static variables and methods used by multiple Activities
 * that don't need to be contained within them.
 * This makes future edits easier as it centralizes methods and maximizes code reusability
 *
 * @author Gereon Elvers
 */
public final class Utils {

    // Tag for log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Create a private Utils constructor to prevent creation of a Utils object. This class is only
     * meant to hold static variables and methods, it should not be treated as an object!
     */
    private Utils() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem building the URL: " + e.toString());
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Make an HTTP GET request to the given URL and return the response as a String.
     */
    public static String makeHttpGetRequest(URL url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        return client.newCall(request).execute().body().string();
    }

    /**
     * Make an HTTP POST request to the given URL and return the response code.
     */
    public static int makeHttpPostRequest(URL url) throws IOException {
        int responseCode = -1;

        // If the URL is null, then return an early response.
        // No need to delay or cause IOException here.
        if (url == null)
            return responseCode;

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .header("Content-Length", "");
        Request request = requestBuilder.build();
        responseCode = client.newCall(request).execute().code();
        return responseCode;
    }

    /**
     * Fills the navigationMenu with category filter and some special items
     */
    public static void createCategoryMenu(Menu navigationMenu, NavigationView navigationView, DrawerLayout drawerLayout) {
        // if there was no category request, don't fill in the items; DON'T perform the request,
        // because of potentially blocking the ui thread (or rather throwing an exception and crashing)
        if (WordPressAPI.hasCachedCategories()) {
            navigationMenu.add(R.id.mainGroup, -2, 0, R.string.favorites_title);
            navigationMenu.getItem(0).setCheckable(true);
            navigationMenu.add(R.id.mainGroup, -1, 1, ContextApp.getApplication().getResources().getString(R.string.all_articles_cat));
            navigationMenu.getItem(1).setCheckable(true);
            for (int i = 0; i < WordPressAPI.getCategories(false).size(); i++) {
                Category category = WordPressAPI.getCategories(false).get(i);
                navigationMenu.add(/*Group ID*/R.id.categoryGroup, /*itemID*/category.id, /*Order*/i + 2, /*name*/category.name);
                navigationMenu.getItem(i + 2).setCheckable(true);
            }
            navigationMenu.add(R.id.authorGroup, -3, WordPressAPI.getCategories(false).size() + 2, R.string.authors);
            navigationMenu.getItem(navigationMenu.size() - 1).setCheckable(true);

            navigationView.invalidate();
            drawerLayout.invalidate();
            drawerLayout.requestLayout();
        } else {
            Log.e(LOG_TAG, "Failed to create category menu: no category response");
        }
    }

    /**
     * Sets the theme of the activity based on the current night mode setting
     *
     * @param activity activity to update
     */
    public static void updateNightMode(Activity activity) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            activity.setTheme(R.style.AppThemeDark);
        } else {
            activity.setTheme(R.style.AppTheme);
        }
    }

    /**
     * Sets night mode state which is used to determine which theme should be used for all activities
     */
    public static void updateGlobalNightMode(Activity activity) {
        AppCompatDelegate.setDefaultNightMode(PreferenceManager.getDefaultSharedPreferences(activity)
                .getBoolean("dark_mode", false) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * Read text asset and return content
     *
     * @param name Name of the file to load
     * @param context Current context
     * @return File content
     * @throws IOException If failed to load file
     */
    public static String loadTextAsset(String name, Context context) throws IOException {
        Log.i(LOG_TAG, "Loading dark theme JavaScript file. This should only happen once");
        InputStream in = context.getAssets().open(name);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[in.available()];
        int size;
        while ((size = in.read(buffer)) != -1) {
            bos.write(buffer, 0, size);
        }
        bos.close();
        in.close();
        return new String(bos.toByteArray());
    }

    /**
     * Load JavaScript asset, optimize size and remove (single line) comments
     *
     * @param name Name of the javascript file to load
     * @param context Current context
     * @return JavaScript string
     * @throws IOException If failed to load file
     */
    public static String loadJavaScriptAsset(String name, Context context) throws IOException {
        String src = loadTextAsset(name, context);
        StringBuilder dst = new StringBuilder();
        for (String ln : src.split("\n")) {
            if (!ln.trim().startsWith("//") && !ln.isEmpty()) {
                int index = ln.indexOf("//");
                if (index != -1) {
                    ln = ln.substring(0, index);
                }
                dst.append(ln).append("\n");
            }
        }
        return dst.toString();
    }

}