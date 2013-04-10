package de.minestar.bungee.globalchat.core;

import java.io.DataInputStream;
import java.io.IOException;

import de.minestar.protocol.newpackets.BungeePacketHandler;
import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketType;
import de.minestar.protocol.newpackets.packets.InventoryDataPacket;
import de.minestar.protocol.newpackets.packets.InventoryRequestPacket;

public class InventoryPacketHandler extends BungeePacketHandler {

    public InventoryPacketHandler(String channel) {
        super(channel);
    }

    @Override
    protected NetworkPacket handlePacket(PacketType packetType, DataInputStream dataInputStream) throws IOException {
        switch (packetType) {
            case INVENTORY_REQUEST : {
                return new InventoryRequestPacket(dataInputStream);
            }
            case INVENTORY_DATA : {
                return new InventoryDataPacket(dataInputStream);
            }
            default : {
                return null;
            }
        }
    }

}
