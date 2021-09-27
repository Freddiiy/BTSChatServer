package compliant;

import java.util.concurrent.CopyOnWriteArrayList;

public class OnlineList {
    private CopyOnWriteArrayList<ClientHandler> onlineList = new CopyOnWriteArrayList<>();

    public void add(ClientHandler client) {
        onlineList.add(client);
    }

    public void remove() {
//        onlineList.remove(1);
    }

    public String getOnlineUsers() {
        String onlineUsers = "";
        boolean firstIter = true;
        try {
            for (ClientHandler clientHandler : onlineList) {
                if (!firstIter) {
                    onlineUsers += ", " + clientHandler.getClientName();
                } else {
                    onlineUsers += clientHandler.getClientName();
                }
                firstIter = false;
            }
            onlineUsers += ".";
        } catch (NullPointerException e) {
            return "None.";
        }
        return onlineUsers;
    }

    //TODO: Make immutable
    public CopyOnWriteArrayList<ClientHandler> getOnlineList() {
        return onlineList;
    }

}
