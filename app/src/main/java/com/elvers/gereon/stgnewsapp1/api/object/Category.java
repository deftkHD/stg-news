package com.elvers.gereon.stgnewsapp1.api.object;

import java.util.Arrays;

public class Category {

    public final int id;
    public final int count;
    public final String description;
    public final String link;
    public final String name;

    public Category(int id, int count, String description, String link, String name) {
        this.id = id;
        this.count = count;
        this.description = description;
        this.link = link;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[]{id});
    }
}
