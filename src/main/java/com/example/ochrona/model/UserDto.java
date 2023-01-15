package com.example.ochrona.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserDto {
    @NotNull
    @NotEmpty
    @NotBlank(message = "Username can't be blank")
    @Length(min = 4, max = 12, message = "Your username should be between 4 and 12 symbols")
    private String username = "";

    @NotNull
    @NotEmpty(message = "Password can't be empty")
    @NotBlank(message = "Password can't be blank")
    private String password = "";

    @NotNull
    @NotEmpty(message = "Password can't be empty")
    @NotBlank(message = "Password can't be blank")
    private String matchingPassword = "";

    @NotNull
    @NotEmpty(message = "Email can't be empty")
    @Email(message = "Your email is not valid")
    private String email = "";

}
