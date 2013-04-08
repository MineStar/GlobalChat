package de.minestar.protocol.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet {

    public static Packet createPackage(PacketType packageType, String data) throws IOException {
        return createPackage(packageType, data.getBytes("UTF-8"));
    }

    public static Packet createPackage(PacketType packageType, byte[] data) throws IOException {
        return new Packet(packageType, data.length, data);
    }

    private final PacketType packetType;
    private final int length;
    private final byte[] data;
    private ByteArrayOutputStream bos = null;

    public Packet(PacketType packetType, int length, byte[] data) {
        this.packetType = packetType;
        this.length = length;
        this.data = data;
    }

    public ByteArrayOutputStream getByteOutputStream() throws IOException {
        if (this.bos != null) {
            return this.bos;
        }

        // create streams
        this.bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(this.bos);

        // package-ID
        dos.writeInt(packetType.ordinal());

        // data-length

        dos.writeInt(data.length);

        // data
        dos.write(data);

        // return
        return this.bos;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public int getLength() {
        return length;
    }

    public byte[] getData() {
        return data;
    }
}
