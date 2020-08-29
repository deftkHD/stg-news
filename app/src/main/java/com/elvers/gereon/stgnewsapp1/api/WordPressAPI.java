package com.elvers.gereon.stgnewsapp1.api;

import android.util.Log;

import androidx.annotation.Nullable;

import com.elvers.gereon.stgnewsapp1.api.action.ActionResultHandler;
import com.elvers.gereon.stgnewsapp1.api.action.PostCommentAction;
import com.elvers.gereon.stgnewsapp1.api.object.Comment;
import com.elvers.gereon.stgnewsapp1.api.object.Post;
import com.elvers.gereon.stgnewsapp1.api.object.User;
import com.elvers.gereon.stgnewsapp1.api.object.Category;
import com.elvers.gereon.stgnewsapp1.api.request.PostsRequest;
import com.elvers.gereon.stgnewsapp1.api.request.UsersRequest;
import com.elvers.gereon.stgnewsapp1.api.request.CategoriesRequest;
import com.elvers.gereon.stgnewsapp1.api.request.CommentsRequest;
import com.elvers.gereon.stgnewsapp1.api.response.AbstractResponse;
import com.elvers.gereon.stgnewsapp1.api.response.UsersResponse;
import com.elvers.gereon.stgnewsapp1.api.response.CategoriesResponse;
import com.elvers.gereon.stgnewsapp1.utils.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WordPressAPI {

    // Tag for log messages
    private static final String LOG_TAG = "WordPressAPI";

    // DateFormat used in server responses
    public static final SimpleDateFormat wpDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);

    // Cached API responses to increase performance
    private static CategoriesResponse latestCategoriesResponse = null;
    private static UsersResponse latestUsersResponse = null;

    // Static request URL for every request to our server
    public static final String BASE_REQUEST_URL = "stg-sz.net";

    /**
     * Create a private WordPressAPI constructor to prevent creation of a WordPressAPI object.
     * This class is only meant to hold static variables and methods, it should not be treated as
     * an object!
     */
    private WordPressAPI() {
    }

    /**
     * Query the WordPress site for list of {@link Post} (asynchronous)
     *
     * @param resultHandler called after the asynchronous process has finished
     * @param categoryFilter only include posts with matching category (id)
     * @param searchFilter only include posts matching the search term
     * @param authorFilter only include posts created by the author (id)
     * @param postsPerPage posts per page (or rather request), maximum 99
     * @param whitelist specific post (ids) to be included inside the response
     * @param pageNumber the current page number
     */
    public static void requestPosts(PostsRequest.ArticlesRequestResultHandler resultHandler, String categoryFilter, String searchFilter, String authorFilter, Integer postsPerPage, String[] whitelist, int pageNumber) {
        if (categoryFilter != null && categoryFilter.isEmpty())
            categoryFilter = null;
        if (searchFilter != null && searchFilter.isEmpty())
            searchFilter = null;
        if (authorFilter != null && authorFilter.isEmpty())
            authorFilter = null;
        new PostsRequest(resultHandler, categoryFilter, searchFilter, authorFilter, postsPerPage, whitelist, pageNumber).sendAsyncRequest();
    }

    /**
     * Query the WordPress site for list of {@link Comment} (asynchronous)
     *
     * @param resultHandler called after the asynchronous process has finished
     * @param postId id of the post to query comments for
     * @param commentsPerPage comments per page (or rather request), maximum 99
     * @param page the current page number
     */
    public static void requestComments(CommentsRequest.CommentsRequestResultHandler resultHandler, int postId, Integer commentsPerPage, Integer page) {
        new CommentsRequest(resultHandler, postId, commentsPerPage, page).sendAsyncRequest();
    }

    /**
     * Tell the API to create a new {@link Comment} (asynchronous)
     *
     * @param resultHandler called after the asynchronous process has finished
     * @param postId id of the post to create the comment for
     * @param authorName name of the author of the post
     * @param authorEmail email of the author of the post
     * @param content content/text of the post
     */
    public static void sendComment(ActionResultHandler resultHandler, int postId, String authorName, String authorEmail, String content) {
        new PostCommentAction(postId, authorName, authorEmail, content).sendAsyncRequest(resultHandler);
    }

    /**
     * Since the WordPress API only provides user id (not the complete name), the process to
     * retrieve names is a little more intricate, which is why it's moved into a separate method.
     *
     * To get he user name, an array of users and their respective IDs is requested from the
     * backend. Since this process is resource intensive, it should not be performed too often.
     * The user array is then stored as {@link UsersResponse}.
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     *
     * @param userID The ID of the user to find the real name for
     * @return The name of the user or an empty string if no name was found
     */
    public static String getUserName(int userID) {
        // Fill usersArray with users (if it's empty)
        if (latestUsersResponse == null) {
            updateUsers();
        }

        // Iterate over usersResponse until the user ID matches the ID of the user in usersResponse
        for (int i = 0; i < latestUsersResponse.users.size(); i++) {
            User user = latestUsersResponse.users.get(i);
            if (user.getId() == userID) {
                return user.getName();
            }
        }

        return "";
    }

    /**
     * Query the WordPress site for list of {@link User} (asynchronous)
     *
     * @param resultHandler called after the asynchronous process has finished
     * @param searchFilter only include users matching the search term
     * @param usersPerPage users per page (or rather request), maximum 99
     * @param page the current page number
     */
    public static void requestUsers(final UsersRequest.AuthorsRequestResultHandler resultHandler, String searchFilter, Integer usersPerPage, Integer page) {
        if (searchFilter != null && searchFilter.isEmpty())
            searchFilter = null;
        new UsersRequest(new UsersRequest.AuthorsRequestResultHandler() {
            @Override
            public void onAuthorsReceived(UsersResponse response) {
                WordPressAPI.latestUsersResponse = response;
                resultHandler.onAuthorsReceived(response);
            }
        }, searchFilter, usersPerPage, page).sendAsyncRequest();
    }

    /**
     * Query the WordPress site for list of {@link User} (synchronous).
     * The response will be cached inside {@link #latestUsersResponse}.
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     */
    private static void updateUsers() {
        latestUsersResponse = new UsersRequest(null, 100, null).sendRequest();
    }

    /**
     * Query the WordPress site for list of {@link Category} (asynchronous)
     *
     * @param resultHandler called after the asynchronous process has finished
     */
    public static void requestCategories(final CategoriesRequest.CategoriesRequestResultHandler resultHandler) {
        new CategoriesRequest(new CategoriesRequest.CategoriesRequestResultHandler() {
            @Override
            public void onCategoriesReceived(CategoriesResponse response) {
                WordPressAPI.latestCategoriesResponse = response;
                resultHandler.onCategoriesReceived(response);
            }
        }).sendAsyncRequest();
    }

    /**
     * Query the WordPress site for list of {@link Category} (synchronous).
     * The response will be cached inside {@link #latestCategoriesResponse}.
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     */
    public static void updateCategories() {
        try {
            latestCategoriesResponse = new CategoriesRequest().sendRequest();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to get categories from server: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Returns all categories known to the WordPress API.
     *
     * @return List of {@link Category} (max 99 elements) or null if no categories were cached and
     * web requests are not allowed
     */
    @Nullable
    public static List<Category> getCategories(boolean allowWebRequests) {
        if (latestCategoriesResponse == null) {
            if (allowWebRequests) {
                updateCategories();
            } else {
                return null;
            }
        }

        if (latestCategoriesResponse == null)
            return null;
        return latestCategoriesResponse.getCategories();
    }

    /**
     * @return True if some categories were cached already
     */
    public static boolean hasCachedCategories() {
        return latestCategoriesResponse != null;
    }

    /**
     * Tries to find the {@link Category} object belonging to the given id
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     *
     * @param id id of the category to be found
     * @return {@link Category} object belonging to the given id or null if none was found
     */
    @Nullable
    public static Category getCategoryById(int id) {
        if (latestCategoriesResponse == null)
            updateCategories();
        return latestCategoriesResponse.getCategoryById(id);
    }

    /**
     * Tries to find the {@link User} object belonging to the given id
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     *
     * @param slug slug of the user to be found
     * @return {@link User} object belonging to the given id or null if none was found
     */
    @Nullable
    public static User getUserBySlug(String slug) {
        if (latestUsersResponse == null)
            updateUsers();
        return latestUsersResponse.getAuthorBySlug(slug);
    }

    /**
     * Perform a GET request to the given url and parses the (json) response into the given
     * response class.
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     *
     * @param requestUrl URL to perform GET request on
     * @param responseClass Class to handle and parse response
     * @param <T> Type of the response class
     * @return Instance of the given response class
     */
    @Nullable
    public static <T extends AbstractResponse> T doGetRequest(String requestUrl, Class<T> responseClass) {
        // Create URL object
        URL url = Utils.createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = Utils.makeHttpGetRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request: " + e.toString());
            e.printStackTrace();
        }

        if (jsonResponse.isEmpty())
            return null;

        if (responseClass == null)
            return null;

        // Instantiate given response class with the raw response data. Parsing is not my problem
        // anymore
        try {
            return responseClass.getConstructor(String.class).newInstance(jsonResponse);
        } catch (IllegalAccessException e) {
            Log.e(LOG_TAG, "Constructor must be public");
            e.printStackTrace();
        } catch (InstantiationException e) {
            Log.e(LOG_TAG, "Failed to instantiate response");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, "Expected constructor with one parameter (String)");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Perform a POST request to the given url
     *
     * This method should not be called from the ui thread, because it could perform a web request,
     * which might block the ui thread or rather throw an exception and fail.
     *
     * @param requestUrl URL to perform POST request on
     * @return HTTP response code
     */
    public static Integer doPostRequest(String requestUrl) {
        // Create URL object
        URL url = Utils.createUrl(requestUrl);

        try {
            return Utils.makeHttpPostRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to perform API post request");
            e.printStackTrace();
            return -1;
        }
    }

}
