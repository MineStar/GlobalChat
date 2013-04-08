package de.minestar.bungee.globalchat.core;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import de.minestar.bungee.globalchat.listener.ActionListener;

public class Core extends Plugin {

    public static Core INSTANCE;
    public final String NAME = "GlobalChat";

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
        ProxyServer.getInstance().registerChannel("globalchat");
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ActionListener(new PlayerManager()));
        Core.log("Enabled!");
    }

}
