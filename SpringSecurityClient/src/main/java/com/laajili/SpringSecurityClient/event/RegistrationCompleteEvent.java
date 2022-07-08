package com.laajili.SpringSecurityClient.event;

import com.laajili.SpringSecurityClient.entity.RegisteredUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private RegisteredUser registeredUser;
    private String applicationUrl;

    public RegistrationCompleteEvent(RegisteredUser registeredUser, String applicationUrl) {
        super(registeredUser);
        this.registeredUser = registeredUser;
        this.applicationUrl=applicationUrl;
    }
}
