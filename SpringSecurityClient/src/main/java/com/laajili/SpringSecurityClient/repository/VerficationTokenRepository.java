package com.laajili.SpringSecurityClient.repository;

import com.laajili.SpringSecurityClient.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerficationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}
