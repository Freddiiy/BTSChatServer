const messageContainer = document.getElementById('message-container')
const messageForm = document.getElementById('send-container')
const messageInput = document.getElementById('message-input')


const username = prompt
appendenMessage('you have joined')
socket.emit('new-user', username)

bsocket.on('chat-massages',data =>{
    appendenMessage(data.username, data.message)
})


bsocket.on('user-connected', username =>{
    appendenMessage(username, 'connected')
})

bsocket.on('user-disconnected', username =>{
    appendenMessage(username, 'disconnected')
})


messageForm.addEventListener('submit', e => {
    e.preventDefault()
    const message= messageInput.value
    appendenMessage('You: ${message}')
    bsocket.emit('send-chat-message',message)
    messageInput.value = ''
}) 
    function appendenMessage(message){
        const messageElement = document.createElement('div')
        messageElement.innerText = message
        messageContainer.append(messageElement)
    }
