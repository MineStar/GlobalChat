package de.minestar.protocol.newpackets.packets;

import java.nio.ByteBuffer;

import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketType;

public class ChatPacket extends NetworkPacket {

    public ChatPacket(PacketType type) {
        super(PacketType.CHAT);
    }

    public ChatPacket(ByteBuffer buffer) {
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
