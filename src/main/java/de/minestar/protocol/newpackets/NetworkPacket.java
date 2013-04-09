package de.minestar.protocol.newpackets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public NetworkPacket(PacketType type, DataInputStream dataInputStream) throws IOException {
        this(type);
        onReceive(dataInputStream);
    }
    public final PacketType getType() {
        return type;
    }

    public final void pack(ByteBuffer buffer) {
        onSend(buffer);
    }

    public final ByteArrayOutputStream pack() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(type.ordinal());
            onSend(dos);
            return bos;
        } catch (Exception e) {
            return null;
        }
    }

    public abstract void onSend(ByteBuffer buffer);

    public abstract void onReceive(ByteBuffer buffer);

    public abstract void onSend(DataOutputStream dataOutputStream) throws IOException;

    public abstract void onReceive(DataInputStream dataInputStream) throws IOException;

}
