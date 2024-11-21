package com.da.iam.service;

import com.da.iam.entity.User;
import com.da.iam.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;

    public User getUserById(Long id) {
        return userRepo.findById(id).orElseThrow();
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElseThrow();
    }
    public Iterable<User> getUsers() {
        return userRepo.findAll();
    }
    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(User user) {
        return userRepo.save(user);
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

}
