package server;

public class MessageHandler {
    private final String messageFrom;
    private final String messageTo;
    private final String message;
    int id;

    public MessageHandler(String sender, String receiver, String message) {
        this.messageFrom = sender;
        this.messageTo = receiver;
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public String getMessage() {
        return message;
    }

    public MessageHandler getMessageData() {
        return this;
    }

    public String jsonStringify() {
        return
                "{" +
                "messageFrom: "+messageFrom+"," +
                "messageTo: "+messageTo+"," +
                "message: "+message+
                "}";
    }

    public int getId() {
        return id;
    }
}
