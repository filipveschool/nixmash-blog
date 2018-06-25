/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    Collection<User> findAll();

    User findById(Long id);

    User save(User user);

    void delete(User user);

    boolean exists(Long userId);

    @Query("select distinct u from User u left join fetch " +
            "u.authorities left join fetch u.userData p")
    List<User> getUsersWithDetail();

    @Query("select distinct u from User u left join fetch " +
            "u.authorities left join fetch u.userData p where u.id = ?1")
    Optional<User> findByUserIdWithDetail(Long ID);

    Optional<User> findOneByEmail(String email);

    @Query("select distinct u from User u left join u.authorities a where a.id = ?1")
    List<User> findByAuthorityId(Long id);

    Optional<User> findOneByUserKey(String userKey);
}
