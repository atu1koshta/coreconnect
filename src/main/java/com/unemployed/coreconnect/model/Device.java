package com.unemployed.coreconnect.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import com.github.javafaker.Faker;

@Entity
public class Device {
	private static final Faker faker = new Faker();
	
	@Id
    private String macAddress;
    private String deviceName;
    
    public Device() {}
    
    public Device(String macAddress) {
    	this.macAddress = macAddress;
    	this.deviceName = faker.ancient().hero();
    }

	public String getMacAddress() {
		return macAddress;
	}

	public String getDeviceName() {
		return deviceName;
	}
}
