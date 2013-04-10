package de.minestar.bungee.globalchat.data;

public class MineServer {
    private final String name;
    private final ChatColor color;

    public MineServer(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String buildMessage(String playerName, String message) {
        String text = color + "[] " + ChatColor.GRAY + playerName + ChatColor.WHITE + ": " + message;
        return text;
    }
}
