package com.unemployed.coreconnect.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;

import com.unemployed.coreconnect.constant.Constant;
import com.unemployed.coreconnect.model.entity.Device;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NetworkService {

    private static final Logger log = LoggerFactory.getLogger(NetworkService.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SnmpFetcher snmpFetcher;

    public static String getGatewayIp() {
        String gatewayIp = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList("netstat", "-rn"));
            Process result = processBuilder.start();

            try (BufferedReader output = new BufferedReader(new InputStreamReader(result.getInputStream()))) {
                String line = output.readLine();
                while (line != null) {
                    if (line.contains("default")) {
                        gatewayIp = line.split("\\s+")[1];
                        break;
                    }
                    line = output.readLine();
                }
            }
        } catch (IOException e) {
            log.error(String.format("Error getting gateway IP: "), e.getMessage());
        }
        return gatewayIp;
    }

    public static String getHostLocalIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {

        }
        return null;
    }

    public Device getDeviceFromIpAddress(String ipAddress) throws IOException {
        String macAddress = snmpFetcher.getMacAddressForIp(ipAddress);

        return deviceService.checkAndRegisterDevice(macAddress);
    }

    public String getLocalIpAddressFromRequest(Object request) {
        String ipAddress = null;
        if (request instanceof HttpServletRequest httpServletRequest) {
            ipAddress = httpServletRequest.getRemoteAddr();
        } else if (request instanceof ServerHttpRequest serverHttpRequest) {
            InetSocketAddress remoteAddress = serverHttpRequest.getRemoteAddress();
            ipAddress = (remoteAddress != null) ? remoteAddress.getAddress().getHostAddress() : null;
        }

        if (ipAddress != null && ipAddress.equals(Constant.Network.LOCAL_IPV6)) {
            ipAddress = NetworkService.getHostLocalIp();
        }

        return ipAddress;
    }
}
