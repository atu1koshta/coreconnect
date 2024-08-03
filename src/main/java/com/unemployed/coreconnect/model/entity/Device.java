package com.unemployed.coreconnect.model.entity;

import com.github.javafaker.Faker;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Device {
	private static final Faker faker = new Faker();
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
    private String macAddress;
    private String deviceName;
    
    public Device() {}
    
    public Device(String macAddress) {
    	this.macAddress = macAddress;
    	this.deviceName = faker.ancient().hero();
    }
    
    public Integer getId() {
		return id;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public String getDeviceName() {
		return deviceName;
	}
}
