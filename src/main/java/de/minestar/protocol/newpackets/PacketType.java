package de.minestar.protocol.newpackets;

import java.util.HashMap;
import java.util.Map;

public enum PacketType {
    MULTIPACKET,

    JOIN,

    QUIT,

    KICK,

    CHAT,

    COMMAND,

    INVENTORY_REQUEST,

    INVENTORY_DATA;

    private static Map<Integer, PacketType> mapByOrdinal;

    static {
        mapByOrdinal = new HashMap<Integer, PacketType>();
        for (PacketType type : values()) {
            mapByOrdinal.put(type.ordinal(), type);
        }
    }

    public static PacketType get(int ordinal) {
        return mapByOrdinal.get(ordinal);
    }
}
