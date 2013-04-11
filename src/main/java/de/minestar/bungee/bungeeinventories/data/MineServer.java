package de.minestar.bungee.bungeeinventories.data;

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

    public String buildMessage(String message) {
        try {
            String firstLetter = String.valueOf(name.charAt(0)).toUpperCase();
            String text = color + "[" + firstLetter + "] " + message;
            return text;
        } catch (Exception e) {
            String text = color + "[ ] " + message;
            return text;
        }
    }
}
