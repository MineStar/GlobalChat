package de.minestar.bungee.bungeeinventories.manager;

import java.net.InetSocketAddress;
import java.util.HashMap;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerManager {
    private HashMap<String, ServerInfo> playerMap = new HashMap<String, ServerInfo>();
    private HashMap<InetSocketAddress, String> playerConnections = new HashMap<InetSocketAddress, String>();
    private HashMap<String, byte[]> inventoryMap = new HashMap<String, byte[]>();

    /**
     * Add a inventory of a player
     * 
     * @param player
     * @return <b>true</b> if the player already had an inventory bungeeserver, otherwise <b>false</b>
     */
    public boolean addInventory(ProxiedPlayer player, byte[] byteArray) {
        return this.addInventory(player.getName(), byteArray);
    }

    public String getPlayerNameByAdress(InetSocketAddress adress) {
        return this.playerConnections.get(adress);
    }

    /**
     * Add a inventory of a player
     * 
     * @param player
     * @return <b>true</b> if the player already had an inventory bungeeserver, otherwise <b>false</b>
     */
    public boolean addInventory(String playerName, byte[] byteArray) {
        boolean hadInventory = this.hasInventory(playerName);
        this.inventoryMap.put(playerName, byteArray);
        return hadInventory;
    }

    /**
     * Check if the player has a stored inventory
     * 
     * @param player
     * @return <b>true</b> if the player has an inventory, otherwise <b>false</b>
     */
    public boolean hasInventory(ProxiedPlayer player) {
        return this.hasInventory(player.getName());
    }

    /**
     * Check if the player has a stored inventory
     * 
     * @param player
     * @return <b>true</b> if the player has an inventory, otherwise <b>false</b>
     */
    public boolean hasInventory(String playerName) {
        return this.inventoryMap.containsKey(playerName);
    }

    public byte[] getInventory(ProxiedPlayer player) {
        return this.getInventory(player.getName());
    }

    public byte[] getInventory(String playerName) {
        return this.inventoryMap.get(playerName);
    }

    public void removeInventory(ProxiedPlayer player) {
        this.removeInventory(player.getName());
    }

    public void removeInventory(String playerName) {
        this.inventoryMap.remove(playerName);
    }

    /**
     * Add a player to the playermap
     * 
     * @param player
     * @return <b>true</b> if the player was connected on another bungeeserver, otherwise <b>false</b>
     */
    public boolean updatePlayer(ProxiedPlayer player, ServerInfo serverInfo) {
        boolean wasConnected = this.isConnected(player);
        this.playerMap.put(player.getName(), serverInfo);
        this.playerConnections.put(player.getAddress(), player.getName());
        return wasConnected;
    }

    public ServerInfo getServerInfo(ProxiedPlayer player) {
        return this.getServerInfo(player.getName());
    }

    public ServerInfo getServerInfo(String playerName) {
        return this.playerMap.get(playerName);
    }

    public void removePlayer(ProxiedPlayer player) {
        this.playerConnections.remove(player.getAddress());
        this.playerMap.remove(player.getName());
    }

    /**
     * Check if a player is connected on another bungeeserver
     * 
     * @param player
     * @return <b>true</b> if the player is connected on another bungeeserver, otherwise <b>false</b>
     */
    public boolean isConnected(ProxiedPlayer player) {
        return this.isConnected(player.getName());
    }

    /**
     * Check if a player is connected on another bungeeserver
     * 
     * @param player
     * @return <b>true</b> if the player is connected on another bungeeserver, otherwise <b>false</b>
     */
    public boolean isConnected(String playerName) {
        return this.playerMap.containsKey(playerName);
    }
}
