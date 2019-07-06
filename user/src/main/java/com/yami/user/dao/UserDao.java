package com.yami.user.dao;

import com.yami.user.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User findOneByUsername(String userName);

    User findOneById(Long id);

    @Modifying
    @Query("update User set deposit = deposit -?2 where id = ?1")
    int charge(Long id, int amount);
}

