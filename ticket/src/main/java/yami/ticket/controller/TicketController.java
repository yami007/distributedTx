package yami.ticket.controller;

import com.yami.common.dto.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import yami.ticket.dao.TicketDao;
import yami.ticket.pojo.Ticket;
import yami.ticket.service.TicketService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 控制器层
 *
 * @author Administrator
 */
@RestController
@CrossOrigin
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketDao ticketDao;

    @Autowired
    private TicketService ticketService;

    @PostConstruct
    public void init() {
        if (ticketDao.count() > 0) {
            return;
        }
        Ticket ticket = new Ticket();
        ticket.setName("num_1");
        ticket.setTicketNum(100L);
        ticketService.add(ticket);

    }

    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Ticket> findAll() {
        return ticketService.findAll();
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Ticket findById(@PathVariable String id) {
        Ticket ticket = ticketService.findById(id);
        return ticket;
    }

    /**
     * 增加
     *
     * @param ticket
     */
    @RequestMapping(method = RequestMethod.POST)
    public String add(@RequestBody Ticket ticket) {
        ticketService.add(ticket);
        return "增加成功";
    }

    /**
     * 锁票
     *
     * @param orderDto
     * @return
     */
   /* @RequestMapping(value = "/lock", method = RequestMethod.POST)
    public Ticket lock(@RequestBody OrderDto orderDto) {
        Ticket ticket = ticketService.ticketLock(orderDto);
        return ticket;
    }*/

    /**
     * 锁票
     *
     * @param orderDto
     * @return
     */
    @RequestMapping(value = "/lock", method = RequestMethod.POST)
    public int lock(@RequestBody OrderDto orderDto) {
        return ticketService.ticketLock(orderDto);
    }

}
