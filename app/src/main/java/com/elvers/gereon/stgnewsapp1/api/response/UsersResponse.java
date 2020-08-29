package com.elvers.gereon.stgnewsapp1.api.response;

import androidx.annotation.Nullable;

import com.elvers.gereon.stgnewsapp1.api.object.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The AuthorResponse contains a list of {@link User} which is used f.e. to get the name behind an authorId
 */
public class UsersResponse extends AbstractResponse {

    public final List<User> users = new ArrayList<>();

    /**
     * Constructs AuthorResponse
     *
     * @param json response from server
     */
    public UsersResponse(String json) throws JSONException {
        super(json);

        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject author = array.getJSONObject(i);
            users.add(new User(
                    author.getInt("id"),
                    author.getString("name"),
                    author.getString("description"),
                    author.getString("link"),
                    author.getString("slug")
            ));
        }
    }

    @Nullable
    public User getAuthorBySlug(String slug) {
        for (User user : users) {
            if (user.getSlug().equals(slug))
                return user;
        }
        return null;
    }

}
