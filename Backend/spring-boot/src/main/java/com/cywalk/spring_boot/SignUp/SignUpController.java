package com.cywalk.spring_boot.Users.SignUp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cywalk.spring_boot.Users.Key;
import com.cywalk.spring_boot.Users.People;
import com.cywalk.spring_boot.Users.PeopleService;
import com.cywalk.spring_boot.Users.UserRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/signup")
public class SignUpController {

    private final PeopleService peopleService;

    @Autowired
    public SignUpController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {

        if (userRequest.getUsername() == null || userRequest.getUsername().isEmpty()
                || userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Username and password are required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        if (peopleService.getUserByUsername(userRequest.getUsername()).isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Username already in use.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }


        People user = new People();
        user.setUsername(userRequest.getUsername());
        user.setEmail("placeholder@gmail.com");

        Optional<People> createdUser = peopleService.createUser(user);

        if (createdUser.isPresent()) {


            peopleService.saveUserRequest(userRequest);

            Optional<Long> authKey = peopleService.generateAuthKey(user.getUsername());
            if (authKey.isPresent()) {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("username", createdUser.get().getUsername());
                responseBody.put("key", authKey.get());
                return ResponseEntity.ok(responseBody);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "Failed to generate authentication key.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create user.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Boolean>> isUsernameAvailable(@PathVariable String username) {
        boolean available = !peopleService.getUserByUsername(username).isPresent();
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", available);
        return ResponseEntity.ok(response);
    }
}