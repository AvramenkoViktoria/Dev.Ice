package org.naukma.dev_ice.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordEncodeController {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncodeController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/encode")
    public String encodePassword(@RequestBody String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
