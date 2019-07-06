package com.yami.order.dao;

import com.yami.order.dto.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface OrderDao extends JpaRepository<Order,String>,JpaSpecificationExecutor<Order>{

    List<Order> findOneByCustomerId(Long customerId);

    Order findOneByUuid(String uuid);

    Order findOneById(Long id);

    List<Order> findAllByStatusAndCreatDateBefore(String status, ZonedDateTime zonedDateTime);
}
