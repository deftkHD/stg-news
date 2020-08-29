package com.elvers.gereon.stgnewsapp1.api.request;

import android.net.Uri;

import com.elvers.gereon.stgnewsapp1.api.response.PostsResponse;

public class PostsRequest extends AbstractRequest<PostsResponse> {

    private ArticlesRequestResultHandler articlesResultHandler;

    private String categoryFilter;
    private String searchFilter;
    private String authorFilter;
    private Integer postsPerPage;
    private String[] whitelist;
    private Integer page;

    public PostsRequest(ArticlesRequestResultHandler resultHandler, String categoryFilter, String searchFilter, String authorFilter, Integer postsPerPage, String[] whitelist, Integer page) {
        super(PostsResponse.class);
        this.articlesResultHandler = resultHandler;
        this.categoryFilter = categoryFilter;
        this.searchFilter = searchFilter;
        this.authorFilter = authorFilter;
        this.postsPerPage = postsPerPage;
        this.whitelist = whitelist;
        this.page = page;
    }

    public PostsRequest(String categoryFilter, String searchFilter, String authorFilter, Integer postsPerPage, String[] whitelist, Integer page) {
        super(PostsResponse.class);
        this.categoryFilter = categoryFilter;
        this.searchFilter = searchFilter;
        this.authorFilter = authorFilter;
        this.postsPerPage = postsPerPage;
        this.whitelist = whitelist;
        this.page = page;
    }

    @Override
    public void sendAsyncRequest(RequestResultHandler<PostsResponse> resultHandler) {
        sendAsyncUrlRequest(buildURL(), resultHandler);
    }

    @Override
    public PostsResponse sendRequest() {
        return sendUrlRequest(buildURL());
    }

    private String buildURL() {
        // create builder with some predefined fields
        Uri.Builder builder = getPreparedUriBuilder(true, "posts");

        // add custom query parameters
        if (categoryFilter != null)
            builder.appendQueryParameter("categories", categoryFilter);
        if (searchFilter != null)
            builder.appendQueryParameter("search", searchFilter);
        if (authorFilter != null)
            builder.appendQueryParameter("author", authorFilter);
        if (postsPerPage != null)
            builder.appendQueryParameter("per_page", String.valueOf(postsPerPage));
        if (whitelist != null) {
            for (String entry : whitelist) {
                builder.appendQueryParameter("include[]", entry);
            }
        }
        builder.appendQueryParameter("page", String.valueOf(page));
        return builder.toString();
    }

    @Override
    public void onResult(PostsResponse response) {
        if (articlesResultHandler != null)
            articlesResultHandler.onArticlesReceived(response);
    }

    public interface ArticlesRequestResultHandler {
        void onArticlesReceived(PostsResponse response);
    }

}
