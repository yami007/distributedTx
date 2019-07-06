package com.yami.user.dao;

import com.yami.user.dto.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PayInfoDao extends JpaRepository<PayInfo,Long>, JpaSpecificationExecutor<PayInfo> {

    PayInfo findOneByOrderId(Long orderId);
}
