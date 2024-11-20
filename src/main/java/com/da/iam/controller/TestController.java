package com.da.iam.controller;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.entity.User;
import com.da.iam.service.AuthenticationService;
import com.da.iam.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
public class TestController {
    UserService userService;
    AuthenticationService authenticationService;


    @GetMapping("/")
    public String test(){
        return "Hello World";
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request){
        return authenticationService.register(request).getToken();
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthenticationRequest request){
        return authenticationService.authenticate(request).getToken();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id){
        User user = userService.getUser(id);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        ;return ResponseEntity.ok(user);
    }

    @GetMapping("/change-password")
    public ResponseEntity changePassword(
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 @RequestParam String email){
        userService.changePassword(currentPassword, newPassword, confirmPassword,email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestParam String email){
        userService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String token){
        userService.resetPassword(email, newPassword, token);
        return ResponseEntity.ok().build();
    }


}
