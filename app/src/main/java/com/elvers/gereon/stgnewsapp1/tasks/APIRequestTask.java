package com.elvers.gereon.stgnewsapp1.tasks;

import android.os.AsyncTask;

import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.request.RequestResultHandler;
import com.elvers.gereon.stgnewsapp1.api.response.AbstractResponse;

public class APIRequestTask extends AsyncTask<Object, Void, AbstractResponse> {

    private RequestResultHandler<AbstractResponse> resultHandler;

    @Override
    protected AbstractResponse doInBackground(Object... args) {
        // check if arguments are valid
        if (args.length != 3)
            throw new IllegalArgumentException("Expected only one request and one request handler");
        if (!(args[0] instanceof String))
            throw new IllegalArgumentException("Expected url of type String");
        if (!(args[1] instanceof Class<?>))
            throw new IllegalArgumentException("Expected response class of type AbstractResponse");
        if (!(args[2] instanceof RequestResultHandler<?>))
            throw new IllegalArgumentException("Expected request handler of type RequestResultHandler");

        // parse arguments
        this.resultHandler = (RequestResultHandler<AbstractResponse>) args[2];
        Class<AbstractResponse> responseClass = (Class<AbstractResponse>) args[1];

        // perform api call
        return WordPressAPI.doGetRequest(args[0].toString(), responseClass);
    }

    @Override
    protected void onPostExecute(AbstractResponse response) {
        // notify handler task was successfully completed
        if (resultHandler != null)
            resultHandler.onResult(response);
    }

}
