package com.da.iam.controller;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.service.AuthenticationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    AuthenticationService authenticationService;

    public TestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
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
}
