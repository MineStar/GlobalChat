package de.minestar.protocol.newpackets;

import de.minestar.protocol.newpackets.packets.ChatPacket;

public enum PacketType {
    MULTIPACKET,

    PLAYERNAME,

    JOIN,

    QUIT,

    KICK,

    CHAT(ChatPacket.class),

    COMMAND,

    INVENTORY_SAVE,

    INVENTORY_REQUEST,

    INVENTORY_LOAD;

    private Class<? extends NetworkPacket> clazz;

    private PacketType() {
        this(null);
    }

    private PacketType(Class<? extends NetworkPacket> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends NetworkPacket> getClazz() {
        return clazz;
    }
}
