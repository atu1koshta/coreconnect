package com.unemployed.coreconnect.constant;

public class Constant {
	public static class Network {
		public static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
	}

	public static class WebSocket {
		public static final String TOPIC = "/topic";
		public static final String QUEUE = "/queue";
		public static final String APP = "/app";
		public static final String USER = "/user";
		public static final String CHAT = "/chat";
		public static final String PRIVATE_CHAT = "/private-chat";
		public static final String ONLINE_USERS = "/topic/online-users";
		public static final String INITIAL_ONLINE_USERS = "/online-users";
		public static final String MESSAGES = "/topic/messages";
		public static final String HEARTBEAT = "/heartbeat";
		
		public static final int HEARTBEAT_INTERVAL = 5000;
		public static final int DEVICE_OFFLINE_THRESHOLD = 15000;
		public static final int DEVICE_NO_HEARTBEAT_COUNT_THRESHOLD = 3;
	}

	public static class View {
		public static final String LOGIN = "/login";
		public static final String CONVERSATION = "/conversation";
	}
}
