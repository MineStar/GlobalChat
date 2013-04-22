package de.minestar.bungee.bungeeinventories.listener;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;
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

import de.minestar.bungee.bungeeinventories.data.DataPacketHandler;
import de.minestar.bungee.bungeeinventories.data.MineServer;
import de.minestar.bungee.bungeeinventories.manager.MineServerManager;
import de.minestar.bungee.bungeeinventories.manager.PlayerManager;
import de.minestar.bungee.library.protocol.NetworkPacket;
import de.minestar.bungee.library.protocol.packets.ChatDeathPacket;
import de.minestar.bungee.library.protocol.packets.ChatMessagePacket;
import de.minestar.bungee.library.protocol.packets.DataOKPacket;
import de.minestar.bungee.library.protocol.packets.DataRequestPacket;
import de.minestar.bungee.library.protocol.packets.DataSendPacket;
import de.minestar.bungee.library.protocol.packets.ServerchangeDenyPacket;
import de.minestar.bungee.library.protocol.packets.ServerchangeOKPacket;
import de.minestar.bungee.library.protocol.packets.ServerchangeRequestPacket;

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

    public static ProxiedPlayer getPlayer(String playerName) {
        if (playerName == null) {
            return null;
        }
        return ProxyServer.getInstance().getPlayer(playerName);
    }

    // /////////////////////////////////////////
    //
    // CLASS-Methods
    //
    // /////////////////////////////////////////

    private DataPacketHandler dataPacketHandler;
    private MineServerManager container;
    private PlayerManager playerManager;

    public ActionListener(DataPacketHandler dataPacketHandler, PlayerManager playerManager) {
        this.dataPacketHandler = dataPacketHandler;
        this.container = new MineServerManager();
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
            } else if (serverInfo.getName().equalsIgnoreCase("ftb")) {
                this.container.addServer(new MineServer(serverInfo.getName(), ChatColor.BLUE));
            }
        }
    }

    @Subscribe
    public void onPlayerConnected(ServerConnectedEvent event) {
        if (!this.playerManager.updatePlayer(event.getPlayer(), event.getServer().getInfo())) {
            for (ServerInfo otherServer : ProxyServer.getInstance().getServers().values()) {
                // send message to everyone on the server
                Collection<ProxiedPlayer> playerList = otherServer.getPlayers();
                for (ProxiedPlayer player : playerList) {
                    player.sendMessage(ChatColor.YELLOW + event.getPlayer().getName() + " joined the game.");
                }
            }
        }
    }

    @Subscribe
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        // remove the player
        this.playerManager.removePlayer(event.getPlayer());

        // inform other players, only if there is no inventory saved!
        if (!this.playerManager.hasInventory(event.getPlayer().getName())) {
            for (ServerInfo otherServer : ProxyServer.getInstance().getServers().values()) {
                // send message to everyone on the server
                Collection<ProxiedPlayer> playerList = otherServer.getPlayers();
                for (ProxiedPlayer player : playerList) {
                    player.sendMessage(ChatColor.YELLOW + event.getPlayer().getName() + " left the game.");
                }
            }
        }
    }

    @Subscribe
    public void onPlayerKick(ServerKickEvent event) {
        // remove the player
        this.playerManager.removePlayer(event.getPlayer());

        // inform other players, only if there is no inventory saved!
        for (ServerInfo otherServer : ProxyServer.getInstance().getServers().values()) {
            // send message to everyone on the server
            Collection<ProxiedPlayer> playerList = otherServer.getPlayers();
            for (ProxiedPlayer player : playerList) {
                player.sendMessage(ChatColor.YELLOW + event.getPlayer().getName() + " left the game.");
            }
        }
    }

    @Subscribe
    public void onChat(ChatEvent event) {
        if (event.isCommand()) {
            // handle commands
            String command = event.getMessage().toLowerCase();
            if (!command.startsWith("/")) {
                command = "/" + command;
            }

            // handle only players
            boolean isPlayer = (getPlayer(this.playerManager.getPlayerNameByAdress(event.getSender().getAddress())) != null);
            if (!isPlayer) {
                return;
            }

            // handle command
            if (command.startsWith("/who") || command.startsWith("/online") || command.startsWith("/list")) {
                event.setCancelled(true);
                return;
            }
        } else {
            // handle chat
            System.out.println("chat event!!!!");
        }
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {

        System.out.println("packet received!!!");

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
                    case CHAT_DEATH : {
                        this.handleChatDeath(serverInfo, (ChatDeathPacket) packet);
                        break;
                    }
                    case CHAT_MESSAGE : {
                        this.handleChatMessage(serverInfo, (ChatMessagePacket) packet);
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

    private void handleChatMessage(ServerInfo serverInfo, ChatMessagePacket packet) {
        MineServer server = this.container.getServer(serverInfo.getName());
        if (server == null) {
            return;
        }

        // build message
        String message = server.buildMessage(packet.getMessage());

        // iterate over the playerlist
        for (ServerInfo otherServer : ProxyServer.getInstance().getServers().values()) {
            // ignore, if it is the same server
            if (serverInfo.getName().equalsIgnoreCase(otherServer.getName())) {
                continue;
            }

            // send message to everyone on the server
            Collection<ProxiedPlayer> playerList = otherServer.getPlayers();
            for (ProxiedPlayer player : playerList) {
                player.sendMessage(message);
            }
        }
    }

    private void handleChatDeath(ServerInfo serverInfo, ChatDeathPacket packet) {
        for (ServerInfo otherServer : ProxyServer.getInstance().getServers().values()) {
            // ignore, if it is the same server
            if (serverInfo.getName().equalsIgnoreCase(otherServer.getName())) {
                continue;
            }

            // send message to everyone on the server
            Collection<ProxiedPlayer> playerList = otherServer.getPlayers();
            for (ProxiedPlayer player : playerList) {
                player.sendMessage(packet.getMessage());
            }
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
