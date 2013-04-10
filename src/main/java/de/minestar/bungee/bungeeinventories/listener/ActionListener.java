package de.minestar.bungee.bungeeinventories.listener;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;

import com.google.common.eventbus.Subscribe;

import de.minestar.bungee.bungeeinventories.data.ChatColor;
import de.minestar.bungee.bungeeinventories.data.DataPacketHandler;
import de.minestar.bungee.bungeeinventories.data.MineServer;
import de.minestar.bungee.bungeeinventories.data.MineServerContainer;
import de.minestar.bungee.bungeeinventories.data.PlayerManager;
import de.minestar.bungee.bungeeinventories.protocol.NetworkPacket;
import de.minestar.bungee.bungeeinventories.protocol.packets.DataOKPacket;
import de.minestar.bungee.bungeeinventories.protocol.packets.DataRequestPacket;
import de.minestar.bungee.bungeeinventories.protocol.packets.DataSendPacket;
import de.minestar.bungee.bungeeinventories.protocol.packets.ServerchangeDenyPacket;
import de.minestar.bungee.bungeeinventories.protocol.packets.ServerchangeOKPacket;
import de.minestar.bungee.bungeeinventories.protocol.packets.ServerchangeRequestPacket;

public class ActionListener implements Listener {

    // /////////////////////////////////////////
    //
    // STATIC-Methods
    //
    // /////////////////////////////////////////

    public static ServerInfo getServerByAdress(InetSocketAddress adress) {
        for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
            if (info.getAddress().equals(adress)) {
                return info;
            }
        }
        return null;
    }

    // /////////////////////////////////////////
    //
    // CLASS-Methods
    //
    // /////////////////////////////////////////

    private DataPacketHandler dataPacketHandler;
    private MineServerContainer container;
    private PlayerManager playerManager;

    public ActionListener(DataPacketHandler dataPacketHandler, PlayerManager playerManager) {
        this.dataPacketHandler = dataPacketHandler;
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
    public void onPluginMessage(PluginMessageEvent event) {
        // correct channel
        if (!event.getTag().equalsIgnoreCase(this.dataPacketHandler.getChannel())) {
            return;
        }

        // get packet
        NetworkPacket packet = this.dataPacketHandler.extractPacket(event.getData());
        if (packet != null) {
            ServerInfo serverInfo = getServerByAdress(event.getSender().getAddress());
            if (serverInfo != null) {
                switch (packet.getType()) {
                    case SERVERCHANGE_REQUEST : {
                        this.handleServerchangeRequest(serverInfo, (ServerchangeRequestPacket) packet);
                        break;
                    }
                    case DATA_SEND : {
                        this.handleDataSend(serverInfo, (DataSendPacket) packet);
                        break;
                    }
                    case DATA_REQUEST : {
                        this.handleDataRequest(serverInfo, (DataRequestPacket) packet);
                        break;
                    }
                    case DATA_OK : {
                        this.handleDataOK(serverInfo, (DataOKPacket) packet);
                        break;
                    }
                    default : {
                        break;
                    }
                }
            } else {
                System.out.println("ERROR: Server not found!");
            }
        } else {
            System.out.println("ERROR: Invalid packet received!");
        }
    }

    private void handleDataOK(ServerInfo serverInfo, DataOKPacket packet) {
        this.playerManager.removeInventory(packet.getPlayerName());
    }

    private void handleDataRequest(ServerInfo serverInfo, DataRequestPacket packet) {
        if (this.playerManager.hasInventory(packet.getPlayerName())) {
            DataSendPacket answer = new DataSendPacket(packet.getPlayerName(), serverInfo.getName(), this.playerManager.getInventory(packet.getPlayerName()));
            this.dataPacketHandler.send(answer, serverInfo, this.dataPacketHandler.getChannel());
        }
    }

    private void handleDataSend(ServerInfo serverInfo, DataSendPacket packet) {
        this.playerManager.addInventory(packet.getPlayerName(), packet.getData());

        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(packet.getPlayerName());
        ServerInfo toServer = ProxyServer.getInstance().getServerInfo(packet.getServerName());
        if (player != null && toServer != null) {
            // teleport player to another server
            player.connect(toServer);
        }
    }

    private void handleServerchangeRequest(ServerInfo serverInfo, ServerchangeRequestPacket packet) {
        MineServer server = this.container.getServer(packet.getServerName());
        if (server == null) {
            // send ServerchangeDenyPacket
            ServerchangeDenyPacket answer = new ServerchangeDenyPacket(packet.getPlayerName(), "Server '" + packet.getServerName() + "' not found!");
            this.dataPacketHandler.send(answer, serverInfo, this.dataPacketHandler.getChannel());
        } else {
            // server must be != null (online-check?)
            ServerInfo toServer = ProxyServer.getInstance().getServerInfo(packet.getServerName());
            if (toServer == null) {
                ServerchangeDenyPacket answer = new ServerchangeDenyPacket(packet.getPlayerName(), "Server '" + packet.getServerName() + "' not found!");
                this.dataPacketHandler.send(answer, serverInfo, this.dataPacketHandler.getChannel());
                return;
            }

            // servers must be different
            if (toServer.getName().equalsIgnoreCase(serverInfo.getName())) {
                ServerchangeDenyPacket answer = new ServerchangeDenyPacket(packet.getPlayerName(), "You are already connected to '" + packet.getServerName() + "'!");
                this.dataPacketHandler.send(answer, serverInfo, this.dataPacketHandler.getChannel());
                return;
            }

            // send ServerchangeOKPacket
            ServerchangeOKPacket answer = new ServerchangeOKPacket(packet.getPlayerName(), packet.getServerName(), "Connecting to server '" + packet.getServerName() + "'...");
            this.dataPacketHandler.send(answer, serverInfo, this.dataPacketHandler.getChannel());
        }
    }
}
