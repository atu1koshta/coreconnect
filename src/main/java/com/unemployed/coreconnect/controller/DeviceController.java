package com.unemployed.coreconnect.controller;

import java.io.IOException;
import java.net.UnknownHostException;

import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.UdpAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unemployed.coreconnect.exception.MacAddressNotFoundException;
import com.unemployed.coreconnect.exception.NotSameNetworkException;
import com.unemployed.coreconnect.model.Device;
import com.unemployed.coreconnect.service.DeviceService;
import com.unemployed.coreconnect.service.SnmpFetcher;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class DeviceController {
	@Autowired
    private DeviceService deviceService;

	@Autowired
	private SnmpFetcher snmpFetcher;
	
    @GetMapping
    public String handleRequest(HttpServletRequest request) throws UnknownHostException, NotSameNetworkException, MacAddressNotFoundException, IOException {
        String ipAddress = request.getRemoteAddr();
        ResponseEvent<UdpAddress> response = snmpFetcher.fetchSnmpData();
        String macAddress = snmpFetcher.getMacAddressForIp(response, ipAddress);
        
        Device device = deviceService.getDevice(macAddress);
        if (device != null) { 
        	return "Device already registered.";
        } else {
        	device = new Device(macAddress);
        	deviceService.saveDevice(device);
        }
        
        return String.format("Device(%s) registered successfully.", macAddress);
    }
}
