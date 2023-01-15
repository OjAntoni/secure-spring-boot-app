package com.example.ochrona.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserLoginDto {
    @NotNull
    @NotEmpty(message = "Username can't be empty")
    @NotBlank(message = "Username can't be blank")
    private String username;
    @NotNull
    @NotEmpty(message = "Password can't be empty")
    @NotBlank(message = "Password can't be blank")
    private String password;
}
