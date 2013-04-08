package de.minestar.protocol.newpackets;

import java.nio.ByteBuffer;

public abstract class NetworkPacket {

    private final PacketType type;

    public NetworkPacket(PacketType type) {
        this.type = type;
    }

    public NetworkPacket(PacketType type, ByteBuffer buffer) {
        this(type);
        onReceive(buffer);
    }

    public final PacketType getType() {
        return type;
    }

    public final void pack(ByteBuffer buffer) {
        onSend(buffer);
    }

    public abstract void onSend(ByteBuffer buffer);

    public abstract NetworkPacket onReceive(ByteBuffer buffer);

}
