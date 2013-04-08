package de.minestar.protocol.newpackets.packets;

import java.nio.ByteBuffer;

import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketType;

public class ChatPacket implements NetworkPacket {

    public ChatPacket() {
        // REFLECTIONS
    }

    public PacketType getPacketType() {
        return PacketType.CHAT;
    }

    public void pack(ByteBuffer buffer) {
        // TODO Auto-generated method stub

    }

    public NetworkPacket extract(ByteBuffer buffer) {
        return new ChatPacket(buffer);
    }

    private ChatPacket(ByteBuffer buffer) {

    }

}
