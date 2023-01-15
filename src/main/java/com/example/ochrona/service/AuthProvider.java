package com.example.ochrona.service;

import com.example.ochrona.model.Attempts;
import com.example.ochrona.model.User;
import com.example.ochrona.repository.AttemptsRepository;
import com.example.ochrona.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AuthProvider implements AuthenticationProvider {
    private static final int ATTEMPTS_LIMIT = 3;
    @Autowired
    private SecurityUserDetailsService userDetailsService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AttemptsRepository attemptsRepository;
    @Autowired private UserRepository userRepository;

    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Thread.sleep(1000);
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Your account is locked");
        }
        if(passwordEncoder.matches(password, user.getPassword())){
            Optional<Attempts>
                    userAttempts = attemptsRepository.findAttemptsByUsername(username);
            if (userAttempts.isPresent()) {
                Attempts attempts = userAttempts.get();
                attempts.setAttempts(0);
                attemptsRepository.save(attempts);
            }
            return new UsernamePasswordAuthenticationToken(username, password, List.of());
        } else {
            processFailedAttempts(username, userRepository.findUserByUsername(username));
            throw new AuthenticationException("Invalid username or password") {};
        }

    }

    private void processFailedAttempts(String username, User user) {
        Optional<Attempts>
                userAttempts = attemptsRepository.findAttemptsByUsername(username);
        if (userAttempts.isEmpty()) {
            Attempts attempts = new Attempts();
            attempts.setUsername(username);
            attempts.setAttempts(1);
            attemptsRepository.save(attempts);
        } else {
            Attempts attempts = userAttempts.get();
            attempts.setAttempts(attempts.getAttempts() + 1);
            attemptsRepository.save(attempts);

            if (attempts.getAttempts() + 1 >
                    ATTEMPTS_LIMIT) {
                user.setAccountNonLocked(false);
                userRepository.save(user);
                throw new LockedException("Too many invalid attempts. Account is locked!!");
            }
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
