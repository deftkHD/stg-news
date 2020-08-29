package com.elvers.gereon.stgnewsapp1.api.object;


import java.util.Arrays;

public class Comment {

    private final int mId;
    private final String mAuthor;
    private final String mDate;
    private final String mTime;
    private final String mContent;

    /**
     * Constructs a new {@link Comment} object.
     *
     * @param id      is the WordPress assigned comment ID of the comment
     * @param author  is the author of the Comment
     * @param date    is the publication date of the Comment
     * @param content is the content of the Comment as String
     */
    public Comment(int id, String author, String date, String time, String content) {
        mId = id;
        mAuthor = author;
        mDate = date;
        mTime = time;
        mContent = content;
    }

    /**
     * The following methods return the individual components of the Comment
     * (think of the Comment object as a container containing the other objects)
     */

    public int getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getContent() {
        return mContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return mId == comment.mId;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{mId});
    }
}
