package com.da.iam.service;

import com.da.iam.dto.AuthenticationRequest;
import com.da.iam.dto.AuthenticationResponse;
import com.da.iam.dto.RegisterRequest;
import com.da.iam.entity.Role;
import com.da.iam.entity.User;
import com.da.iam.repo.RoleRepo;
import com.da.iam.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JWTService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        User userEntity = new User(request.getEmail(), passwordEncoder.encode(request.getPassword()),null,null,null);
        Set<Role> roles = new HashSet<>();
        for(String role : request.getRole()) {
            Role tmpRole = new Role();
            tmpRole.setName(role);
            roles.add(tmpRole);
        }
        userEntity.setRoles(roles);
        userRepo.save(userEntity);

        var user = CustomUserDetails.builder()
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .authorities(request.getRole().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Prefix roles with "ROLE_"
                        .collect(Collectors.toSet())).build();
          var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword())
        );

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .email(user.getEmail()).password(user.getPassword())
                .authorities(customUserDetailsService.mapRolesToAuthorities(user.getRoles())).build();
        var jwtToken = jwtService.generateToken(userDetails);
        System.out.println(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
