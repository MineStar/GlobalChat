package de.minestar.protocol.newpackets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import net.md_5.bungee.api.config.ServerInfo;

public abstract class BungeePacketHandler {

    private final String broadcast;
    private final String channel;

    public BungeePacketHandler(String channel) {
        this(channel, "ALL");
    }

    public BungeePacketHandler(String channel, String defaultBroadcast) {
        this.channel = channel;
        this.broadcast = defaultBroadcast;
    }

    public void send(NetworkPacket packet, ServerInfo serverInfo, String channel) {
        this.send(packet, serverInfo, channel, BungeeSubChannel.FORWARD, this.broadcast);
    }

    public void send(NetworkPacket packet, ServerInfo serverInfo, String channel, BungeeSubChannel subChannel, String targetServer) {
        if (packet instanceof MultiPacket) {
            MultiPacket multiPacket = (MultiPacket) packet;
            for (NetworkPacket innerPacket : multiPacket) {
                sendPacket(innerPacket, serverInfo, channel, subChannel, targetServer);
            }
        } else {
            sendPacket(packet, serverInfo, channel, subChannel, targetServer);
        }
    }

    public void send(NetworkPacket packet, ServerInfo serverInfo, String channel, BungeeSubChannel subChannel) {
        if (packet instanceof MultiPacket) {
            MultiPacket multiPacket = (MultiPacket) packet;
            for (NetworkPacket innerPacket : multiPacket) {
                sendPacket(innerPacket, serverInfo, channel, subChannel, null);
            }
        } else {
            sendPacket(packet, serverInfo, channel, subChannel, null);
        }
    }

    public final static Charset UFT8 = Charset.forName("UTF-8");

    private void sendPacket(NetworkPacket packet, ServerInfo serverInfo, String channel, BungeeSubChannel subChannel, String targetServer) {

        // Packet Head:
        //
        // | BUNGEE HEAD |
        // ------------------------------------
        // BungeeChannel (Forward, Connect etc.) - Command to Bungee what to do
        // with the packet
        // TargetServer (ALL or servername) - Receiver of the message
        // ------------------------------------
        // | BUKKIT HEAD |
        // Channel (Own defined plugin channel) - Channel between two plugins
        // DataLength (Length of the data without any head length)
        // Data (Array of bytes - Must be long as defined in DataLength
        //

        try {
            // create streams
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            // BungeeChannel
            dos.writeUTF(subChannel.getName());

            // target
            dos.writeUTF(targetServer);

            // pluginchannel
            dos.writeUTF(channel);

            // pack data
            ByteArrayOutputStream data = packet.pack();
            byte[] dataArray = data.toByteArray();

            // datalength
            dos.writeInt(dataArray.length);

            // data
            dos.write(dataArray);

            // send data
            serverInfo.sendData(this.channel, bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NetworkPacket extractPacket(byte[] data) {
        try {
            // create streams
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DataInputStream dataInputStream = new DataInputStream(bis);

            // extract head
            PacketHeadData headData = new PacketHeadData(dataInputStream);

            if (!headData.getSubChannel().equalsIgnoreCase(this.getChannel())) {
                return null;
            }

            // get data
            int datalength = dataInputStream.readInt();
            byte[] dataArray = new byte[datalength];
            bis.read(dataArray);

            // create new streams for reading
            bis = new ByteArrayInputStream(dataArray);
            dataInputStream = new DataInputStream(bis);

            // get packettype
            PacketType packetType = PacketType.get(dataInputStream.readInt());

            return this.handlePacket(packetType, dataInputStream);
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract NetworkPacket handlePacket(PacketType packetType, DataInputStream dataInputStream) throws IOException;

    public String getChannel() {
        return channel;
    }

    public String getBroadcast() {
        return broadcast;
    }
}
