package tauri.dev.discord.core;

import tauri.dev.discord.Enums;

public class Lobby {
    public int Id;
    public Enums.LobbyType Type;
    public int OwnerId;
    public String Secret;
    public long Capacity;
    public boolean Locked;
}
