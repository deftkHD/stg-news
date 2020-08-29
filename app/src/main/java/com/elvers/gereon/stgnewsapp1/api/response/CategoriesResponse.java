package com.elvers.gereon.stgnewsapp1.api.response;

import androidx.annotation.Nullable;

import com.elvers.gereon.stgnewsapp1.api.object.Category;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The CategoryResponse contains a list of {@link Category} which is used f.e. to get the name behind a categoryId
 */
public class CategoriesResponse extends AbstractResponse {

    private List<Category> categories = new ArrayList<>();

    /**
     * Constructs CategoryResponse
     *
     * @param json response from server
     * @throws JSONException if the json is not an array or not valid
     */
    public CategoriesResponse(String json) throws JSONException {
        super(json);

        JSONArray array = new JSONArray(json);

        for (int i = 0; i < array.length(); i++) {
            JSONObject category = array.getJSONObject(i);
            categories.add(new Category(
                    category.getInt("id"),
                    category.getInt("count"),
                    category.getString("description"),
                    category.getString("link"),
                    category.getString("name")
            ));
        }
    }

    public List<Category> getCategories() {
        return categories;
    }

    @Nullable
    public Category getCategoryById(int id) {
        for (Category category : categories) {
            if (category.id == id)
                return category;
        }
        return null;
    }

}
