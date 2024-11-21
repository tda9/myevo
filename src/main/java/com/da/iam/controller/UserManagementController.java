package com.da.iam.controller;


import com.da.iam.entity.User;
import com.da.iam.service.PasswordService;
import com.da.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@RestController
public class UserManagementController {
    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/user")
    public ResponseEntity<EntityModel<User>> getUser(@RequestParam(required = false) Long id, @RequestParam(required = false) String email) {
        User user = null;
        if (id != null) {
            user = userService.getUserById(id);
        } else if (email != null) {
            user = userService.getUserByEmail(email);
        }
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EntityModel.of(user, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(id, email)).withSelfRel()));
    }



    @GetMapping("/users")
    public ResponseEntity<CollectionModel<EntityModel<User>>> getUsers() {
        Iterable<User> userEntities = userService.getUsers();
        if(userEntities == null || !userEntities.iterator().hasNext()) {
            return ResponseEntity.notFound().build();
        }

        List<EntityModel<User>> users = new ArrayList<>();
        toCollectionModel(userEntities, users);
        return ResponseEntity.ok(CollectionModel.of(users, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUsers()).withSelfRel()));
    }

    @PostMapping("/users")
    public ResponseEntity<EntityModel<User>> createUser(@RequestBody User user) {
        User newUser = userService.createUser(user);
        return ResponseEntity.ok(EntityModel.of(newUser, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(newUser.getUserId(), null)).withSelfRel()));
    }

    @PutMapping("/users")
    public ResponseEntity<EntityModel<User>> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(EntityModel.of(updatedUser, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(updatedUser.getUserId(), null)).withSelfRel()));
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
    private void toCollectionModel(Iterable<User> userEntities, List<EntityModel<User>> users) {
        for(User user : userEntities) {
            users.add(EntityModel.of(user, linkTo(WebMvcLinkBuilder.methodOn(UserManagementController.class).getUser(user.getUserId(), null)).withSelfRel()));
        }

    }

}
