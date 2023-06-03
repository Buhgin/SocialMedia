package com.boris.business.model.enums.sort;

public enum ActivitySort {
    ID("id"),
    POST("POST"),
    FRIEND_REQUEST("friendRequest"),
    CREATED_AT("createdAt");

    private final String attribute;

    ActivitySort(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }
}
