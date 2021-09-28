const websocket = new WebSocket("ws://localhost:8285");
console.log("started");
websocket.onopen = () => {
    console.log("connection estaplished")
}
websocket.onclose = () => {
    console.log("connection lost")
}
