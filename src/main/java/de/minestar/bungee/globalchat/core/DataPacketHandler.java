package de.minestar.bungee.globalchat.core;

import java.io.DataInputStream;
import java.io.IOException;

import de.minestar.protocol.newpackets.BungeePacketHandler;
import de.minestar.protocol.newpackets.NetworkPacket;
import de.minestar.protocol.newpackets.PacketType;
import de.minestar.protocol.newpackets.packets.DataOKPacket;
import de.minestar.protocol.newpackets.packets.DataRequestPacket;
import de.minestar.protocol.newpackets.packets.DataSendPacket;
import de.minestar.protocol.newpackets.packets.ServerchangeDenyPacket;
import de.minestar.protocol.newpackets.packets.ServerchangeOKPacket;
import de.minestar.protocol.newpackets.packets.ServerchangeRequestPacket;

public class DataPacketHandler extends BungeePacketHandler {

    public DataPacketHandler(String channel) {
        super(channel);
    }

    @Override
    protected NetworkPacket handlePacket(PacketType packetType, DataInputStream dataInputStream) throws IOException {
        switch (packetType) {
            case SERVERCHANGE_REQUEST : {
                return new ServerchangeRequestPacket(dataInputStream);
            }
            case SERVERCHANGE_OK : {
                return new ServerchangeOKPacket(dataInputStream);
            }
            case SERVERCHANGE_DENY : {
                return new ServerchangeDenyPacket(dataInputStream);
            }
            case DATA_REQUEST : {
                return new DataRequestPacket(dataInputStream);
            }
            case DATA_SEND : {
                return new DataSendPacket(dataInputStream);
            }
            case DATA_OK : {
                return new DataOKPacket(dataInputStream);
            }
            default : {
                return null;
            }
        }
    }

}
