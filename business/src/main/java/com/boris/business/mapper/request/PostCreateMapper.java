package com.boris.business.mapper.request;

import com.boris.business.mapper.config.EntityMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.request.PostCreateRequest;
import com.boris.dao.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapstructConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostCreateMapper extends EntityMapper<Post, PostCreateRequest> {
    @Override
    Post toEntity(PostCreateRequest dto);
}
