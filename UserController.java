package com.example.ccms_1.Controllers;

import com.example.ccms_1.Entities.Complaint;
import com.example.ccms_1.Entities.User;
import com.example.ccms_1.Enums.ServiceType;
import com.example.ccms_1.Services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        return ResponseEntity.ok(
                userService.login(user.getEmailId(), user.getPassword())
        );
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> viewMyComplaints(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getUserComplaints(authentication.getName())
        );
    }

    @PostMapping("/complaint/register")
    public ResponseEntity<String> registerComplaint(
            @RequestParam String description,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                userService.registerComplaint(
                        authentication.getName(),
                        description
                )
        );
    }
}
