package com.elvers.gereon.stgnewsapp1.api.response;

import android.util.Log;

import androidx.preference.PreferenceManager;

import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.object.Post;
import com.elvers.gereon.stgnewsapp1.api.object.Category;
import com.elvers.gereon.stgnewsapp1.utils.ContextApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsResponse extends AbstractResponse {

    // Tag for log messages
    private static final String LOG_TAG = "ArticlesResponse";

    public final List<Post> posts = new ArrayList<>();

    public PostsResponse(String json) {
        super(json);

        // check if there were any error messages
        try {
            JSONObject obj = new JSONObject(json);
            if (obj.has("code")) // has error(s)
                return;
        } catch (JSONException ignored) {}

        // parse articles
        try {
            JSONArray array = new JSONArray(json);

            boolean highResCover = PreferenceManager.getDefaultSharedPreferences(ContextApp.getContext()).getBoolean("high_res", false);

            for (int i = 0; i < array.length(); i++) {
                JSONObject articleObject = array.getJSONObject(i);

                // Get WordPress Article ID
                int id = articleObject.getInt("id");

                // Get article URL
                String urlString = articleObject.getString("link");

                // Get article title
                String titleString = articleObject.getJSONObject("title").getString("rendered");

                // Get article authors name
                String authorString = WordPressAPI.getUserName(articleObject.getInt("author"));

                // Get article date
                String dateStringInput = articleObject.getString("date");
                Date articleDate;
                try {
                    articleDate = WordPressAPI.wpDateFormat.parse(dateStringInput, new ParsePosition(0));
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error parsing dateString into format: " + e.toString());
                    e.printStackTrace();
                    continue;
                }
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault());
                String dateString = outputFormat.format(articleDate);

                // Get article preview image
                String imageUrlString = "";
                JSONObject imgObj = null;
                try {
                    JSONObject betterFeaturedImage = articleObject.getJSONObject("better_featured_image");
                    //TODO select best image based on display size
                    if (!highResCover) {
                        JSONObject media_details = betterFeaturedImage.getJSONObject("media_details");
                        JSONObject sizes = media_details.getJSONObject("sizes");
                        try {
                            imgObj = sizes.getJSONObject("large");
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "Failed to get large image");
                        }
                        try {
                            if (imgObj == null) {
                                imgObj = sizes.getJSONObject("medium");
                            }
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "Failed to get medium image");
                        }
                        try {
                            if (imgObj == null) {
                                imgObj = sizes.getJSONObject("thumbnail");
                            }
                        } catch (Exception e) {
                            Log.i(LOG_TAG, "Failed to get thumbnail image");
                        }
                    } else {
                        imgObj = betterFeaturedImage;
                    }

                    if (imgObj != null) {
                        imageUrlString = imgObj.getString("source_url");
                    }

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Image Conversion for article " + i + " Can't parse img through betterFeaturedImage: " + e.toString());
                    e.printStackTrace();
                }

                // Get articles categories
                StringBuilder categoryString = new StringBuilder();
                boolean isFirstCategory = true;

                JSONArray articleCategoryArray = articleObject.getJSONArray("categories");
                for (int j = 0; j < articleCategoryArray.length(); j++) {
                    int targetCategoryId = articleCategoryArray.getInt(j);
                    Category category = WordPressAPI.getCategoryById(targetCategoryId);
                    if (category != null) {
                        if (isFirstCategory) {
                            categoryString.append(category.name);
                            isFirstCategory = false;
                        } else {
                            categoryString.append(", ").append(category.name);
                        }
                    }
                }

                // After the individual parts of the Article are retrieved individually, they are parsed into an Article object and added to the array
                posts.add(new Post(id, titleString, authorString, dateString, urlString, imageUrlString, categoryString.toString()));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing article JSON: " + e.toString());
            e.printStackTrace();
        }
    }
}
