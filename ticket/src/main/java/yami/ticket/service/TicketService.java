package yami.ticket.service;

import com.yami.common.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yami.ticket.dao.TicketDao;
import yami.ticket.pojo.Ticket;

import java.util.List;

/**
 * 服务层
 *
 * @author Administrator
 */
@Service
public class TicketService {
    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 锁票
     *
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:new", containerFactory = "msqFactory")
    public void handTicketLock(OrderDto orderDto) {
        // 数据库锁票操作
        int lockCount = ticketDao.LockTicket(orderDto.getCustomerId(), orderDto.getTicketNum());
        // 锁票成功就发送消息
        if (lockCount == 1) {
            logger.info("锁票了：{}",orderDto.getTitle());
            orderDto.setStatus("TICKET_LOCKED");
            jmsTemplate.convertAndSend("order:locked", orderDto);
        } else {
            // 锁票失败后发送锁票失败的消息给order服务
            logger.info("锁票失败：{}",orderDto.getTitle());
            orderDto.setStatus("TICKET_LOCK_FAIL");
            jmsTemplate.convertAndSend("order:fail", orderDto);
        }
    }

    /**
     * 交票
     *
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:ticket_move", containerFactory = "msqFactory")
    public void handTicketMove(OrderDto orderDto) {
        // 通过票号和用户id查询票，并叫锁票状态清空，更新票所有人为用户id
        Ticket ticket = ticketDao.findOneByTicketNumAndLockUser(orderDto.getTicketNum(), orderDto.getCustomerId());
        int count = ticketDao.moveTicket(orderDto.getCustomerId(), ticket.getTicketNum());
        if (count == 0) {
            logger.warn("已经交票");
        } else {
            logger.info("订单已完成：{}",orderDto.getTitle());
            orderDto.setStatus("TICKET_MOVE");
            jmsTemplate.convertAndSend("order:finish", orderDto);
        }
    }

    /**
     * 解锁票
     *
     * @param orderDto
     */
    @Transactional
    @JmsListener(destination = "order:ticket_error", containerFactory = "msqFactory")
    public void handTicketUnlock(OrderDto orderDto) {
        // 通过票号和用户id查询票，并叫锁票状态清空，更新票所有人为用户id
        Ticket ticket = ticketDao.findOneByTicketNumAndLockUser(orderDto.getTicketNum(), orderDto.getCustomerId());
        int count = ticketDao.unLockTicket(orderDto.getCustomerId(), ticket.getTicketNum());
        if (count == 0) {
            logger.warn("解锁失败：{}",orderDto.getTitle());
        }
        count = ticketDao.unMoveTicket(ticket.getOwner(), ticket.getTicketNum());
        if (count == 0) {
            logger.warn("票转移失败：{}",orderDto.getTitle());
        }
        logger.info("解锁票：{}",orderDto.getTitle());
        jmsTemplate.convertAndSend("order:fail", orderDto);
    }

    /**
     * 锁票
     * @param orderDto
     */
   /* @Transactional
    public Ticket ticketLock(OrderDto orderDto) {
        Ticket ticket = ticketDao.findOneByTicketNum(orderDto.getTicketNum());
        ticket.setLockUser(orderDto.getCustomerId());
        ticket = ticketDao.save(ticket);
        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ticket;
    }*/

    /**
     * 锁票
     *
     * @param orderDto
     */
    @Transactional
    public int ticketLock(OrderDto orderDto) {
        logger.info("对{}锁票！", orderDto);
        int lockCount = ticketDao.LockTicket(orderDto.getCustomerId(), orderDto.getTicketNum());
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            logger.error("出错了！");
        }
        return lockCount;
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    public List<Ticket> findAll() {
        return ticketDao.findAll();
    }

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    public Ticket findById(String id) {
        return ticketDao.findOne(id);
    }

    /**
     * 增加
     *
     * @param ticket
     */
    public void add(Ticket ticket) {
        ticketDao.save(ticket);
    }

    /**
     * 修改
     *
     * @param ticket
     */
    public void update(Ticket ticket) {
        ticketDao.save(ticket);
    }

}
