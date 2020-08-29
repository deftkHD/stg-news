package com.elvers.gereon.stgnewsapp1.api.request;

import androidx.annotation.Nullable;

import com.elvers.gereon.stgnewsapp1.api.response.AbstractResponse;

/**
 * The RequestResultHandler is called after an asynchronous API call has finished
 *
 * @param <T> The type of the response to expect
 */
public interface RequestResultHandler<T extends AbstractResponse> {

    /**
     * Called after an asynchronous API call has finished
     *
     * @param response The response of the API or null if the request failed due to internet
     *                 connection issues or similar
     */
    void onResult(@Nullable T response);

}