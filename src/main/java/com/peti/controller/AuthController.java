package com.peti.controller;

import com.peti.model.ErrorResponse;
import com.peti.model.LoggedInUser;
import com.peti.model.User;
import com.peti.security.JwtHelper;
import com.peti.security.model.LoginRequest;
import com.peti.service.AuthService;
import com.peti.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private JwtHelper helper;
    private final UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager manager;
    @Autowired
    private AuthService authService;
    private Logger logger = LoggerFactory.getLogger(AuthController.class);
    //    private final JwtService jwtService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        authService.register(user);
        return ResponseEntity.ok("Registrierung erfolgreich – Warte auf Admin-Freischaltung.");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        try {
            // Retrieve user to check status
            User user = authService.getUserByEmail(request.getEmail());
            // Check if user is enabled
            if (!user.isEnabled()) {
                logger.warn("Login failed: Account is disabled for email: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Account is disabled. Please contact the administrator."));
            }
            // Proceed with authentication
            this.doAuthenticate(request.getEmail(), request.getPassword());
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = this.helper.generateToken(userDetails);
            LoggedInUser loggedInUser = LoggedInUser.builder()
                    .jwtToken(token)
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .enabled(user.isEnabled())
                    .build();
            logger.info("JWT token created for email: {}", userDetails.getUsername());
            return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password."));
        } catch (RuntimeException e) {
            logger.error("User not found: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found."));
        } catch (Exception e) {
            logger.error("Login error for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred during login."));
        }
    }
    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password !!");
        }
    }
}