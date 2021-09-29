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
