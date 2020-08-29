package com.elvers.gereon.stgnewsapp1.api.action;

import android.net.Uri;

import androidx.annotation.NonNull;

public class PostCommentAction extends AbstractAction {

    private final int postId;
    private final Integer authorId;
    private final String authorName;
    private final String authorEmail;
    private final String content;

    public PostCommentAction(int postId, int authorId, @NonNull String content) {
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = null;
        this.authorEmail = null;
        this.content = content;
    }

    public PostCommentAction(int postId, @NonNull String authorName, @NonNull String authorEmail, @NonNull String content) {
        this.postId = postId;
        this.authorId = null;
        this.authorName = authorName;
        this.authorEmail = authorEmail;
        this.content = content;
    }

    @Override
    public void sendAsyncRequest(ActionResultHandler resultHandler) {
        sendAsyncUrlPostRequest(buildURL(), resultHandler);
    }

    @Override
    public int sendRequest() {
        return sendUrlPostRequest(buildURL());
    }

    private String buildURL() {
        Uri.Builder builder = getPreparedUriBuilder(true, "comments");
        builder.appendQueryParameter("post", String.valueOf(postId));
        if (authorId != null) {
            builder.appendQueryParameter("author", String.valueOf(authorId));
        } else {
            builder.appendQueryParameter("author_email", authorEmail);
            builder.appendQueryParameter("author_name", authorName);
        }
        builder.appendQueryParameter("content", content);
        return builder.toString();
    }

}
