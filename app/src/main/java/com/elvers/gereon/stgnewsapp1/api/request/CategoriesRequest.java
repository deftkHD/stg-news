package com.elvers.gereon.stgnewsapp1.api.request;

import android.net.Uri;

import com.elvers.gereon.stgnewsapp1.api.response.CategoriesResponse;

public class CategoriesRequest extends AbstractRequest<CategoriesResponse> {

    private CategoriesRequestResultHandler categoriesResultHandler;

    public CategoriesRequest(CategoriesRequestResultHandler categoriesResultHandler) {
        super(CategoriesResponse.class);
        this.categoriesResultHandler = categoriesResultHandler;
    }

    public CategoriesRequest() {
        super(CategoriesResponse.class);
    }

    @Override
    public void sendAsyncRequest(RequestResultHandler<CategoriesResponse> resultHandler) {
        sendAsyncUrlRequest(buildURL(), resultHandler);
    }

    @Override
    public CategoriesResponse sendRequest() {
        return sendUrlRequest(buildURL());
    }

    private String buildURL() {
        Uri.Builder builder = getPreparedUriBuilder(false, "categories");
        builder.appendQueryParameter("per_page", "100");
        return builder.toString();
    }

    @Override
    public void onResult(CategoriesResponse response) {
        if (categoriesResultHandler != null)
            categoriesResultHandler.onCategoriesReceived(response);
    }

    public interface CategoriesRequestResultHandler {
        void onCategoriesReceived(CategoriesResponse response);
    }

}
