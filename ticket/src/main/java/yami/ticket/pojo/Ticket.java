package yami.ticket.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 实体类
 * @author Administrator
 *
 */
@Entity
@Table(name="tb_ticket")
public class Ticket implements Serializable{

	@Id
	@GeneratedValue
	private Long id;//ID
	private String name;
	private Long owner;
	private Long lockUser;
	private Long ticketNum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getOwner() {
		return owner;
	}

	public void setOwner(Long owner) {
		this.owner = owner;
	}

	public Long getLockUser() {
		return lockUser;
	}

	public void setLockUser(Long lockUser) {
		this.lockUser = lockUser;
	}

	public Long getTicketNum() {
		return ticketNum;
	}

	public void setTicketNum(Long ticketNum) {
		this.ticketNum = ticketNum;
	}
}
