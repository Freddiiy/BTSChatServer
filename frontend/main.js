const websocket = new WebSocket("ws://localhost:8285");
const users ={}

console.log("started");
websocket.onopen= ('new-user', username => {
    users[websocket.id] = username
    websocket.broadcast.emit('user-connected', username)
})
websocket.onopen = () => {
    console.log("connection estaplished")
    websocket.onopen('send-chat-message',message => {
        
        websocket.broadcast.emit('chat-message',{ message : message , username: users[websocket.id]})

    })

 
}

websocket.onopen= ('disconnect',()=> {
    websocket.broadcast.emit('user-disconnected', users[websocket.id])
delete users[websocket.id]    
})
websocket.onclose = () => {
    console.log("connection lost")
}
