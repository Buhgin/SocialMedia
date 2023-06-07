package com.boris.business.model.response;

import com.boris.business.model.dto.PostDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PostResponse {
    private List<PostDto> content;
    private int pageNo;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private boolean isLast;
}
