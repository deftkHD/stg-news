package com.elvers.gereon.stgnewsapp1.api;

import android.text.Html;

/**
 * An {@link Article} object contains information related to a single Article.
 *
 * @author Gereon Elvers
 */
public class Article extends ListEntry {
    private int mId;
    private String mTitle;
    private String mTitleHtmlEscaped;
    private String mAuthor;
    private String mDate;
    private String mUrl;
    private String mCoverImage;
    private String mCategory;

    /**
     * Constructs a new {@link Article} object.
     *
     * @param id                is the WordPress-ID of the article
     * @param titleHtmlEscaped  is the title of the article, but it might contain f.e. &#8211; instead of -
     * @param author            is the author of the article
     * @param date              is the publication date of the article
     * @param url               is the website URL of the article
     * @param coverImage        is the image URL of the cover image (if present)
     * @param category          contains the first three categories of the article (concatenated as String; ", ..." added if more than three categories are present)
     */
    public Article(int id, String titleHtmlEscaped, String author, String date, String url, String coverImage, String category) {
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
     * The following methods return the individual components of the Article (think of the Article object as a container containing the other objects)
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


}
