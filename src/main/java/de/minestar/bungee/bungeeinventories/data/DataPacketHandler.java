package de.minestar.bungee.bungeeinventories.data;

import java.io.DataInputStream;
import java.io.IOException;

import de.minestar.bungee.library.protocol.BungeePacketHandler;
import de.minestar.bungee.library.protocol.NetworkPacket;
import de.minestar.bungee.library.protocol.PacketType;
import de.minestar.bungee.library.protocol.packets.ChatDeathPacket;
import de.minestar.bungee.library.protocol.packets.ChatMessagePacket;
import de.minestar.bungee.library.protocol.packets.DataOKPacket;
import de.minestar.bungee.library.protocol.packets.DataRequestPacket;
import de.minestar.bungee.library.protocol.packets.DataSendPacket;
import de.minestar.bungee.library.protocol.packets.ServerchangeDenyPacket;
import de.minestar.bungee.library.protocol.packets.ServerchangeOKPacket;
import de.minestar.bungee.library.protocol.packets.ServerchangeRequestPacket;

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
            case CHAT_DEATH : {
                return new ChatDeathPacket(dataInputStream);
            }
            case CHAT_MESSAGE : {
                return new ChatMessagePacket(dataInputStream);
            }
            default : {
                return null;
            }
        }
    }

}
