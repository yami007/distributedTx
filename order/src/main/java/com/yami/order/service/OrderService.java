package com.yami.order.service;

import com.yami.common.dto.OrderDto;
import com.yami.order.dao.OrderDao;
import com.yami.order.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 服务层
 *
 * @author Administrator
 */
@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 创建订单
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:locked",containerFactory = "msqFactory")
    public void handleOrderNew(OrderDto orderDto){
        if(orderDao.findOneByUuid(orderDto.getUuid())!=null){
            logger.info("订单已经创建 :{}",orderDto.getTitle());
        }else {
            logger.info("订单创建 :{}",orderDto.getTitle());
            Order order = creatOrder(orderDto);
            Order save = orderDao.save(order);
            orderDto.setId(order.getId());
        }
        orderDto.setStatus("NEW");
        jmsTemplate.convertAndSend("order:pay",orderDto);
    }

    /**
     * 完成订单
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:locker",containerFactory = "msqFactory")
    public void handleOrderFinish(OrderDto orderDto){
        logger.info("完成订单：{}",orderDto);
        Order order = orderDao.findOneByUuid(orderDto.getUuid());
        order.setStatus("FINISH");
        orderDao.save(order);
    }

    public Order creatOrder(OrderDto orderDto){
        Order order = new Order();
        order.setUuid(orderDto.getUuid());
        order.setAmount(orderDto.getAmount());
        order.setTitle(orderDto.getTitle());
        order.setCustomerId(orderDto.getCustomerId());
        order.setTicketNum(orderDto.getTicketNum());
        order.setStatus(orderDto.getStatus());
        order.setCreatDate(new Date());
        return order;
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Order> findAll() {
        return orderDao.findAll();
    }


    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderDao.findOne(id);
    }

    /**
     * 增加
     *
     * @param order
     */
    public void add(Order order) {
        orderDao.save(order);
    }

    /**
     * 修改
     *
     * @param order
     */
    public void update(Order order) {
        orderDao.save(order);
    }

}