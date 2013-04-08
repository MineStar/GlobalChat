package de.minestar.protocol.packets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MultiPacket {

    public static MultiPacket readPackage(byte[] data) {
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

            String type = dis.readUTF();
            String targetServer = dis.readUTF();
            int packetTypus = dis.readInt();
            PacketType packetType = PacketType.fromInt(packetTypus);

            if (packetType == null) {
                System.out.println("PacketID " + packetTypus + " not found! Ignoring package...");
                return null;
            }

            int packetCount = dis.readInt();

            // build the packet-list
            ArrayList<Packet> packetList = new ArrayList<Packet>();
            for (int index = 0; index < packetCount; index++) {
                int length = 0;
                byte[] byteArray = null;
                int packetID = dis.readInt();
                PacketType singlePacketType = PacketType.fromInt(packetID);
                if (singlePacketType != null) {
                    // get the length of the package
                    length = dis.readInt();

                    // get the bytearray
                    byteArray = new byte[length];
                    dis.read(byteArray);
                } else {
                    System.out.println("PacketID " + packetID + " not found! Ignoring package...");
                    continue;
                }
                packetList.add(new Packet(singlePacketType, length, byteArray));
            }
            return new MultiPacket(type, targetServer, packetType, packetList);
        } catch (Exception e) {
        }
        return null;
    }

    private final String type;
    private final String targetServer;
    private final PacketType packetType;
    private final ArrayList<Packet> packetList;
    private byte[] data;
    private ByteArrayOutputStream bos;

    public MultiPacket(String type, String targetServer, PacketType packetType) throws IOException {
        this(type, targetServer, packetType, null);
    }

    public MultiPacket(String type, String targetServer, PacketType packetType, ArrayList<Packet> packetList) throws IOException {
        this.type = type;
        this.targetServer = targetServer;
        this.packetType = packetType;

        // create packetlist
        if (packetList != null) {
            this.packetList = packetList;
        } else {
            this.packetList = new ArrayList<Packet>();
        }

        this.buildPacket();
    }

    private void buildPacket() throws IOException {
        // create streams
        this.bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        // write type
        dos.writeUTF(this.type);

        // write targetserver
        dos.writeUTF(this.targetServer);

        // write packettype
        dos.writeInt(this.packetType.ordinal());

        // write packetcout
        dos.writeInt(this.packetList.size());

        // write every packet
        for (Packet packet : this.packetList) {
            // write packet-type
            dos.writeInt(packet.getPacketType().ordinal());

            // write length
            dos.writeInt(packet.getLength());

            // write data
            dos.write(packet.getData());
        }
        this.data = bos.toByteArray();
    }

    public ArrayList<Packet> getPacketList() {
        return packetList;
    }

    public void addPacket(Packet packet) throws IOException {
        // add packet
        this.packetList.add(packet);

        // rebuild packet
        this.buildPacket();
    }

    public ByteArrayOutputStream getByteOutputStream() throws IOException {
        return this.bos;
    }

    public String getType() {
        return type;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public int getPacketCount() {
        return packetList.size();
    }

    public byte[] getData() {
        return data;
    }
}
