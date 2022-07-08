package com.laajili.SpringSecurityClient.repository;

import com.laajili.SpringSecurityClient.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
}
