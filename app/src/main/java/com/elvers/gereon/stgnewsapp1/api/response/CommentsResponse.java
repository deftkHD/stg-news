package com.elvers.gereon.stgnewsapp1.api.response;

import android.util.Log;

import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.object.Comment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentsResponse extends AbstractResponse {

    // Tag for log messages
    private static final String LOG_TAG = "CommentsResponse";


    public final List<Comment> comments = new ArrayList<>();

    public CommentsResponse(String json) {
        super(json);

        // Check for empty JSON response
        try {
            JSONArray rawComments = new JSONArray(json);
            // This loop iterates over the rawComments to parse the JSONArray into an rawComments of Comments
            for (int i = 0; i < rawComments.length(); i++) {

                // Get current Comment from Array
                JSONObject currentComment = rawComments.getJSONObject(i);

                // Get WordPress Comment ID
                int id = currentComment.getInt("id");

                // Get comment author
                String authorString = currentComment.getString("author_name");

                // Get article date
                String dateStringInput = currentComment.getString("date");
                Date articleDate;
                try {
                    articleDate = WordPressAPI.wpDateFormat.parse(dateStringInput, new ParsePosition(0));
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error parsing dateString into format: " + e.toString());
                    e.printStackTrace();
                    continue;
                }
                SimpleDateFormat outputFormatDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                SimpleDateFormat outputFormatTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String dateString = outputFormatDate.format(articleDate);
                String timeString = outputFormatTime.format(articleDate);

                JSONObject content = currentComment.getJSONObject("content");
                String contentString = content.getString("rendered");


                // After the individual parts of the Article are retrieved individually, they are parsed into an Article object and added to the rawComments
                comments.add(new Comment(id, authorString, dateString, timeString, contentString));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Problem parsing comment JSON: " + e.toString());
            e.printStackTrace();
        }
    }

}
