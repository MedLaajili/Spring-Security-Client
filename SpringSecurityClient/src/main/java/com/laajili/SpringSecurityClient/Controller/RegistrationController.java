package com.laajili.SpringSecurityClient.Controller;

import com.laajili.SpringSecurityClient.Model.PasswordModel;
import com.laajili.SpringSecurityClient.Model.UserModel;
import com.laajili.SpringSecurityClient.entity.RegisteredUser;
import com.laajili.SpringSecurityClient.entity.VerificationToken;
import com.laajili.SpringSecurityClient.event.RegistrationCompleteEvent;
import com.laajili.SpringSecurityClient.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request){
        RegisteredUser registeredUser = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(registeredUser,applcationUrl(request)));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verifies Successfully!";
        }
        return "Bad User";
    }

    @GetMapping("/resendVerifyToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request){
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);

        RegisteredUser registeredUser = verificationToken.getRegisteredUser();
        resendVerificationTokenMail(registeredUser,applcationUrl(request),verificationToken);
        return "Verification Link Sent";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request){
      RegisteredUser registeredUser = userService.findByEmail(passwordModel.getEmail());
      String url = "";
      if (registeredUser != null) {
          String token = UUID.randomUUID().toString();
          userService.createPasswordResetTokenForUser(registeredUser,token);
          url = passwordResetTokenMail(registeredUser,applcationUrl(request),token);
      }
      return url;
    }

    @GetMapping("/savePassword")
    private String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel){
        String result = userService.validatePasswordToken(token);
        if (!result.equalsIgnoreCase("valid")) {
            return "Invalid Token";
        }
        Optional<RegisteredUser> registeredUser = userService.getUserByPasswordResetToken(token);
        if (registeredUser.isPresent()) {
            userService.changePassword(registeredUser.get(), passwordModel.getNewPassword());
            return "Password Reset Successfully";
        } else {
            return "Invalid Token";
        }
    }

    @PostMapping("/changePassword")
    private String changePassword(@RequestBody PasswordModel passwordModel){
        RegisteredUser registeredUser = userService.findByEmail(passwordModel.getEmail());
        if (!userService.checkIfValidOldPassword(registeredUser,passwordModel.getOldPassword())) {
            return "Invalid Old Pasword";
        }
        // Save New Password
        userService.changePassword(registeredUser,passwordModel.getNewPassword());
        return "Password Changed Successfully";
    }


    private String passwordResetTokenMail(RegisteredUser registeredUser, String applcationUrl, String token) {
        String url = applcationUrl
                + "/savePassword?token="
                + token;
        //sendVerificationEmail()
        log.info("Click the link to reset your password: {}",url);
        return url;
    }

    ;

    private void resendVerificationTokenMail(RegisteredUser registeredUser, String applcationUrl, VerificationToken verificationToken) {
        String url = applcationUrl
                + "/verifyRegistration?token="
                + verificationToken.getToken();
        //sendVerificationEmail()
        log.info("Click the link to verify your account: {}",url);
    }

    private String applcationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName()+
                ":" +
                request.getServerPort()+
                request.getContextPath();
    }
}
