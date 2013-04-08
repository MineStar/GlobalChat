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

import de.minestar.bungee.globalchat.core.ChatColor;
import de.minestar.bungee.globalchat.core.MineServer;
import de.minestar.bungee.globalchat.core.MineServerContainer;
import de.minestar.bungee.globalchat.core.PlayerManager;
import de.minestar.protocol.packets.MultiPacket;
import de.minestar.protocol.packets.Packet;
import de.minestar.protocol.packets.PacketType;

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
        try {
            String channelName = "globalchat";

            // message
            MultiPacket multiPacket = new MultiPacket("Forward", "ALL", PacketType.MULTIPACKET);
            multiPacket.addPacket(Packet.createPackage(PacketType.CHAT, "PAKET 1"));
            multiPacket.addPacket(Packet.createPackage(PacketType.CHAT, message));
            multiPacket.addPacket(Packet.createPackage(PacketType.CHAT, "PAKET 3"));
            multiPacket.addPacket(Packet.createPackage(PacketType.COMMAND, "i stone"));
            sender.getServer().sendData(channelName, multiPacket.getByteOutputStream().toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void sendPackage(ProxiedPlayer sender, MineServer server, MultiPacket packet) {
//        try {
//            String channelName = "globalchat";
//            sender.getServer().sendData(channelName, packet.getByteOutputStream().toByteArray());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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
        System.out.println("PluginMessageEvent");
        System.out.println("length: " + event.getData().length);
        MultiPacket multiPacket = MultiPacket.readPackage(event.getData());
        if (multiPacket != null) {
            System.out.println("Type: " + multiPacket.getPacketType());
            switch (multiPacket.getPacketType()) {
                case INVENTORY_SAVE : {
                    this.handleInventorySave(multiPacket);
                    break;
                }
                case INVENTORY_REQUEST : {
                    this.handleInventoryRequest(multiPacket);
                    break;
                }
                default : {
                    break;
                }
            }
        } else {
            System.out.println("MultiPackage IS NULL");
        }
    }

    private void handleInventorySave(MultiPacket multiPacket) {
        try {
            System.out.println("saving inventory...");

            // handle playerpacket
            Packet playerPacket = multiPacket.getPacketList().get(0);
            String playerName = new String(playerPacket.getData(), "UTF-8");

            // handle inventorypacket
            Packet invPacket = multiPacket.getPacketList().get(1);
            this.playerManager.addInventory(playerName, invPacket.getData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleInventoryRequest(MultiPacket multiPacket) {
        try {
            // handle playerpacket
            Packet playerPacket = multiPacket.getPacketList().get(0);
            String playerName = new String(playerPacket.getData(), "UTF-8");

            System.out.println("inventory request => sending inventory");
            if (this.playerManager.hasInventory(playerName)) {
                MultiPacket sendPacket = new MultiPacket("Forward", "ALL", PacketType.INVENTORY_LOAD);
                sendPacket.addPacket(Packet.createPackage(PacketType.PLAYERNAME, playerName));
                sendPacket.addPacket(Packet.createPackage(PacketType.INVENTORY_LOAD, this.playerManager.getInventory(playerName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
