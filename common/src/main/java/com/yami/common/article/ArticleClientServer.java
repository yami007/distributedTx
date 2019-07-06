package com.yami.common.article;

import com.yami.common.dto.OrderDto;

public interface ArticleClientServer {
    public OrderDto findById(String id);
}
