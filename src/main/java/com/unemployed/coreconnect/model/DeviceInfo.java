package com.unemployed.coreconnect.model;

public class DeviceInfo {
    private String ipAddress;
    private String deviceName;
    private long lastHeartbeat;

    public DeviceInfo(String ipAddress, String deviceName, long lastHeartbeat) {
        this.ipAddress = ipAddress;
        this.deviceName = deviceName;
        this.lastHeartbeat = lastHeartbeat;
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

    public boolean isOffline(long currentTimeMillis) {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastHeartbeat > 10000;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "ipAddress='" + ipAddress + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }
}
