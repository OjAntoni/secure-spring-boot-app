package com.example.ochrona.controller;

import com.example.ochrona.model.User;
import com.example.ochrona.model.UserDto;
import com.example.ochrona.model.UserLoginDto;
import com.example.ochrona.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        model.addAttribute("username", "");
        model.addAttribute("emailError", "");
        model.addAttribute("passwordError", "");
        return "registration";
    }

    @PostMapping("/user/registration")
    public String registerUser(Model model, @Valid @ModelAttribute(name = "user") UserDto userDto, BindingResult bindingResult){
        boolean isOk = true;
        if(bindingResult.hasErrors()){
            isOk = false;
            bindingResult.getAllErrors().forEach(e -> System.out.println(e));
            System.out.println("binding errors");
        }
        String username = userDto.getUsername();
        if(userService.existsByUsername(username)){
            isOk=false;
            model.addAttribute("usernameError", "User with such username already exists");
        }
        String email = userDto.getEmail();
        if(userService.existsByEmail(email)){
            isOk=false;
            model.addAttribute("emailError", "Email is already registered in our service");
        }
        if(!userDto.getPassword().equals(userDto.getMatchingPassword())){
            model.addAttribute("passwordError", "Passwords don't match");
            return "registration";
        }
        if(!userService.isPasswordSecure(userDto.getPassword())){
            isOk=false;
            model.addAttribute("passwordError", "Too weak password");
        }
        if(isOk){
            userService.save(new User(-1, username, userDto.getPassword(), email));
        }
        return isOk ? "login" : "registration";
    }

    @GetMapping("/user/login")
    public String getLoginPage(HttpServletRequest request, Model model, HttpSession session, UserLoginDto user){
        model.addAttribute(
                "error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION")
        );
        return "login";
    }

    @PostMapping("/user/login")
    public String afterLoginSuccess(){
        return "index";
    }

    @PostMapping
    public String loginUser(HttpServletRequest request, HttpSession session){
        session.setAttribute(
                "error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION")
        );
        return "login";
    }

    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username or password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        } else {
            error = "Invalid username or password!";
        }
        System.out.println(error);
        return error;
    }
}
