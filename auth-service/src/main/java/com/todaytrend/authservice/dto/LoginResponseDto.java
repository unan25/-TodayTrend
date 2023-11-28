package com.todaytrend.authservice.dto;

import com.todaytrend.authservice.domain.enum_.UserType;
import com.todaytrend.authservice.domain.enum_.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {

    private String uuid;
    private UserType userType;
    private Role role;
}