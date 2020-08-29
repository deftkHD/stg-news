package com.elvers.gereon.stgnewsapp1.api.action;

import android.net.Uri;

import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.tasks.APIActionTask;

/**
 * Abstract base class for every API action. An API action is send to the server as a post request
 */
public abstract class AbstractAction implements ActionResultHandler {

    /**
     * Start sending an asynchronous (post) request to the API. Its possible to use the default
     * implementation {@link #sendAsyncUrlPostRequest(String, ActionResultHandler)} if you don't
     * need to pass extra header fields or do something similar.
     *
     * @param resultHandler The resultHandler is called after the asynchronous process has finished
     */
    public abstract void sendAsyncRequest(ActionResultHandler resultHandler);

    /**
     * See {@link #sendAsyncRequest(ActionResultHandler)}, but the action class itself is expected
     * to handle the result of the API response. In order to do that you should override
     * {@link #onActionResult(Integer)}.
     */
    public void sendAsyncRequest() {
        sendAsyncRequest(this);
    }

    /**
     * Send a synchronous (post) request to the API. Its possible to use the default implementation
     * {@link #sendUrlPostRequest(String)} if you don't need to pass extra header fields or do
     * something similar.
     *
     * @return the HTTP response code
     */
    public abstract int sendRequest();

    /**
     * Default implementation for sending an asynchronous post request to the API
     *
     * @param url endpoint url and its url encoded parameters
     * @param resultHandler resultHandler is called after the asynchronous process has finished
     */
    protected void sendAsyncUrlPostRequest(String url, ActionResultHandler resultHandler) {
        new APIActionTask().execute(url, resultHandler);
    }

    /**
     * Default implementation for sending a synchronous post request to the API
     *
     * @param url endpoint url and its url encoded parameters
     * @return the HTTP response code
     */
    protected int sendUrlPostRequest(String url) {
        return WordPressAPI.doPostRequest(url);
    }

    /**
     * Prepare a simple {@link Uri.Builder} which is able to perform an ordinary API call
     *
     * @param https weather https (secure) or http should be used
     * @param endpoint the endpoint to perform the request to; see WordPress documentation for more information
     * @return the prepared {@link Uri.Builder}
     */
    protected Uri.Builder getPreparedUriBuilder(boolean https, String endpoint) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(https ? "https" : "http");
        builder.authority(WordPressAPI.BASE_REQUEST_URL);
        builder.appendPath("wp-json").appendPath("wp").appendPath("v2");
        builder.appendPath(endpoint);
        return builder;
    }

    /**
     * See {@link #sendAsyncRequest()}
     */
    @Override
    public void onActionResult(Integer responseCode) {
    }
}
