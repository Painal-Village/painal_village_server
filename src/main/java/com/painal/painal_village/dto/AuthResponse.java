package com.painal.painal_village.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {

    private Integer id;
    private String name;
    private String email;
    private String role;
    private String message;
}
