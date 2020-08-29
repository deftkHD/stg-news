package com.elvers.gereon.stgnewsapp1.api.response;

/**
 * Abstract base class for every API response to a API request.
 * Parents of this class must have the exact same constructor in order to work correctly with the
 * API wrapper.
 * In the process of an API request, a parent of this class will be initialized and returned as
 * API response.
 */
public abstract class AbstractResponse {

    /**
     * The json response of the API
     */
    protected final String json;

    /**
     * Construct the response
     * @param json The json response of the API
     */
    public AbstractResponse(String json) {
        this.json = json;
    }

}
