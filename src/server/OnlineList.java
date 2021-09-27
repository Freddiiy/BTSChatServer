package server;

import java.util.concurrent.CopyOnWriteArrayList;

public class OnlineList {
    private static CopyOnWriteArrayList<ClientHandler> onlineList;

    public void add(ClientHandler client) {
        onlineList.add(client);
    }

    public void remove() {
//        onlineList.remove(1);
    }
}
