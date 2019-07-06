package com.yami.order.controller;

import com.alibaba.fastjson.JSON;
import com.yami.common.article.ArticleClientServer;
import com.yami.common.dto.OrderDto;
import com.yami.order.dto.Order;
import com.yami.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController implements ArticleClientServer {

	@Autowired
	private OrderService articleService;

	@Autowired
	private JmsTemplate jmsTemplate;





	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public List<Order> findAll(){
		return articleService.findAll();
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public OrderDto findById(@PathVariable String id){
		Order article = articleService.findById(id);
		OrderDto articleDto = JSON.parseObject(JSON.toJSONString(article),OrderDto.class);
		return articleDto;
	}

	/**
	 * 添加订单
	 * @param order
	 */
	@RequestMapping(value = "/creat",method = RequestMethod.POST)
	public void add(@RequestBody OrderDto order  ){
		UUID uuid = UUID.randomUUID();
		order.setUuid(uuid.toString());
		jmsTemplate.convertAndSend("order:new",order);
	}

	
}
