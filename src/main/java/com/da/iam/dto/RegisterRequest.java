package com.da.iam.dto;

import com.da.iam.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Builder
public record RegisterRequest(String email,String password) {
    //,Set<String> role
}
