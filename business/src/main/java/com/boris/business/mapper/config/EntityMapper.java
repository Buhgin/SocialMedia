package com.boris.business.mapper.config;

import java.util.Set;

/**
 * The interface that defines basic methods for mapping dto to entity. Object types are generic and
 * must be specified in parameters of class
 *
 *  @param <T> entity class to map
 *  @param <S> dto class from which to map
 */
public interface EntityMapper<T, S> {

    /**
     * This method maps passed as argument dto object to an entity.
     *
     * @param dto must not be null
     * @return mapped entity
     */
    T toEntity(S dto);

    /**
     * This method maps passed as argument set of dtos to set of entities.
     *
     * @param dtoSet must not be null
     * @return mapped set of entities
     */
    Set<T> toEntitySet(Set<S> dtoSet);
}
