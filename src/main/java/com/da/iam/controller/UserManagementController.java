package com.da.iam.controller;


import com.da.iam.dto.UserDto;
import com.da.iam.dto.UserProfile;
import com.da.iam.dto.response.BasedResponse;
import com.da.iam.entity.Role;
import com.da.iam.entity.User;
import com.da.iam.exception.ErrorResponseException;
import com.da.iam.exception.UserNotFoundException;
import com.da.iam.repo.UserRoleRepo;
import com.da.iam.service.EmailService;
import com.da.iam.service.RoleService;
import com.da.iam.service.UserService;

import com.da.iam.utils.InputUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
public class UserManagementController {
    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleRepo userRoleRepo;

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/user")
    public BasedResponse<?> getUser(@RequestParam String email) {
        InputUtils.isValidEmail(email);
        User user = userService.getUserByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User not found"));

        UserDto userDto = new UserDto(user);
        userDto.setRoles(roleService.getRolesByUserId(user.getUserId()));
        return BasedResponse.builder()
                .requestStatus(true)
                .httpStatusCode(200)
                .data(EntityModel.of(userDto, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(email)).withSelfRel()))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getUsers() {
        Iterable<User> userEntities = userService.getUsers();
        if (userEntities == null || !userEntities.iterator().hasNext()) {
            throw new UserNotFoundException("No users found");
        }
        List<EntityModel<UserDto>> users = new ArrayList<>();
        toCollectionModel(userEntities, users);
        return ResponseEntity.ok(CollectionModel.of(users, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUsers()).withSelfRel()));
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/users")
    public ResponseEntity<EntityModel<?>> updateUser(@RequestBody UserProfile userProfile) {
        User updatedUser = null;
        try {
            updatedUser = userService.updateUser(userProfile);
        } catch (Exception e) {
            throw new ErrorResponseException("Error user profile");
        }
        return ResponseEntity.ok(EntityModel.of(updatedUser, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(updatedUser.getEmail())).withSelfRel()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.status(400).body(Map.of("User not found", id));
        }
        Set<Role> roles = roleService.getRolesByUserId(user.getUserId());
        UserDto userDto = new UserDto(user);
        userDto.setRoles(roles);
        userService.deleteUser(id);
        userRoleRepo.deleteByUserId(id);
        return ResponseEntity.status(200).body(Map.of("Deleted successful", userDto));
    }

    private void toCollectionModel(Iterable<User> userEntities, List<EntityModel<UserDto>> users) {
        for (User user : userEntities) {
            UserDto userDto = new UserDto(user);
            userDto.setRoles(roleService.getRolesByUserId(user.getUserId()));
            users.add(EntityModel.of(userDto, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(user.getEmail())).withSelfRel()));
        }
    }
}
