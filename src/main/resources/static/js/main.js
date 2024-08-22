(function () {
  const HEARTBEAT_INTERVAL = 5000;
  let stompClient = null;
  let heartbeatInterval;
  let selectedUser;

  const setConnected = (connected) => {
    document.getElementById("conversationDiv").style.visibility = connected
      ? "visible"
      : "hidden";
    document.getElementById("response").innerHTML = "";
  };

  const connect = () => {
    const socket = new SockJS("/chat");
    stompClient = Stomp.over(socket);

    stompClient.connect(
      {},
      (frame) => {
        sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];

        setConnected(true);
        console.log("Connected: " + frame);

        stompClient.subscribe("/topic/messages", (messageOutput) => {
          showMessageOutput(JSON.parse(messageOutput.body));
        });

        // Subscribe to the user's private message queue
        stompClient.subscribe("/user/queue/messages", (privateMessage) => {
          showMessageOutput(JSON.parse(privateMessage.body), true);
        });

        stompClient.subscribe("/topic/online-users", (users) => {
          showOnlineUsers(JSON.parse(users.body));
        });

        stompClient.subscribe("/app/online-users", (messageOutput) => {
          showOnlineUsers(JSON.parse(messageOutput.body));
        });

        heartbeatInterval = setInterval(sendHeartbeat, HEARTBEAT_INTERVAL);
      },
      (error) => {
        console.error("Connection error", error);
        setConnected(false);
      }
    );
  };

  const sendMessage = () => {
    const chatInput = document.getElementById("content");
    if (stompClient && stompClient.connected) {
      if (selectedUser) {
        stompClient.send(
          "/app/private-chat",
          {},
          JSON.stringify({
            recipient: selectedUser,
            message: chatInput.value,
          })
        );
      } else {
        stompClient.send("/app/chat", {}, chatInput.value);
      }
    } else {
      console.warn("Connection is not established. Message not sent.");
    }
  };

  const showMessageOutput = (messageOutput, private=false) => {
    const response = document.getElementById("response");
    const p = document.createElement("p");
    p.style.wordWrap = "break-word";
    p.appendChild(
      document.createTextNode(
        `${messageOutput.sender} [${messageOutput.time}${private ? "*" : ""}]: ${messageOutput.message}`
      )
    );
    response.appendChild(p);
    response.scrollTop = response.scrollHeight;
  };

  const showOnlineUsers = (users) => {
    const onlineDevicesList = document.getElementById("onlineDevicesList");
    onlineDevicesList.innerHTML = "";

    for (const key in users) {
      if (users.hasOwnProperty(key)) {
        const userElement = document.createElement("li");
        userElement.classList.add("list-group-item", "selectable-device");
        userElement.appendChild(document.createTextNode(users[key].username));
        onlineDevicesList.appendChild(userElement);

        // Add click event listener to open chat panel
        userElement.addEventListener("click", () => {
          selectedUser = users[key].username;
          highlightSelectedDevice(userElement);
          clearChat();
        });
      }
    }
  };

  const highlightSelectedDevice = (deviceElement) => {
    const allDevices = document.querySelectorAll(".selectable-device");
    allDevices.forEach((device) => device.classList.remove("active"));
    deviceElement.classList.add("active");
  };

  const clearChat = () => {
    const chatInput = document.getElementById("content");
    const response = document.getElementById("response");
    chatInput.value = "";
    response.innerHTML = "";
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
      });
    }
  };

  const toggleOnlineDevices = () => {
    const onlineDevices = document.getElementById("onlineDevices");
    onlineDevices.style.display =
      onlineDevices.style.display === "none" ? "block" : "none";
  };

  document.addEventListener("DOMContentLoaded", () => {
    connect();
  });

  // Export functions to global scope for buttons to access
  window.connect = connect;
  window.sendMessage = sendMessage;
  window.disconnect = disconnect;
  window.toggleOnlineDevices = toggleOnlineDevices;
})();
