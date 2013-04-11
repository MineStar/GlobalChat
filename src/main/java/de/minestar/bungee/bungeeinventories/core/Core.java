package de.minestar.bungee.bungeeinventories.core;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import de.minestar.bungee.bungeeinventories.data.DataPacketHandler;
import de.minestar.bungee.bungeeinventories.data.PlayerManager;
import de.minestar.bungee.bungeeinventories.listener.ActionListener;
import de.minestar.bungee.library.AbstractCore;

public class Core extends AbstractCore {

    public static Core INSTANCE;
    public static final String NAME = "BungeeInventories";

    private DataPacketHandler dataPacketHandler;
    private ActionListener actionListener;

    @Override
    protected boolean createManager() {
        Core.INSTANCE = this;
        this.dataPacketHandler = new DataPacketHandler("MS_InvSync");
        return super.createManager();
    }

    @Override
    protected boolean commonEnable() {
        ProxyServer.getInstance().registerChannel(this.dataPacketHandler.getChannel());
        return super.commonEnable();
    }

    @Override
    protected boolean createListener() {
        this.actionListener = new ActionListener(this.dataPacketHandler, new PlayerManager());
        return super.createListener();
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerListener(this, this.actionListener);
        return super.registerEvents(pm);
    }

}
