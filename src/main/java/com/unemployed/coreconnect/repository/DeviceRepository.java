package com.unemployed.coreconnect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unemployed.coreconnect.model.entity.Device;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
	Device findByMacAddress(String macAddress);
}
