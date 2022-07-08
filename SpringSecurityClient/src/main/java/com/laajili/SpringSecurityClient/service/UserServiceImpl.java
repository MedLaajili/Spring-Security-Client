package com.laajili.SpringSecurityClient.service;

import com.laajili.SpringSecurityClient.Model.UserModel;
import com.laajili.SpringSecurityClient.entity.PasswordResetToken;
import com.laajili.SpringSecurityClient.entity.RegisteredUser;
import com.laajili.SpringSecurityClient.entity.VerificationToken;
import com.laajili.SpringSecurityClient.repository.PasswordResetTokenRepo;
import com.laajili.SpringSecurityClient.repository.UserRepository;
import com.laajili.SpringSecurityClient.repository.VerficationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerficationTokenRepository verficationTokenRepository;

    @Autowired
    private PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public RegisteredUser registerUser(UserModel userModel) {
        RegisteredUser registeredUser = new RegisteredUser();
        registeredUser.setEmail(userModel.getEmail());
        registeredUser.setFirstName(userModel.getFirstName());
        registeredUser.setLastName(userModel.getLastName());
        registeredUser.setRole("USER");
        registeredUser.setPassword(passwordEncoder.encode(userModel.getPassword()));

        userRepository.save(registeredUser);
        return registeredUser;
    }

    @Override
    public void saveVerficationTokenForUser(String token, RegisteredUser registeredUser) {
        VerificationToken verificationToken
                = new VerificationToken(registeredUser,token);
        verficationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verficationTokenRepository.findByToken(token);

        if(verificationToken==null){
            return "invalid";
        }

        RegisteredUser registeredUser = verificationToken.getRegisteredUser();
        Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationTime().getTime()- cal.getTime().getTime())<=0 ) {
            verficationTokenRepository.delete(verificationToken);
            return "expired";
        }
        registeredUser.setEnabled(true);
        userRepository.save(registeredUser);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verficationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verficationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public RegisteredUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(RegisteredUser registeredUser, String token) {
        PasswordResetToken passwordResetToken
                = new PasswordResetToken(registeredUser,token);
        passwordResetTokenRepo.save(passwordResetToken);
    }

    @Override
    public String validatePasswordToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepo.findByToken(token);

        if (passwordResetToken == null) {
            return "invalid";
        }
        RegisteredUser registeredUser = passwordResetToken.getRegisteredUser();
        Calendar cal = Calendar.getInstance();

        if ((passwordResetToken.getExpirationTime().getTime() - cal.getTime().getTime()) <= 0) {
            passwordResetTokenRepo.delete(passwordResetToken);
            return "expired";
        }
        return "valid";
    }

    @Override
    public Optional<RegisteredUser> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepo.findByToken(token).getRegisteredUser());
    }

    @Override
    public void changePassword(RegisteredUser registeredUser, String newPassword) {
        registeredUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(registeredUser);
    }

    @Override
    public boolean checkIfValidOldPassword(RegisteredUser registeredUser, String oldPassword) {
        return passwordEncoder.matches(oldPassword, registeredUser.getPassword());
    }
}
