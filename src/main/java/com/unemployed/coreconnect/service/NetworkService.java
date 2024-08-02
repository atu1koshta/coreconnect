package com.unemployed.coreconnect.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;

import org.springframework.stereotype.Service;

import com.unemployed.coreconnect.utils.Logging;

@Service
public class NetworkService implements Logging {

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
            e.printStackTrace();
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
}
