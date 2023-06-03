package com.boris.business.mapper.config;

import java.util.List;
import java.util.Set;

/**
 * The interface that defines basic methods for mapping entity to dto. Object types are generic and
 * must be specified in parameters of class
 *
 * @param <S> dto class to map
 * @param <T> entity class from which to map
 */
public interface DtoMapper<S, T> {

    /**
     * This method maps passed as argument entity object to a dto.
     *
     * @param entity must not be null
     * @return mapped dto
     */
    S toDto(T entity);

    /**
     * This method maps passed as argument set of entities to set of dtos.
     *
     * @param entitySet must not be null
     * @return mapped set of dtos
     */
    Set<S> toDtoSet(Set<T> entitySet);

    /**
     * This method maps passed as argument list of entities to list of dtos.
     *
     * @param entityList must not be null
     * @return mapped set of dtos
     */
    List<S> toDtoList(List<T> entityList);
}
