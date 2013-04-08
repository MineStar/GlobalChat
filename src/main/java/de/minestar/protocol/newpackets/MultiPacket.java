package de.minestar.protocol.newpackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MultiPacket extends NetworkPacket implements Iterable<NetworkPacket> {

    private List<NetworkPacket> subPackets;

    public MultiPacket() {
        super(PacketType.MULTIPACKET);
        subPackets = new ArrayList<NetworkPacket>();
    }

    public MultiPacket addPacket(NetworkPacket packet) {
        subPackets.add(packet);
        return this;
    }

    public void clean() {
        subPackets.clear();
    }

    public Iterator<NetworkPacket> iterator() {
        return subPackets.iterator();
    }

    @Override
    public void onSend(ByteBuffer buffer) {
        throw new NotImplementedException();
    }

    @Override
    public NetworkPacket onReceive(ByteBuffer buffer) {
        throw new NotImplementedException();
    }
}
