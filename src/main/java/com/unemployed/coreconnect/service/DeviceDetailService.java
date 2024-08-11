/*
 * This class is used to provide authentication mechanism to the platform
 */

package com.unemployed.coreconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DeviceDetailService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String macAddress) throws UsernameNotFoundException { 
        /*
         * Use device information as auth credentials and return User accordingly
         */

        return User.withUsername("user")
                    .password(passwordEncoder.encode("password"))
                    .roles("DEVICE")
                    .build();
    }
    
}
