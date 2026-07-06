package com.java.ipm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.java.ipm.dto.AuthRequest;
import com.java.ipm.dto.AuthResponse;

import com.java.ipm.service.UserService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
    private UserService userService;
	
//	@PostMapping("/register")
//	public ResponseEntity<String> register(@RequestBody AuthRequest request){
//		return ResponseEntity.ok(userService.register(request));
//	}
	 @PostMapping("/register")
	    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
	        try {
	            return ResponseEntity.ok(userService.register(request));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	        }
	    }
	
//	@PostMapping("/login")
//	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request){
//		return ResponseEntity.ok(userService.login(request));
//	}
	 @PostMapping("/login")
	    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
	        try {
	            return ResponseEntity.ok(userService.login(request));
	        } catch (RuntimeException e) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(e.getMessage()));
	        }
	    }

}