package mrjake.discord.core.activity;

import mrjake.discord.Enums;

public class Activity {
    public Enums.ActivityType Type;

    public int ApplicationId;
    public String Name;
    public String State;
    public String Details;
    public ActivityTimestamps Timestamps;
    public ActivityAssets Assets;
    public ActivityParty Party;
    public ActivitySecrets Secrets;
    public boolean Instance;
}
