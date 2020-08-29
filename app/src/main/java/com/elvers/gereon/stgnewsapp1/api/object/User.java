package com.elvers.gereon.stgnewsapp1.api.object;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class User {

    private final int id;
    private final String name;
    private final String description;
    private final String link;
    private final String slug;

    public User(int id, String name, String description, String link, String slug) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.slug = slug;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getSlug() {
        return slug;
    }

    @NonNull
    @Override
    public String toString() {
        return name + "@" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{id});
    }
}
