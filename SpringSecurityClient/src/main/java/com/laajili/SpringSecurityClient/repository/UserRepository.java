package com.laajili.SpringSecurityClient.repository;

import com.laajili.SpringSecurityClient.entity.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<RegisteredUser, Long> {
    RegisteredUser findByEmail(String email);
}
