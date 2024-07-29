package com.unemployed.coreconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unemployed.coreconnect.model.Device;
import com.unemployed.coreconnect.repository.DeviceRepository;

@Service
public class DeviceService {

	@Autowired
	private DeviceRepository deviceRepository;

	public Device saveDevice(Device device) {
		return deviceRepository.save(device);
	}

	public Device getDevice(String macAddress) {
		return deviceRepository.findByMacAddress(macAddress);
	}

	public Device checkAndRegisterDevice(String macAddress) {
		Device device = getDevice(macAddress);
		
		if (device != null) {
			return device;
		} else {
			device = new Device(macAddress);
			device = saveDevice(device);
			if(device != null) {
				return device;
			}
			return null;
		}
	}
}
