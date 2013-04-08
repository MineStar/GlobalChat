package de.minestar.protocol.newpackets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.connection.Server;

public class PacketHandler {

    private static final String BROADCAST = "ALL";

    private static final int MAX_PACKET_SIZE = 32766;

    private final ByteBuffer BUFFER;

    private Map<Integer, NetworkPacket> packetMap = new HashMap<Integer, NetworkPacket>();

    public PacketHandler() {
        BUFFER = ByteBuffer.allocate(MAX_PACKET_SIZE);
        fillPacketMap();
    }

    private void fillPacketMap() {
        for (PacketType type : PacketType.values()) {
            try {
                packetMap.put(type.ordinal(), type.getClazz().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(NetworkPacket packet, Server server, String channel) {
        this.send(packet, server, channel, BungeeSubChannel.FORWARD, BROADCAST);
    }

    public void send(NetworkPacket packet, Server server, String channel, BungeeSubChannel subChannel, String targetServer) {
        if (packet instanceof MultiPacket) {
            MultiPacket multiPacket = (MultiPacket) packet;
            for (NetworkPacket innerPacket : multiPacket) {
                sendPacket(innerPacket, server, channel, subChannel, targetServer);
            }
        } else {
            sendPacket(packet, server, channel, subChannel, targetServer);
        }
    }

    public void send(NetworkPacket packet, Server server, String channel, BungeeSubChannel subChannel) {
        if (packet instanceof MultiPacket) {
            MultiPacket multiPacket = (MultiPacket) packet;
            for (NetworkPacket innerPacket : multiPacket) {
                sendPacket(innerPacket, server, channel, subChannel, null);
            }
        } else {
            sendPacket(packet, server, channel, subChannel, null);
        }
    }

    public final static Charset UFT8 = Charset.forName("UTF-8");

    private void sendPacket(NetworkPacket packet, Server server, String channel, BungeeSubChannel subChannel, String targetServer) {
        BUFFER.clear();
        BUFFER.put(subChannel.getSubchannel().getBytes(UFT8));

        if (targetServer != null)
            BUFFER.put(targetServer.getBytes(UFT8));

        BUFFER.putInt(packet.getPacketType().ordinal());
        packet.pack(BUFFER);
        BUFFER.rewind();
        // Dirty -.-
        server.sendData(channel, Arrays.copyOf(BUFFER.array(), BUFFER.limit()));
        BUFFER.clear();
    }

    public NetworkPacket extractPacket(byte[] data) {
        BUFFER.clear();
        BUFFER.put(data);
        BUFFER.reset();
        NetworkPacket templatePacket = packetMap.get(BUFFER.getInt());
        return templatePacket.extract(BUFFER);

    }

}
