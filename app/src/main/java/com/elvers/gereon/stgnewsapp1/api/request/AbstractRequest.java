package com.elvers.gereon.stgnewsapp1.api.request;

import android.net.Uri;

import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.response.AbstractResponse;
import com.elvers.gereon.stgnewsapp1.tasks.APIRequestTask;

/**
 * Abstract base class for every API request. An API request is send to the server as a get request
 */
public abstract class AbstractRequest<T extends AbstractResponse> implements RequestResultHandler<T> {

    /**
     * See {@link AbstractResponse}
     */
    private Class<T> responseClass;

    public AbstractRequest(Class<T> responseClass) {
        this.responseClass = responseClass;
    }

    /**
     * Start sending an asynchronous (get) request to the API. Its possible to use the default
     * implementation {@link #sendAsyncUrlRequest(String, RequestResultHandler)}} if you don't
     * need to pass extra header fields or do something similar.
     *
     * @param resultHandler The resultHandler is called after the asynchronous process has finished
     */
    public abstract void sendAsyncRequest(RequestResultHandler<T> resultHandler);

    /**
     * See {@link #sendAsyncRequest(RequestResultHandler)}, but the request class itself is expected
     * to handle the result of the API response. In order to do that you should override
     * {@link #onResult(AbstractResponse)}.
     */
    public void sendAsyncRequest() {
        sendAsyncRequest(this);
    }

    /**
     * Send a synchronous (get) request to the API. Its possible to use the default implementation
     * {@link #sendUrlRequest(String)} if you don't need to pass extra header fields or do
     * something similar.
     *
     * @return the API response
     */
    public abstract T sendRequest();

    /**
     * Default implementation for sending an asynchronous get request to the API
     *
     * @param url endpoint url and its url encoded parameters
     * @param resultHandler resultHandler is called after the asynchronous process has finished
     */
    protected void sendAsyncUrlRequest(String url, RequestResultHandler<T> resultHandler) {
        RequestResultHandler<?> handler;
        if (resultHandler == null) {
            handler = this;
        } else {
            handler = resultHandler;
        }
        new APIRequestTask().execute(url, responseClass, handler);
    }

    /**
     * Default implementation for sending a synchronous get request to the API
     *
     * @param url endpoint url and its url encoded parameters
     * @return the HTTP response code
     */
    protected T sendUrlRequest(String url) {
        return WordPressAPI.doGetRequest(url, responseClass);
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

    @Override
    public void onResult(T response) {
    }

}
