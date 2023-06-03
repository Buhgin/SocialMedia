package com.boris.business.model.enums.sort;

import org.springframework.data.domain.Sort.Direction;

public enum SortType {

    ASC(Direction.ASC),
    DESC(Direction.DESC);

    private final Direction direction;

    SortType(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
