package com.unemployed.coreconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unemployed.coreconnect.model.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
	Device findByMacAddress(String macAddress);
}
