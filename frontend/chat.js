
const messageContainer = document.getElementById('message-container')
const messageForm = document.getElementById('send-container')
const messageInput = document.getElementById('message-input')


const username = prompt
appendenMessage('you have joined')
websocket.emit('new-user', username)

websocket.on('chat-massages',data =>{
    appendenMessage(data.username, data.message)
})


websocket.on('user-connected', username =>{
    appendenMessage(username, 'connected')
})

websocket.on('user-disconnected', username =>{
    appendenMessage(username, 'disconnected')
})


messageForm.addEventListener('submit', e => {
    e.preventDefault()
    const message= messageInput.value
    appendenMessage('You: ${message}')
    websocket.emit('send-chat-message',message)
    messageInput.value = ''
}) 
    function appendenMessage(message){
        const messageElement = document.createElement('div')
        messageElement.innerText = message
        messageContainer.append(messageElement)
    }
