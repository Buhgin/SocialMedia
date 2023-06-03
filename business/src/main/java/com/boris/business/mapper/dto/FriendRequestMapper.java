package com.boris.business.mapper.dto;

import com.boris.business.mapper.config.DtoMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.dto.FriendRequestDto;
import com.boris.dao.entity.FriendRequest;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface FriendRequestMapper extends DtoMapper<FriendRequestDto, FriendRequest> {
}
