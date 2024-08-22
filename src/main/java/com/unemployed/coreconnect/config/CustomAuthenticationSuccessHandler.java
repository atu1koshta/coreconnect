package com.unemployed.coreconnect.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.entity.Device;
import com.unemployed.coreconnect.service.NetworkService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private NetworkService networkService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();
        String ipAddress = networkService.getLocalIpAddressFromRequest(request);
        Device device = networkService.getDeviceFromIpAddress(ipAddress);

        log.info("User " + username + " has logged in from device " + device.getDeviceName());

        response.sendRedirect(Constant.View.CONVERSATION);
    }
}
