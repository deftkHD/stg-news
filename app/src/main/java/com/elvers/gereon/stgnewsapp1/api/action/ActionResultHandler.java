package com.elvers.gereon.stgnewsapp1.api.action;

/**
 * The ActionResultHandler is called after an asynchronous API action has finished
 */
public interface ActionResultHandler {

    /**
     * Called after an asynchronous API action has finished
     * @param responseCode the HTTP response code
     */
    void onActionResult(Integer responseCode);

}
