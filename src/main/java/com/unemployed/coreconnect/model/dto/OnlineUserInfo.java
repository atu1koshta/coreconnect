package com.unemployed.coreconnect.model.dto;

import com.unemployed.coreconnect.constant.Constant;

public class OnlineUserInfo {
	private final String ipAddress;
	private final String username;
	private long lastHeartbeat;
	private byte noHeartbeatCount;
	private boolean isHeartbeatReceived;

	public OnlineUserInfo(String ipAddress, String username, long lastHeartbeat) {
		this.ipAddress = ipAddress;
		this.username = username;
		this.lastHeartbeat = lastHeartbeat;
		this.noHeartbeatCount = 0;
		this.isHeartbeatReceived = false;
		
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getUsername() {
		return username;
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
		return "DeviceInfo{" + "ipAddress='" + ipAddress + '\'' + ", username='" + username + '\'' + '}';
	}
}
