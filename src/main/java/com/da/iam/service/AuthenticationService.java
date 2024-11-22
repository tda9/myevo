package com.da.iam.service;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.AuthenticationResponse;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.entity.*;
import com.da.iam.exception.TooManyRequestsException;
import com.da.iam.exception.UserNotFoundException;
import com.da.iam.repo.*;
//import com.da.iam.repo.UserRoleRepo;
import com.da.iam.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JWTService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;
    private final RoleRepo roleRepo;
    private final UserRoleRepo userRoleRepo;
    private final PasswordService passwordService;
    private final PasswordResetTokenRepo passwordResetTokenRepo;
    private final BlackListTokenRepo blackListTokenRepo;

    public AuthenticationResponse register(RegisterRequest request) {
        String email = request.email();
        //email khong hop le/khong ton tai
        if (!EmailUtils.isValidEmail(request.email())) {
            throw new IllegalArgumentException("Invalid email format");
        } else if (userRepo.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email existed");
        }

        User userEntity = new User(email, passwordEncoder.encode(request.password()));
//        Set<Role> roles = getRoles(request.role());
        userRepo.save(userEntity);
//        for (Role r : roles) {
//            //Role role = roleRepo.findRoleByName(r.getName());
//            Role role = roleRepo.findRoleByName("USER");
//            UserRoles userRoles = new UserRoles(userEntity.getUserId(), role.getRoleId());
//            userRoleRepo.saveUserRole(userEntity.getUserId(), userRoles.getRoleId());
//        }
            Role role = roleRepo.findRoleByName("USER");
            UserRoles userRoles = new UserRoles(userEntity.getUserId(), role.getRoleId());
            userRoleRepo.saveUserRole(userEntity.getUserId(), userRoles.getRoleId());

        String token = passwordService.generateToken();
        //TODO: send email confirm registration here
        //5 phut hieu luc, trong thoi gian do khong duoc gui them
        sendConfirmation(request.email(), token, userEntity);
        return AuthenticationResponse.builder().token("We send confirmation register to your email. It will expire in 5 minutes").build();
    }

    public void sendConfirmation(String to, String token, User userEntity) {
        if (userEntity == null) {
            throw new UserNotFoundException("User Entity null");
        }
        Optional<PasswordResetToken> lastToken = passwordResetTokenRepo.findTopByUserIdOrderByCreatedAtDesc(userEntity.getUserId());
        if (lastToken.isPresent() && lastToken.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new TooManyRequestsException("You can only request a password reset every 5 minutes.");
        } else if (lastToken.isPresent() && lastToken.get().getExpirationDate().isAfter(LocalDateTime.now())) {
            passwordResetTokenRepo.delete(lastToken.get());
        }
        emailService.sendConfirmationRegistrationEmail(to, token);
        passwordResetTokenRepo.save(new PasswordResetToken(token, LocalDateTime.now().plusMinutes(5), userEntity.getUserId()));
    }

    public void confirmEmail(String email, String token) {
        User user = userRepo.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User not found"));
        PasswordResetToken requestToken = passwordResetTokenRepo.findPasswordResetTokenByToken(token);
        if (requestToken.getExpirationDate().isBefore(LocalDateTime.now()) || user.getUserId() != (requestToken.getUserId())) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        user.setConfirm(true);
        userRepo.save(user);
        passwordResetTokenRepo.delete(requestToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        String email = request.email();
        String password = request.password();

        User userEntity = userRepo.findByEmail(request.email()).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!userEntity.isConfirm()) {
            String token = passwordService.generateToken();
            sendConfirmation(request.email(), token, userEntity);
            return AuthenticationResponse.builder().token("Email hasn't been confirmed. We send confirmation register to your email. It will expire in 5 minutes").build();
        }
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        var jwtToken = jwtService.generateToken(userEntity.getEmail());
        blackListTokenRepo.save(new BlackListToken(jwtToken, LocalDateTime.now().plusMinutes(10), LocalDateTime.now(), userEntity.getUserId()));
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    private Set<Role> getRoles(Set<String> roles) {
        Set<Role> rolesSet = new HashSet<>();
        for (String role : roles) {
            Role tmpRole = new Role();
            tmpRole.setName(role);
            rolesSet.add(tmpRole);
        }
        return rolesSet;
    }

    @Transactional//tim hieu tai sao o day can transactional
    public void logout(String email) {
        User u = userRepo.findByEmail(email).orElseThrow();
        blackListTokenRepo.deleteAllByUserId(u.getUserId());
    }
    //private CustomUserDetails getUserDetails(User user) {
//        Set<Role> userRoles = roleRepo.findRolesByUserId(user.getUserId());
//        return CustomUserDetails.builder()
//                .email(user.getEmail()).password(user.getPassword())
//                .authorities(customUserDetailsService.mapRolesToAuthorities(userRoles)).build();
//    }
}
