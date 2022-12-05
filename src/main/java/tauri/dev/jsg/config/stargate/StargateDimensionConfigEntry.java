package tauri.dev.jsg.config.stargate;

import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StargateDimensionConfigEntry {

    public int energyToOpen;
    public int keepAlive;
    public ArrayList<String> groups;
    public Map<BiomeOverlayEnum, Integer> milkyWayOrigins;

    public StargateDimensionConfigEntry(int energyToOpen, int keepAlive, ArrayList<String> groups) {
        this(energyToOpen, keepAlive, groups, new HashMap<>());
    }

    public StargateDimensionConfigEntry(int energyToOpen, int keepAlive, ArrayList<String> groups, @Nonnull Map<BiomeOverlayEnum, Integer> origins) {
        this.energyToOpen = energyToOpen;
        this.keepAlive = keepAlive;
        this.groups = groups;
        this.milkyWayOrigins = origins;
    }

    @Override
    public String toString() {
        if(milkyWayOrigins.size() > 0){
            StringBuilder originIdsString = new StringBuilder("[");
            int i = 0;
            for(BiomeOverlayEnum k : milkyWayOrigins.keySet()){
                i++;
                originIdsString.append(k.toString()).append(": ").append(milkyWayOrigins.get(k));
                if(i < milkyWayOrigins.size()) originIdsString.append(", ");
            }
            originIdsString.append("]");
            return "[open=" + energyToOpen + ", keepAlive=" + keepAlive + ", groups: '" + groups.toString() + "', milkyWayOrigins: '" + originIdsString + "']";
        }
        return "[open=" + energyToOpen + ", keepAlive=" + keepAlive + ", groups: '" + groups.toString() + "']";
    }

    public boolean isGroupEqual(StargateDimensionConfigEntry other) {
        if (this.groups == null)
            return false;

        if (other.groups == null)
            return false;

        for (int i = 0; i < groups.size(); i++) {
            if (other.groups.contains(groups.get(i)))
                return true;
        }

        return groups.equals(other.groups);
    }
}
