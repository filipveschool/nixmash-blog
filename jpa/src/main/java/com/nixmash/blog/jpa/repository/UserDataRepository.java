package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.UserData;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDataRepository extends CrudRepository<UserData, Long>{

    UserData findByUserId(Long userId);

    List<UserData> findAll();
}
