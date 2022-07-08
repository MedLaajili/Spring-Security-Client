package com.laajili.SpringSecurityClient.service;

import com.laajili.SpringSecurityClient.Model.UserModel;
import com.laajili.SpringSecurityClient.entity.RegisteredUser;
import com.laajili.SpringSecurityClient.entity.VerificationToken;

import java.util.Optional;

public interface UserService {
    RegisteredUser registerUser(UserModel userModel);

    void saveVerficationTokenForUser(String token, RegisteredUser registeredUser);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    RegisteredUser findByEmail(String email);

    void createPasswordResetTokenForUser(RegisteredUser registeredUser, String token);

    String validatePasswordToken(String token);

    Optional<RegisteredUser> getUserByPasswordResetToken(String token);

    void changePassword(RegisteredUser registeredUser, String newPassword);

    boolean checkIfValidOldPassword(RegisteredUser registeredUser, String oldPassword);
}
