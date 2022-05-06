package mrjake.discord.core;

import mrjake.discord.Enums;

public class Lobby {
    public int Id;
    public Enums.LobbyType Type;
    public int OwnerId;
    public String Secret;
    public long Capacity;
    public boolean Locked;
}
