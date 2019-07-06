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
        int lockCount = ticketDao.LockTicket(orderDto.getCustomerId(), orderDto.getTicketNum());
        if (lockCount == 1) {
            logger.info("锁票了：{}",orderDto.getTitle());
            orderDto.setStatus("TICKET_LOCKED");
            jmsTemplate.convertAndSend("order:locked", orderDto);
        } else {

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
