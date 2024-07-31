package com.unemployed.coreconnect.model;

import com.unemployed.coreconnect.constant.Constant;

public class DeviceInfo {
	private String ipAddress;
	private String deviceName;
	private long lastHeartbeat;
	private byte noHeartbeatCount;
	private boolean isHeartbeatReceived;

	public DeviceInfo(String ipAddress, String deviceName, long lastHeartbeat) {
		this.ipAddress = ipAddress;
		this.deviceName = deviceName;
		this.lastHeartbeat = lastHeartbeat;
		this.noHeartbeatCount = 0;
		this.isHeartbeatReceived = false;
		
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setLastHeartbeat(long lastHeartbeat) {
		this.lastHeartbeat = lastHeartbeat;
	}

	public void incrementNoHeartbeatCount() {
		noHeartbeatCount++;
	}
	
	public void resetNoHeartbeatCount() {
		noHeartbeatCount = 0;
	}
	
	public boolean isHeartbeatReceived() {
		return isHeartbeatReceived;
	}
	
	public synchronized void setHeartbeatReceived(boolean isHeartbeatReceived) {
		this.isHeartbeatReceived = isHeartbeatReceived;
	}

	public boolean isOffline(long currentTimeMillis) {
		return currentTimeMillis - lastHeartbeat > Constant.WebSocket.DEVICE_OFFLINE_THRESHOLD && noHeartbeatCount > Constant.WebSocket.DEVICE_OFFLINE_THRESHOLD_COUNT;
	}

	@Override
	public String toString() {
		return "DeviceInfo{" + "ipAddress='" + ipAddress + '\'' + ", deviceName='" + deviceName + '\'' + '}';
	}
}
