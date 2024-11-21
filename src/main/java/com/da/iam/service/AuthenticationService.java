package com.da.iam.service;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.AuthenticationResponse;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.entity.Role;
import com.da.iam.entity.User;
import com.da.iam.repo.UserRepo;
import com.da.iam.utils.EmailUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    /**
     * Register a new user:
     * <p>Description:
     * </p>
     *
     * @param request
     * @return AuthenticationResponse object
     */
    public AuthenticationResponse register(RegisterRequest request) {
        if (!EmailUtils.isValidEmail(request.email()) || userRepo.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Invalid email");
        }
        User userEntity = new User(request.email(), passwordEncoder.encode(request.password()));
        Set<Role> userEntityRoles = getRoles(request.role());
        userEntity.setRoles(userEntityRoles);
        userRepo.save(userEntity);

        var userDetails = getUserDetails(userEntity);
        //TODO: send email confirm registration here
        //emailService.sendConfirmationRegistrationEmail(userEntity.getEmail(), jwtService.generateToken(userDetails));
        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Authenticate a user:
     * <p>Description:
     * </p>
     *
     * @param request
     * @return AuthenticationResponse object
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        String email = request.email();
        String password = request.password();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        var userEntity = userRepo.findByEmail(email).orElseThrow();
        CustomUserDetails userDetails = getUserDetails(userEntity);
        var jwtToken = jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    /**
     * Get a CustomUserDetails object from a User object
     *
     * @param user
     * @return CustomUserDetails object
     */
    private CustomUserDetails getUserDetails(User user) {
        return CustomUserDetails.builder()
                .email(user.getEmail()).password(user.getPassword())
                .authorities(customUserDetailsService.mapRolesToAuthorities(user.getRoles())).build();
    }

    /**
     * Get string roles from the request and return a set of Role type objects
     *
     * @param roles request's roles
     * @return Set<Role>
     */
    private Set<Role> getRoles(Set<String> roles) {
        Set<Role> rolesSet = new HashSet<>();
        for (String role : roles) {
            Role tmpRole = new Role();
            tmpRole.setName(role);
            rolesSet.add(tmpRole);
        }
        return rolesSet;
    }
}
