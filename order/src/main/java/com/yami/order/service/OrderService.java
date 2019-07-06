package com.yami.order.service;

import com.yami.common.dto.OrderDto;
import com.yami.order.dao.OrderDao;
import com.yami.order.dto.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
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
     *
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:locked", containerFactory = "msqFactory")
    public void handleOrderNew(OrderDto orderDto) {
        // 如果通过uuid查询不到订单，说明该订单还未创建
        if (orderDao.findOneByUuid(orderDto.getUuid()) != null) {
            logger.info("订单已经创建 :{}", orderDto.getTitle());
        } else {
            logger.info("订单创建 :{}", orderDto.getTitle());
            Order order = creatOrder(orderDto);
            Order save = orderDao.save(order);
            orderDto.setId(order.getId());
        }
        orderDto.setStatus("NEW");
        jmsTemplate.convertAndSend("order:pay", orderDto);
    }

    /**
     * 完成订单
     *
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:finish", containerFactory = "msqFactory")
    public void handleOrderFinish(OrderDto orderDto) {
        logger.info("完成订单：{}", orderDto);
        Order order = orderDao.findOneByUuid(orderDto.getUuid());
        order.setStatus("FINISH");
        orderDao.save(order);
    }

    /**
     * 订票失败
     *
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:fail", containerFactory = "msqFactory")
    public void handleOrderFail(OrderDto orderDto) {
        logger.info("订单失败处理开始：{}",orderDto.getTitle());
        Order order = null;
        if (orderDto.getId() == null) {
            // 订单锁票失败的订单异常处理
            order = creatOrder(orderDto);
            order.setReason("TICKET_LOCK_FAIL");
        } else {
            order = orderDao.findOneById(orderDto.getId());
            if (orderDto.getStatus().equals("NOT_ENOUGH_DEPOSIT")) {
                // 订单扣费失败的订单异常处理
                order.setReason("NOT_ENOUGH_DEPOSIT");
            }else if (orderDto.getStatus().equals("TIMEOUT")){
                // 订单超时的异常
                order.setReason("TIMEOUT");
            }
        }
        logger.info("订单失败处理成功：订单{}，原因{}",orderDto.getTitle(),orderDto.getReason());
        order.setStatus("FAIL");
        orderDao.save(order);
    }

    public Order creatOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setUuid(orderDto.getUuid());
        order.setAmount(orderDto.getAmount());
        order.setTitle(orderDto.getTitle());
        order.setCustomerId(orderDto.getCustomerId());
        order.setTicketNum(orderDto.getTicketNum());
        order.setStatus(orderDto.getStatus());
        order.setCreatDate(ZonedDateTime.now());
        return order;
    }

    /**
     * 定时任务扫描订单超时失败的订单
     */
    @Scheduled(fixedDelay = 10000L)
    public void checkTimeOutOrders(){
        ZonedDateTime zonedDateTime = ZonedDateTime.now().minusMinutes(1L);
        List<Order> orders = orderDao.findAllByStatusAndCreatDateBefore("NEW",zonedDateTime);
        for (Order order : orders) {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setTicketNum(order.getTicketNum());
            orderDto.setUuid(order.getUuid());
            orderDto.setAmount(order.getAmount());
            orderDto.setTitle(order.getTitle());
            orderDto.setCustomerId(order.getCustomerId());
            orderDto.setStatus("TIMEOUT");
            jmsTemplate.convertAndSend("order:fail",orderDto);
        }
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
        return orderDao.findOneById(Long.valueOf(id));
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
