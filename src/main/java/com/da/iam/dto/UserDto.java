package com.da.iam.dto;

import com.da.iam.entity.Role;
import com.da.iam.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Data
public class UserDto {
    private User user;
    public UserDto(User user){
        this.user = user;
    }
    Set<Role> roles;
}
