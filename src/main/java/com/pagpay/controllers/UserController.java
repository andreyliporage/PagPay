package com.pagpay.controllers;

import com.pagpay.domain.user.User;
import com.pagpay.dtos.UserDTO;
import com.pagpay.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<User> post(@RequestBody UserDTO user) throws Exception {
        User newUser = this.service.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        List<User> users = this.service.getAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
