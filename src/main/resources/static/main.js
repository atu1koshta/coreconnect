var stompClient = null;
var heartbeatInterval;

function setConnected(connected) {
	document.getElementById("connect").disabled = connected;
	document.getElementById("disconnect").disabled = !connected;
	document.getElementById("conversationDiv").style.visibility = connected
		? "visible"
		: "hidden";
	document.getElementById("response").innerHTML = "";
}

function connect() {
	var socket = new SockJS("/chat");
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log("Connected: " + frame);
		stompClient.subscribe("/topic/messages", function(messageOutput) {
			showMessageOutput(JSON.parse(messageOutput.body));
		});
		stompClient.subscribe(
			"/topic/online-devices",
			function(messageOutput) {
				updateOnlineDevices(JSON.parse(messageOutput.body));
			}
		);

		// Start sending heartbeat messages
		heartbeatInterval = setInterval(sendHeartbeat, 30000); // Send heartbeat every 30 seconds
	});
}

function sendMessage() {
	var content = document.getElementById("content").value;
	stompClient.send(
		"/app/chat",
		{},
		content);
}

function showMessageOutput(messageOutput) {
	var response = document.getElementById("response");
	var p = document.createElement("p");
	p.style.wordWrap = "break-word";
	p.appendChild(
		document.createTextNode(
			messageOutput.sender +
			" [" +
			messageOutput.time +
			"]: " +
			messageOutput.message
		)
	);
	response.appendChild(p);

	response.scrollTop = response.scrollHeight;
}

function updateOnlineDevices(devices) {
	var onlineDevicesList = document.getElementById("onlineDevicesList");
	onlineDevicesList.innerHTML = "";

	for (var key in devices) {
		var deviceElement = document.createElement("li");
		deviceElement.className = "list-group-item";
		deviceElement.appendChild(
			document.createTextNode(devices[key].ipAddress + " - " + devices[key].deviceName)
		);
		onlineDevicesList.appendChild(deviceElement);
	}
}

function sendHeartbeat() {
	if (stompClient != null && stompClient.connected) {
		stompClient.send("/app/heartbeat", {}, "")
	}
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");

	clearInterval(heartbeatInterval);
}

function toggleOnlineDevices() {
	var onlineDevices = document.getElementById("onlineDevices");
	if (onlineDevices.style.display === "none") {
		onlineDevices.style.display = "block";
	} else {
		onlineDevices.style.display = "none";
	}
}