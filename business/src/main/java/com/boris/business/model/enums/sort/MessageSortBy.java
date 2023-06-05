package com.boris.business.model.enums.sort;

public enum MessageSortBy {
    CREATED_AT("createdAt");
    private final String attribute;

      MessageSortBy(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}
