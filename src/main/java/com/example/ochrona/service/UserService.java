package com.example.ochrona.service;

import com.example.ochrona.model.Attempts;
import com.example.ochrona.model.User;
import com.example.ochrona.repository.AttemptsRepository;
import com.example.ochrona.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttemptsRepository attemptsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean existsByUsername(String username){
        return username == null || userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email){
        return email == null || userRepository.existsByEmail(email);
    }

    public boolean isPasswordSecure(String password){
        return calculateEntropy(password) > 3.2;
    }
    private double calculateEntropy(String password){
        byte[] input = password.getBytes();

        if (input.length == 0) {
            return 0.0;
        }

        /* Total up the occurrences of each byte */
        int[] charCounts = new int[256];
        for (byte b : input) {
            charCounts[b & 0xFF]++;
        }

        double entropy = 0.0;
        for (int i = 0; i < 256; ++i) {
            if (charCounts[i] == 0.0) {
                continue;
            }

            double freq = (double) charCounts[i] / input.length;
            entropy -= freq * (Math.log(freq) / Math.log(2));
        }

        return entropy;
    }

    public void save(User user) {
        String password = user.getPassword();
        String encoded = passwordEncoder.encode(password);
        user.setPassword(encoded);
        userRepository.save(user);
    }

    public int getAttempts(String username){
        Optional<Attempts> attempts = attemptsRepository.findAttemptsByUsername(username);
        return attempts.map(Attempts::getAttempts).orElse(0);
    }
}
