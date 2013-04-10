package de.minestar.bungee.bungeeinventories.core;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import de.minestar.bungee.bungeeinventories.data.DataPacketHandler;
import de.minestar.bungee.bungeeinventories.data.PlayerManager;
import de.minestar.bungee.bungeeinventories.listener.ActionListener;

public class Core extends Plugin {

    public static Core INSTANCE;
    public final String NAME = "GlobalChat";

    private DataPacketHandler dataPacketHandler;

    public static void log(String message) {
        System.out.println("[ " + INSTANCE.NAME + " ] " + message);
    }

    @Override
    public void onDisable() {
        Core.log("Disabled!");
    }

    @Override
    public void onEnable() {
        Core.INSTANCE = this;

        this.dataPacketHandler = new DataPacketHandler("MS_InvSync");

        ProxyServer.getInstance().registerChannel(this.dataPacketHandler.getChannel());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ActionListener(this.dataPacketHandler, new PlayerManager()));
        Core.log("Enabled!");
    }

}
