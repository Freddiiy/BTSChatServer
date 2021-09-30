const socket = new WebSocket("ws://localhost:8285");

console.log("started");

socket.onopen = () => {
    console.log("connection estaplished")
    var send = "abcdef";
    socket.send(send);
    console.log(send);
}

socket.onclose = () => {
    console.log("connection lost")
}
// Connection opened
socket.addEventListener('open', function (event) {
});

socket.onmessage = ({ data }) => {
    console.log(data);
}

// Listen for messages
socket.addEventListener('message', function (event) {
    console.log('Message from server ', event.data);
});
