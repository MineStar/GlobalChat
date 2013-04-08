package de.minestar.protocol.newpackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MultiPacket implements NetworkPacket, Iterable<NetworkPacket> {

    private List<NetworkPacket> subPackets;

    public MultiPacket() {
        subPackets = new ArrayList<NetworkPacket>();
    }

    public MultiPacket addPacket(NetworkPacket packet) {
        subPackets.add(packet);
        return this;
    }

    public void clean() {
        subPackets.clear();
    }

    public void pack(ByteBuffer buffer) {
        throw new NotImplementedException();
    }

    public NetworkPacket extract(ByteBuffer buffer) {
        throw new NotImplementedException();
    }

    public Iterator<NetworkPacket> iterator() {
        return subPackets.iterator();
    }

    public PacketType getPacketType() {
        return PacketType.MULTIPACKET;
    }
}
