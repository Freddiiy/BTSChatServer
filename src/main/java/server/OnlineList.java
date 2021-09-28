package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OnlineList {
    private static CopyOnWriteArrayList<ClientHandler> onlineList = new CopyOnWriteArrayList<>();

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
                if (firstIter) {
                    onlineUsers += clientHandler.getClientName();
                    firstIter = false;
                } else {
                    onlineUsers += ", " + clientHandler.getClientName();
                }
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
