package com.elvers.gereon.stgnewsapp1.api.request;

import android.net.Uri;

import com.elvers.gereon.stgnewsapp1.api.response.UsersResponse;

public class UsersRequest extends AbstractRequest<UsersResponse> {

    private AuthorsRequestResultHandler authorsResultHandler;

    private String searchFilter;
    private Integer perPage;
    private Integer page;

    public UsersRequest(AuthorsRequestResultHandler resultHandler, String searchFilter, Integer perPage, Integer page) {
        super(UsersResponse.class);
        this.authorsResultHandler = resultHandler;
        this.searchFilter = searchFilter;
        this.perPage = perPage;
        this.page = page;
    }

    public UsersRequest(String searchFilter, Integer perPage, Integer page) {
        super(UsersResponse.class);
        this.searchFilter = searchFilter;
        this.perPage = perPage;
        this.page = page;
    }

    @Override
    public void sendAsyncRequest(RequestResultHandler<UsersResponse> resultHandler) {
        sendAsyncUrlRequest(buildURL(), resultHandler);
    }

    @Override
    public UsersResponse sendRequest() {
        return sendUrlRequest(buildURL());
    }

    private String buildURL() {
        Uri.Builder builder = getPreparedUriBuilder(true, "users");
        if (searchFilter != null)
            builder.appendQueryParameter("search", searchFilter);
        if (perPage != null)
            builder.appendQueryParameter("per_page", String.valueOf(perPage));
        if (page != null)
            builder.appendQueryParameter("page", String.valueOf(page));
        return builder.toString();
    }

    @Override
    public void onResult(UsersResponse response) {
        if (authorsResultHandler != null)
            authorsResultHandler.onAuthorsReceived(response);
    }

    public interface AuthorsRequestResultHandler {
        void onAuthorsReceived(UsersResponse response);
    }

}
