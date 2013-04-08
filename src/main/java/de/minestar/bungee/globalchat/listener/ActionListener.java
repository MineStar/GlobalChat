package de.minestar.bungee.globalchat.listener;

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
import com.sun.xml.internal.ws.api.message.Packet;

import de.minestar.bungee.globalchat.core.ChatColor;
import de.minestar.bungee.globalchat.core.Core;
import de.minestar.bungee.globalchat.core.MineServer;
import de.minestar.bungee.globalchat.core.MineServerContainer;
import de.minestar.bungee.globalchat.core.PlayerManager;
import de.minestar.protocol.newpackets.MultiPacket;
import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketHandler;
import de.minestar.protocol.newpackets.PacketType;
import de.minestar.protocol.newpackets.packets.InventoryRequestPackage;

public class ActionListener implements Listener {

    private MineServerContainer container;
    private PlayerManager playerManager;

    public ActionListener(PlayerManager playerManager) {
        this.container = new MineServerContainer();
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

        // send testpackage
        this.sendTestPackages(sender, server, server.buildMessage(sender.getDisplayName(), event.getMessage()));

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

    private void sendTestPackages(ProxiedPlayer sender, MineServer server, String message) {
    }

    // private void sendPackage(ProxiedPlayer sender, MineServer server, MultiPacket packet) {
    // try {
    // String channelName = "globalchat";
    // sender.getServer().sendData(channelName, packet.getByteOutputStream().toByteArray());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event) {
        System.out.println("ServerConnectedEvent");
        // if (this.playerManager.updatePlayer(event.getPlayer()) &&
        // this.playerManager.hasInventory(event.getPlayer())) {
        // // TODO: update the inventory with the old one!
        // try {
        // System.out.println("Sending inventory...");
        // // Packet packet = Packet.createPackage("Forward",
        // event.getServer().getInfo().getName(), PacketType.INVENTORY_SEND,
        // this.playerManager.getInventory(event.getPlayer()));
        //
        // // get the sender
        // ProxiedPlayer sender = event.getPlayer();
        //
        // // get the server
        // MineServer server =
        // this.container.getServer(event.getServer().getInfo().getName());
        // // this.sendPackage(sender, server, packet);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
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

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        // correct channel
        if (!event.getTag().equalsIgnoreCase(Core.INSTANCE.NAME)) {
            return;
        }

        // get packet
        NetworkPacket packet = PacketHandler.INSTANCE.extractPacket(event.getData());
        if (packet != null) {
            System.out.println("Type: " + packet.getType());
            switch (packet.getType()) {
                case INVENTORY_REQUEST : {
                    this.handleInventoryRequest((InventoryRequestPackage) packet);
                    break;
                }
                default : {
                    break;
                }
            }
        } else {
            System.out.println("TYPE IS UNKNOWN");
        }
    }

    private void handleInventoryRequest(InventoryRequestPackage packet) {
    }
}
