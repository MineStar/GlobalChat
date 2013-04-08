package de.minestar.bungee.globalchat.core;

import java.util.HashMap;

public class MineServerContainer {

    private HashMap<String, MineServer> serverList;

    public MineServerContainer() {
        this.serverList = new HashMap<String, MineServer>();
    }

    public void addServer(MineServer server) {
        this.serverList.put(server.getName(), server);
        Core.log("registering server '" + server.getName() + "'");
    }

    public MineServer getServer(String serverName) {
        return this.serverList.get(serverName);
    }
}
