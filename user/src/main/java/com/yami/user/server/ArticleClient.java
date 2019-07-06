package com.yami.user.server;

import com.yami.common.article.ArticleClientServer;
import com.yami.common.dto.OrderDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "article",path = "/article")
public interface ArticleClient extends ArticleClientServer {
    /**
     * 根据ID查询
     * @param id ID
     * @return
     */
    @RequestMapping(value="/{id}",method= RequestMethod.GET)
    public OrderDto findById(@PathVariable("id")String id);

}
