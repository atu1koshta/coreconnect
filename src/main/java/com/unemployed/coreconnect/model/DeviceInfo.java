package com.unemployed.coreconnect.model;

import com.unemployed.coreconnect.constant.Constant;

public class DeviceInfo {
	private final int id;
	private final String ipAddress;
	private final String deviceName;
	private long lastHeartbeat;
	private byte noHeartbeatCount;
	private boolean isHeartbeatReceived;

	public DeviceInfo(int id, String ipAddress, String deviceName, long lastHeartbeat) {
		this.id = id;
		this.ipAddress = ipAddress;
		this.deviceName = deviceName;
		this.lastHeartbeat = lastHeartbeat;
		this.noHeartbeatCount = 0;
		this.isHeartbeatReceived = false;
		
	}

	public int getId() {
		return this.id;
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
		return currentTimeMillis - lastHeartbeat > Constant.WebSocket.DEVICE_OFFLINE_THRESHOLD && noHeartbeatCount > Constant.WebSocket.DEVICE_NO_HEARTBEAT_COUNT_THRESHOLD;
	}

	@Override
	public String toString() {
		return "DeviceInfo{" + "ipAddress='" + ipAddress + '\'' + ", deviceName='" + deviceName + '\'' + '}';
	}
}
