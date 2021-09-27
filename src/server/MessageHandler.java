package server;

public class MessageHandler {
    private final String messageFrom;
    private final String messageTo;
    private final String message;
    int id;

    public MessageHandler(String messageFrom, String messageTo, String message) {
        this.messageFrom = messageFrom;
        this.messageTo = messageTo;
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

    public int getId() {
        return id;
    }
}
