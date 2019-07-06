package com.yami.user.server;

import com.yami.common.dto.OrderDto;
import com.yami.user.dao.PayInfoDao;
import com.yami.user.dao.UserDao;
import com.yami.user.dto.PayInfo;
import com.yami.user.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServer {
    private static final Logger logger = LoggerFactory.getLogger(UserServer.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private PayInfoDao payInfoDao;

    public void saveUser(User user) {
        User save = userDao.save(user);
    }

    public List<User> getUserAll() {
        List<User> all = userDao.findAll();
        return all;
    }

    /**
     * 付款
     *
     * @param dto
     */
    @Transactional
    @JmsListener(destination = "order:pay", containerFactory = "msqFactory")
    public void handleOrderPay(OrderDto dto) {
        logger.info("开始支付订单金额：{}", dto.getTitle());

        PayInfo payInfo = payInfoDao.findOneByOrderId(dto.getId());
        if (payInfo != null) {
            logger.warn("该订单已经支付");
        } else {
            User user = userDao.findOneById(dto.getCustomerId());
            if (user.getDeposit() < dto.getAmount()) {
                logger.warn("该用户余额不足");
                return;
            }
            payInfo = new PayInfo();
            payInfo.setOrderId(dto.getId());
            payInfo.setAmount(dto.getAmount());
            payInfo.setStatus("PAID");
            payInfoDao.save(payInfo);
            userDao.charge(user.getId(), dto.getAmount());
        }
        dto.setStatus("PAID");
        jmsTemplate.convertAndSend("order:ticket_move", dto);
    }
}
