package com.doombitshop.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;

}
