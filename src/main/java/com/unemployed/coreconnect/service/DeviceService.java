package com.unemployed.coreconnect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

	public Pair<Boolean, String> checkAndRegisterDevice(String macAddress) {
		Device device = getDevice(macAddress);
		
		if (device != null) {
			return Pair.of(true, device.getDeviceName());
		} else {
			device = new Device(macAddress);
			device = saveDevice(device);
			if(device != null) {
				return Pair.of(true, String.format("Welcome + %s", device.getDeviceName()));
			}
			return Pair.of(false, "Device registration failed.");
		}
	}
}
