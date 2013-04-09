package de.minestar.protocol.newpackets.packets;

import java.nio.ByteBuffer;

import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketType;

public class InventoryRequestPackage extends NetworkPacket {

    private String playerName;

    public InventoryRequestPackage(PacketType type) {
        super(PacketType.CHAT);
    }

    public InventoryRequestPackage(ByteBuffer buffer) {
        super(PacketType.CHAT, buffer);
    }

    public PacketType getPacketType() {
        return PacketType.CHAT;
    }

    @Override
    public void onSend(ByteBuffer buffer) {
    }

    @Override
    public void onReceive(ByteBuffer buffer) {

    }
}
