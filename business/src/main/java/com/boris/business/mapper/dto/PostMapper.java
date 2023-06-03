package com.boris.business.mapper.dto;

import com.boris.business.mapper.config.DtoMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.dto.PostDto;
import com.boris.dao.entity.Post;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface PostMapper extends DtoMapper<PostDto, Post> {
}
