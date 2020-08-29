package com.elvers.gereon.stgnewsapp1.tasks;

import android.os.AsyncTask;

import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.action.ActionResultHandler;

public class APIActionTask extends AsyncTask<Object, Void, Integer> {

    // Tag for log messages
    private static final String LOG_TAG = "APIActionTask";

    private ActionResultHandler resultHandler;

    @Override
    protected Integer doInBackground(Object... args) {
        resultHandler = (ActionResultHandler) args[1];

        return WordPressAPI.doPostRequest((String) args[0]);
    }

    @Override
    protected void onPostExecute(Integer responseCode) {
        if (resultHandler != null)
            resultHandler.onActionResult(responseCode);
    }
}
