package com.yami.order.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * 实体类
 * @author Administrator
 *
 */
@Entity(name="tb_order")
public class Order implements Serializable{

	@Id
	@GeneratedValue
	private Long id;//ID
	private String uuid;
	private Long customerId;
	private String title;
	private Long ticketNum;
	private int amount;
	private String status;
	private String reason;
	private ZonedDateTime creatDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getTicketNum() {
		return ticketNum;
	}

	public void setTicketNum(Long ticketNum) {
		this.ticketNum = ticketNum;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public ZonedDateTime getCreatDate() {
		return creatDate;
	}

	public void setCreatDate(ZonedDateTime creatDate) {
		this.creatDate = creatDate;
	}
}
