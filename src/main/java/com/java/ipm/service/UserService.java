package com.java.ipm.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.java.ipm.dto.AuthRequest;
import com.java.ipm.dto.AuthResponse;
import com.java.ipm.entity.User;
import com.java.ipm.repository.UserRepository;
import com.java.ipm.security.JwtUtil;

@Service
public class UserService {
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtil jwtUtil;

    public String register(AuthRequest request) {
    	if(userRepository.findByUsername(request.getUsername()).isPresent()) {
    		throw new RuntimeException("Username already exist");
    	}
    	User user = new User();
    	
    	user.setUsername(request.getUsername());
    	user.setPassword(passwordEncoder.encode(request.getPassword()));
    	user.setEmail(request.getEmail());
    	user.setRole(request.getRole());
    	userRepository.save(user);
    	return "User register successfully...!";
    }
    
    public AuthResponse login(AuthRequest request) {
    	authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), 
    			request.getPassword()));
    	
    	String token = jwtUtil.generateToken(userRepository.findByUsername(request.getUsername())
    			.orElseThrow(()-> new RuntimeException("User not found")));
    	
    	return new AuthResponse(token);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    
}