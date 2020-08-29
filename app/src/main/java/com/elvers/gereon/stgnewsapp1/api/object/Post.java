package com.elvers.gereon.stgnewsapp1.api.object;

import android.text.Html;

import java.util.Arrays;

/**
 * An {@link Post} object contains information related to a single Post.
 *
 * @author Gereon Elvers
 */
public class Post {

    private final int mId;
    private final String mTitle;
    private final String mTitleHtmlEscaped;
    private final String mAuthor;
    private final String mDate;
    private final String mUrl;
    private final String mCoverImage;
    private final String mCategory;

    /**
     * Constructs a new {@link Post} object.
     *
     * @param id               is the WordPress-ID of the article
     * @param titleHtmlEscaped is the title of the article, but it might contain f.e. &#8211;
     *                         instead of -
     * @param author           is the author of the article
     * @param date             is the publication date of the article
     * @param url              is the website URL of the article
     * @param coverImage       is the image URL of the cover image (if present)
     * @param category         contains the first three categories of the article (concatenated a
     *                         s String; ", ..." added if more than three categories are present)
     */
    public Post(int id, String titleHtmlEscaped, String author, String date, String url, String coverImage, String category) {
        mId = id;
        mTitleHtmlEscaped = titleHtmlEscaped;
        mTitle = Html.fromHtml(mTitleHtmlEscaped).toString();
        mAuthor = author;
        mDate = date;
        mUrl = url;
        mCoverImage = coverImage;
        mCategory = category;
    }

    /**
     * The following methods return the individual components of the Post (think of the Post object
     * as a container containing its properties in form of other objects)
     */

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getTitleHtmlEscaped() {
        return mTitleHtmlEscaped;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getCoverImage() {
        return mCoverImage;
    }

    public String getCategory() {
        return mCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return mId == post.mId;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{mId});
    }
}
