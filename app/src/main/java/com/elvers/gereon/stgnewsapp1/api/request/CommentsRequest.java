package com.elvers.gereon.stgnewsapp1.api.request;

import android.net.Uri;

import com.elvers.gereon.stgnewsapp1.api.response.CommentsResponse;

public class CommentsRequest extends AbstractRequest<CommentsResponse> {

    private CommentsRequestResultHandler commentsResultHandler;

    private int post;
    private Integer commentsPerPage;
    private Integer page;

    public CommentsRequest(CommentsRequestResultHandler resultHandler, int post, Integer commentsPerPage, Integer page) {
        super(CommentsResponse.class);
        this.commentsResultHandler = resultHandler;
        this.post = post;
        this.commentsPerPage = commentsPerPage;
        this.page = page;
    }

    public CommentsRequest(int post, Integer commentsPerPage, Integer page) {
        super(CommentsResponse.class);
        this.post = post;
        this.commentsPerPage = commentsPerPage;
        this.page = page;
    }

    @Override
    public void sendAsyncRequest(RequestResultHandler<CommentsResponse> resultHandler) {
        sendAsyncUrlRequest(buildURL(), resultHandler);
    }

    @Override
    public CommentsResponse sendRequest() {
        return sendUrlRequest(buildURL());
    }

    private String buildURL() {
        Uri.Builder builder = getPreparedUriBuilder(true, "comments");
        builder.appendQueryParameter("post", String.valueOf(post));
        if (commentsPerPage != null)
            builder.appendQueryParameter("per_page", String.valueOf(commentsPerPage));
        if (page != null)
            builder.appendQueryParameter("page", String.valueOf(page));
        return builder.toString();
    }

    @Override
    public void onResult(CommentsResponse response) {
        if (commentsResultHandler != null)
            commentsResultHandler.onCommentsReceived(response);
    }

    public interface CommentsRequestResultHandler {
        void onCommentsReceived(CommentsResponse response);
    }

}
