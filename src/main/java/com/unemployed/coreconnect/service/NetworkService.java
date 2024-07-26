package com.unemployed.coreconnect.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.springframework.stereotype.Service;

@Service
public class NetworkService {
	public static String getGatewayIp() {
		String gatewayIp = null;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(Arrays.asList("netstat", "-rn"));
			Process result = processBuilder.start();

			BufferedReader output = new BufferedReader(new InputStreamReader(result.getInputStream()));

			String line = output.readLine();
			while (line != null) {
				if (line.contains("default")) {
					gatewayIp = line.split("\\s+")[1];
					break;
				}
				line = output.readLine();
			}
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gatewayIp;
	}
}
