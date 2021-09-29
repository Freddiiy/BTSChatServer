//const socket = new WebSocket("ws://localhost:8285");
const socket = new WebSocket('ws://localhost:8285');
console.log("started");

socket.onopen = () => {
    console.log("connection estaplished")
    socket.send('ping');
}

socket.onclose = () => {
    console.log("connection lost")
}
// Connection opened
socket.addEventListener('open', function (event) {
    socket.send('Hello Server!');
});

// Listen for messages
socket.addEventListener('message', function (event) {
    console.log('Message from server ', event.data);
});