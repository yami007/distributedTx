package yami.ticket.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import yami.ticket.pojo.Ticket;

import java.util.List;

/**
 * 数据访问接口
 *
 * @author Administrator
 */
public interface TicketDao extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    List<Ticket> findOneByOwner(String owner);

    Ticket findOneByTicketNumAndLockUser(Long ticketNum,Long lockUser);

    @Override
    @Modifying(clearAutomatically = true)
    Ticket save(Ticket ticket);

    @Query("update Ticket t set t.lockUser = ?1 where t.lockUser = null and t.ticketNum = ?2")
    @Modifying
    int LockTicket(Long lockUser,Long ticketNum);

    @Modifying
    @Query("update Ticket t set t.owner = ?1,t.lockUser=null where t.lockUser = ?1 and t.ticketNum = ?2")
    int moveTicket(Long customerId, Long ticketNum);

    @Modifying
    @Query("update Ticket t set t.lockUser=null where t.lockUser = ?1 and t.ticketNum = ?2")
    int unLockTicket(Long customerId, Long ticketNum);
}
