package com.laajili.SpringSecurityClient.event.listener;

import com.laajili.SpringSecurityClient.entity.RegisteredUser;
import com.laajili.SpringSecurityClient.event.RegistrationCompleteEvent;
import com.laajili.SpringSecurityClient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event){
        //Create the Verification Token for the registeredUser with Link
        RegisteredUser registeredUser =event.getRegisteredUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerficationTokenForUser(token, registeredUser);
        //Send Mail to registeredUser
        String url = event.getApplicationUrl()
                + "/verifyRegistration?token="
                + token;
        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}",url);
    }
}
