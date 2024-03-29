package com.yami.user.dto;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pay_info")
public class PayInfo implements Serializable{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "order_id")
    private Long orderId;
    private String status;
    private int amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
