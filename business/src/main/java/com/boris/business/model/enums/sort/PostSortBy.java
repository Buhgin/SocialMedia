package com.boris.business.model.enums.sort;

public enum PostSortBy {
    ID("id"),
    CONTENT("content"),
    DESCRIPTION("description"),
    TITLE("title");

    private final String attribute;

    PostSortBy(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}
