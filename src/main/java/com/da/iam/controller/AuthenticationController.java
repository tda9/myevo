package com.da.iam.controller;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.AuthenticationResponse;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.exception.ErrorResponseException;
import com.da.iam.service.AuthenticationService;
import com.da.iam.service.PasswordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
public class AuthenticationController {
    private final PasswordService passwordService;
    private final AuthenticationService authenticationService;

    @GetMapping("/confirmation-registration")
    public ResponseEntity<?> confirmRegister(@RequestParam String email, @RequestParam String token) throws Exception {
        try {
            authenticationService.confirmEmail(email, token);
        } catch (Exception e) {
            throw new ErrorResponseException(e.getMessage());
        }
        return ResponseEntity.ok().body("Confirm register successful");
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = null;
        try {
            response = authenticationService.register(request);
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("Error", e.getMessage()));
        }
    }

    //TODO:stop brute force attack login attempts
    //chua lam moi token jwt? hoac roi
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = null;
        try {
            response = authenticationService.authenticate(request);
            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("Error", e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam String currentPassword,@RequestParam String newPassword,
            @RequestParam String confirmPassword,@RequestParam String email) {
        passwordService.changePassword(currentPassword, newPassword, confirmPassword, email);
        return ResponseEntity.ok().body("Change password successful");
    }

    //TODO:stop spamming email and remove token from db after use
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            passwordService.forgotPassword(email);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("Error", e.getMessage()));
        }
        return ResponseEntity.ok().body("Sending Mail Reset Password Successful");
    }

    @GetMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String token) {
        passwordService.resetPassword(email, newPassword, token);
        return ResponseEntity.ok().body("Reset password successful");
    }

    @PostMapping("/api/logout")//de /logout khong se trung default url, va khong chay dc
    public String logout( @RequestParam String email) {
        authenticationService.logout(email);
        return "Logged out";
    }




    @GetMapping("/")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String test() {
        return "Hello World";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public String test1() {
        return "Hello World ADMIN";
    }
}
