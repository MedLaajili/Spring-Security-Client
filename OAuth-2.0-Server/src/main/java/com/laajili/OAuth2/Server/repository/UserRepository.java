package com.laajili.OAuth2.Server.repository;

import com.laajili.OAuth2.Server.entity.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<RegisteredUser, Long> {
    RegisteredUser findByEmail(String email);
}
