(function() {
	const HEARTBEAT_INTERVAL = 5000;
	let stompClient = null;
	let heartbeatInterval;
	let sessionId = localStorage.getItem('sessionId');

	const setConnected = (connected) => {
		document.getElementById("connect").disabled = connected;
		document.getElementById("disconnect").disabled = !connected;
		document.getElementById("conversationDiv").style.visibility = connected ? "visible" : "hidden";
		document.getElementById("response").innerHTML = "";
	};

	const connect = () => {
		const socket = new SockJS("/chat");
		stompClient = Stomp.over(socket);

		stompClient.connect({}, (frame) => {
			sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
			localStorage.setItem("sessionId", sessionId);

			setConnected(true);
			console.log("Connected: " + frame);

			stompClient.subscribe("/topic/messages", (messageOutput) => {
				showMessageOutput(JSON.parse(messageOutput.body));
			});
			stompClient.subscribe("/topic/online-devices", (messageOutput) => {
				showOnlineDevices(JSON.parse(messageOutput.body));
			});

			heartbeatInterval = setInterval(sendHeartbeat, HEARTBEAT_INTERVAL);
		}, (error) => {
			console.error('Connection error', error);
			setConnected(false);
		});
	};

	const sendMessage = () => {
		const content = document.getElementById("content").value;
		if (stompClient && stompClient.connected) {
			stompClient.send("/app/chat", {}, content);
		} else {
			console.warn('Connection is not established. Message not sent.');
		}
	};

	const showMessageOutput = (messageOutput) => {
		const response = document.getElementById("response");
		const p = document.createElement("p");
		p.style.wordWrap = "break-word";
		p.appendChild(
			document.createTextNode(`${messageOutput.sender} [${messageOutput.time}]: ${messageOutput.message}`)
		);
		response.appendChild(p);
		response.scrollTop = response.scrollHeight;
	};

	const showOnlineDevices = (devices) => {
		const onlineDevicesList = document.getElementById("onlineDevicesList");
		onlineDevicesList.innerHTML = "";

		for (const key in devices) {
			if (devices.hasOwnProperty(key)) {
				const deviceElement = document.createElement("li");
				deviceElement.classList.add("list-group-item", "selectable-device");
				deviceElement.appendChild(
					document.createTextNode(`${devices[key].ipAddress} - ${devices[key].deviceName}`)
				);
				onlineDevicesList.appendChild(deviceElement);
			}
		}
	};

	const sendHeartbeat = () => {
		if (stompClient && stompClient.connected) {
			stompClient.send("/app/heartbeat", {}, "");
		}
	};

	const disconnect = () => {
		if (stompClient) {
			stompClient.disconnect(() => {
				setConnected(false);
				console.log("Disconnected");
				clearInterval(heartbeatInterval);
				localStorage.removeItem("sessionId");
			});
		}
	};

	const toggleOnlineDevices = () => {
		const onlineDevices = document.getElementById("onlineDevices");
		onlineDevices.style.display = (onlineDevices.style.display === "none") ? "block" : "none";
	};

	document.addEventListener("DOMContentLoaded", () => {
		sessionId = localStorage.getItem('sessionId');
		if (sessionId) {
			console.log("SESSION EXISTS: " + sessionId);
			connect();
		}
	});

	// Export functions to global scope for buttons to access
	window.connect = connect;
	window.sendMessage = sendMessage;
	window.disconnect = disconnect;
	window.toggleOnlineDevices = toggleOnlineDevices;
})();
