package com.boris.business.mapper.request;

import com.boris.business.mapper.config.EntityMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.request.TokenCreateRequest;
import com.boris.dao.entity.Token;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapstructConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TokenCreateMapper extends EntityMapper<Token, TokenCreateRequest> {
    @Override
    Token toEntity(TokenCreateRequest dto);
}
