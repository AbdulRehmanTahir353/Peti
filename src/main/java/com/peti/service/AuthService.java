package com.peti.service;


import com.peti.constants.Role;
import com.peti.model.User;
import com.peti.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder encoder;
//    @Autowired
//    private SieberEngine sieberEngine;

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("E-Mail-Address.already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.setRole(Role.USER);
        return userRepository.save(user);
    }
    public User getUserByEmail(String email)
    {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return user;
    }

//    public void changePassword(String oldPassword, String newPassword)
//    {
//        User currentUser = sieberEngine.getLoggedInUser();
//        if (!encoder.matches(oldPassword, currentUser.getPassword()))
//        {
//            throw new RuntimeException("Old password is incorrect");
//        }
//        String encodedNewPassword = encoder.encode(newPassword);
//        currentUser.setPassword(encodedNewPassword);
//        userRepository.save(currentUser);
//    }


//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public void approveUser(Long id) {
//        userRepository.findById(id).ifPresent(u -> {
//            u.setEnabled(true);
//            userRepository.save(u);
//        });
//    }
}