const socket = new WebSocket("ws://localhost:8285");
//const socket = new WebSocket('ws://83.94.37.212:6969');
console.log("started");

socket.onopen = () => {
    console.log("connection estaplished")
}

socket.onclose = () => {
    console.log("connection lost")
}
// Connection opened
socket.addEventListener('open', function (event) {
var send = "abcdef";
    socket.send(send);
    console.log(send);
});

socket.onmessage = ({ data }) => {
    console.log(data);
}

// Listen for messages
socket.addEventListener('message', function (event) {
    console.log('Message from server ', event.data);
});

//send username 
var username = document.getElementById("username");
//let username = document.querySelector("#username")
function send(){
console.log(username.value);
socket.send(JSON.stringify(new String(username.value)))
}
