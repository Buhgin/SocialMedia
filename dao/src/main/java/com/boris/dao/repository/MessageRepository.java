package com.boris.dao.repository;

import com.boris.dao.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;



public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {


}
