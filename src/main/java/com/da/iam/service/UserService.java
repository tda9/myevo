package com.da.iam.service;

import com.da.iam.dto.UserProfile;
import com.da.iam.entity.User;
import com.da.iam.exception.UserNotFoundException;
import com.da.iam.repo.UserRepo;
import com.da.iam.utils.EmailUtils;
import com.da.iam.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public User updateUser(UserProfile userProfile) {
        if(!UserUtils.isValidPhoneNumber(userProfile.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if(!UserUtils.isValidDOB(userProfile.getDob())) {
            throw new IllegalArgumentException("Invalid date of birth");
        }
        User user = userRepo.findByEmail(userProfile.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setDob(LocalDate.parse(userProfile.getDob()));
        user.setPhone(userProfile.getPhone());
        return userRepo.save(user);
    }

    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

}
