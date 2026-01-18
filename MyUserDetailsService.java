package com.example.ccms_1.Services;

import com.example.ccms_1.Entities.Admin;
import com.example.ccms_1.Entities.User;
import com.example.ccms_1.Principals.AdminPrincipal;
import com.example.ccms_1.Principals.UserPrincipal;
import com.example.ccms_1.Repositories.AdminRepository;
import com.example.ccms_1.Repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public MyUserDetailsService(
            UserRepository userRepository,
            AdminRepository adminRepository
    ) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Admin admin = adminRepository.findByEmailId(email).orElse(null);
        if (admin != null) {
            return new AdminPrincipal(admin);
        }

        User user = userRepository.findByEmailId(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email)
                );

        return new UserPrincipal(user);
    }
}
