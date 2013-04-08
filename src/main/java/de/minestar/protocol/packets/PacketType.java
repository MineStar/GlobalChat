package de.minestar.protocol.packets;

public enum PacketType {
    MULTIPACKET,

    PLAYERNAME,

    JOIN,

    QUIT,

    KICK,

    CHAT,

    COMMAND,

    INVENTORY_SAVE,

    INVENTORY_REQUEST,

    INVENTORY_LOAD;

    public static PacketType fromInt(int integer) {
        for (PacketType type : PacketType.values()) {
            if (type.ordinal() == integer) {
                return type;
            }
        }
        return null;
    }
}
