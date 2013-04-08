package de.minestar.protocol.newpackets;

import java.nio.ByteBuffer;

public interface NetworkPacket {

    public PacketType getPacketType();

    public void pack(ByteBuffer buffer);

    public NetworkPacket extract(ByteBuffer buffer);
}
