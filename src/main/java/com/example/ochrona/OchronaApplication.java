package com.example.ochrona;

import com.example.ochrona.model.User;
import com.example.ochrona.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.Md4PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import java.security.SecureRandom;

@SpringBootApplication
public class OchronaApplication {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    public static void main(String[] args) {
        SpringApplication.run(OchronaApplication.class, args);
    }

    @EventListener
    public void initDatabaseWithData(ApplicationReadyEvent e){
        String bob = encoder.encode("bob");
        System.out.println(bob);
        System.out.println(encoder.matches("bobBB ", bob));
        userRepository.save(new User(-1, "bob", encoder.encode( "bob"), "bobemail@gmail.com"));
        userRepository.save(new User(-1, "bach", encoder.encode( "haslo"), "bachemail@gmail.com"));
        userRepository.save(new User(-1, "wiktor", encoder.encode( "lodylody"), "lodyemail@gmail.com"));
    }
}
