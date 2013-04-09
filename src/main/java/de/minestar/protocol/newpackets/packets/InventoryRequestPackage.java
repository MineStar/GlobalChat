package de.minestar.protocol.newpackets.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import de.minestar.bungee.globalchat.core.BufferHelper;
import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketType;

public class InventoryRequestPackage extends NetworkPacket {

    private String playerName;

    public InventoryRequestPackage(String playerName) {
        super(PacketType.INVENTORY_REQUEST);
        this.playerName = playerName;
    }

    public InventoryRequestPackage(PacketType type) {
        super(PacketType.INVENTORY_REQUEST);
    }

    public InventoryRequestPackage(ByteBuffer buffer) {
        super(PacketType.INVENTORY_REQUEST, buffer);
    }

    public InventoryRequestPackage(DataInputStream dataInputStream) throws IOException {
        super(PacketType.INVENTORY_REQUEST, dataInputStream);
    }

    public PacketType getPacketType() {
        return PacketType.INVENTORY_REQUEST;
    }

    @Override
    public void onSend(ByteBuffer buffer) {
        BufferHelper.setString(buffer, this.playerName);
    }

    @Override
    public void onReceive(ByteBuffer buffer) {
        this.playerName = BufferHelper.getString(buffer);
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void onSend(DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeUTF(this.playerName);
    }

    @Override
    public void onReceive(DataInputStream dataInputStream) throws IOException {
        this.playerName = dataInputStream.readUTF();
    }
}
