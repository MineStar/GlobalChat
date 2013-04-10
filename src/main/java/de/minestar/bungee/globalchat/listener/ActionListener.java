package de.minestar.bungee.globalchat.listener;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;

import com.google.common.eventbus.Subscribe;

import de.minestar.bungee.globalchat.core.ChatColor;
import de.minestar.bungee.globalchat.core.InventoryPacketHandler;
import de.minestar.bungee.globalchat.core.MineServer;
import de.minestar.bungee.globalchat.core.MineServerContainer;
import de.minestar.bungee.globalchat.core.PlayerManager;
import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.packets.InventoryDataPacket;
import de.minestar.protocol.newpackets.packets.InventoryRequestPacket;

public class ActionListener implements Listener {

    private MineServerContainer container;
    private InventoryPacketHandler inventoryPacketHandler;
    private PlayerManager playerManager;

    public ActionListener(InventoryPacketHandler inventoryPacketHandler, PlayerManager playerManager) {
        this.container = new MineServerContainer();
        this.inventoryPacketHandler = inventoryPacketHandler;
        this.playerManager = playerManager;
        this.loadServers();
    }

    private void loadServers() {
        Map<String, ServerInfo> map = ProxyServer.getInstance().getServers();
        for (ServerInfo serverInfo : map.values()) {
            if (serverInfo.getName().equalsIgnoreCase("main")) {
                this.container.addServer(new MineServer(serverInfo.getName(), ChatColor.GREEN));
            } else if (serverInfo.getName().equalsIgnoreCase("res")) {
                this.container.addServer(new MineServer(serverInfo.getName(), ChatColor.YELLOW));
            } else if (serverInfo.getName().equalsIgnoreCase("survival")) {
                this.container.addServer(new MineServer(serverInfo.getName(), ChatColor.RED));
            }
        }
    }

    @Subscribe
    public void onChat(ChatEvent event) {
        // return?
        if (event.isCancelled() || event.getSender() == null || !(event.getSender() instanceof ProxiedPlayer) || event.getMessage().startsWith("/")) {
            return;
        }

        // get the sender
        ProxiedPlayer sender = (ProxiedPlayer) event.getSender();

        // get the server
        MineServer server = this.container.getServer(sender.getServer().getInfo().getName());

        if (server != null) {
            // build message
            String message = server.buildMessage(sender.getDisplayName(), event.getMessage());

            // iterate over the playerlist
            Collection<ProxiedPlayer> playerList = ProxyServer.getInstance().getPlayers();
            for (ProxiedPlayer player : playerList) {
                // send only, if the server is different
                if (sender.getServer().getInfo().getName().equalsIgnoreCase(player.getServer().getInfo().getName())) {
                    continue;
                }
                // send message
                player.sendMessage(message);
            }
        }
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        System.out.println("ServerConnectedEvent");
        this.playerManager.updatePlayer(event.getPlayer(), event.getServer().getInfo());
    }

    @Subscribe
    public void onServerKick(ServerKickEvent event) {
        System.out.println("ServerKickEvent");
        this.playerManager.removeInventory(event.getPlayer());
        this.playerManager.removePlayer(event.getPlayer());
    }

    @Subscribe
    public void onDisconnect(PlayerDisconnectEvent event) {
        System.out.println("PlayerDisconnectEvent");
        this.playerManager.removeInventory(event.getPlayer());
        this.playerManager.removePlayer(event.getPlayer());
    }

    public static ServerInfo getServerByAdress(InetSocketAddress adress) {
        for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            if (info.getAddress().equals(adress)) {
                return info;
            }
        }
        return null;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        // correct channel
        if (!event.getTag().equalsIgnoreCase(this.inventoryPacketHandler.getChannel())) {
            System.out.println("--------------------------------");
            System.out.println("wrong channel: " + event.getTag());
            return;
        }

        // get packet
        System.out.println("----------------------");
        System.out.println("received package...");
        NetworkPacket packet = this.inventoryPacketHandler.extractPacket(event.getData());
        if (packet != null) {
            System.out.println("PACKET: " + packet.getType());
            switch (packet.getType()) {
                case INVENTORY_REQUEST : {
                    this.handleInventoryRequestPacket(event.getSender().getAddress(), (InventoryRequestPacket) packet);
                    break;
                }
                case INVENTORY_DATA : {
                    this.handleInventoryDataPacket((InventoryDataPacket) packet);
                    break;
                }
                default : {
                    break;
                }
            }
        } else {
            System.out.println("invalid packet!");
        }
    }

    private void handleInventoryDataPacket(InventoryDataPacket packet) {
        System.out.println("INVENTORY_DATA received from player: " + packet.getPlayerName());
        this.playerManager.addInventory(packet.getPlayerName(), packet.getData());
    }

    private void handleInventoryRequestPacket(InetSocketAddress adress, InventoryRequestPacket packet) {
        System.out.println("INVENTORY_REQUEST from player: " + packet.getPlayerName());

        if (this.playerManager.isConnected(packet.getPlayerName()) && this.playerManager.hasInventory(packet.getPlayerName())) {
            System.out.println("player has inventory stored!");
            ServerInfo server = getServerByAdress(adress);
            if (server != null) {
                InventoryDataPacket answerPacket = new InventoryDataPacket(packet.getPlayerName(), this.playerManager.getInventory(packet.getPlayerName()));
                this.inventoryPacketHandler.send(answerPacket, server, this.inventoryPacketHandler.getChannel());
                this.playerManager.removeInventory(packet.getPlayerName());
            }
        } else {
            System.out.println("no inventory for player stored or player offline!");
        }
    }
}
