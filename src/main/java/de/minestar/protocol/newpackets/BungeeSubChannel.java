package de.minestar.protocol.newpackets;

public enum BungeeSubChannel {

    CONNECT("Connect"),

    FORWARD("Forward");

    private final String subchannel;

    private BungeeSubChannel(String subChannel) {
        this.subchannel = subChannel;
    }

    public String getSubchannel() {
        return subchannel;
    }
}
