package com.yami.user.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.yami.common.dto.OrderDto;
import com.yami.user.dto.User;
import com.yami.user.server.ArticleClient;
import com.yami.user.server.UserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserServer userServer;
    @Autowired
    private ArticleClient articleClient;

    @RequestMapping("/save")
    public String save(@RequestBody User user){
        userServer.saveUser(user);
        return "ok";
    }
    @RequestMapping("/all")
    @HystrixCommand
    public List<User> getUserAll(){
        List<User> all = userServer.getUserAll();
        return all;
    }
    @RequestMapping("/getArticle")
    public OrderDto getArticle(@RequestParam String id){
        OrderDto article =  articleClient.findById(id);
        return article;
    }


}
