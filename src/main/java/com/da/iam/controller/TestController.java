package com.da.iam.controller;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.AuthenticationResponse;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.service.AuthenticationService;
import com.da.iam.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
public class TestController {
    PasswordService passwordService;
    AuthenticationService authenticationService;


    @GetMapping("/")
    public String test() {
        return "Hello World";
    }

    /**
     * Register a new user
     * @param request RegisterRequest object with email, password and string roles
     * @throws Exception if email already exists, status code 400
     * @return ResponseEntity object
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = null;
        try {
            response = authenticationService.register(request);
            return ResponseEntity.status(200).body(Map.of("Successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("Error", e.getMessage()));
        }
    }

    /**
     * Login a user
     * @param request AuthenticationRequest object with email and password
     * @throws Exception if email or password is incorrect, status code 400
     * @return ResponseEntity object
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = null;
        try {
            response = authenticationService.authenticate(request);
            return ResponseEntity.status(200).body(Map.of("Successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("Error", e.getMessage()));
        }
    }

    @GetMapping("/change-password")
    public ResponseEntity changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @RequestParam String email) {
        passwordService.changePassword(currentPassword, newPassword, confirmPassword, email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgot-password")
    public ResponseEntity forgotPassword(@RequestParam String email) {
        passwordService.forgotPassword(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String token) {
        passwordService.resetPassword(email, newPassword, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logout")
    public String logout() {
        return "Logged out";
    }


}
