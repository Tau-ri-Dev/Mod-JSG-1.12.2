package tauri.dev.jsg.config.stargate;

import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StargateDimensionConfigEntry {

    public String name;
    public int distance;
    public ArrayList<String> groups;
    public Map<BiomeOverlayEnum, Integer> milkyWayOrigins;

    public StargateDimensionConfigEntry(String name, int distance,  ArrayList<String> groups) {
        this(name, distance, groups, new HashMap<>());
    }

    public StargateDimensionConfigEntry(String name, int distance, ArrayList<String> groups, @Nonnull Map<BiomeOverlayEnum, Integer> origins) {
        this.name = name;
        this.distance = distance;
        this.groups = groups;
        this.milkyWayOrigins = origins;
        if (this.groups == null)
            this.groups = new ArrayList<>();
    }

    @Override
    public String toString() {
        if (milkyWayOrigins.size() > 0) {
            StringBuilder originIdsString = new StringBuilder("[");
            int i = 0;
            for (BiomeOverlayEnum k : milkyWayOrigins.keySet()) {
                i++;
                originIdsString.append(k.toString()).append(": ").append(milkyWayOrigins.get(k));
                if (i < milkyWayOrigins.size()) originIdsString.append(", ");
            }
            originIdsString.append("]");
            return "[name=" + name + ", distance=" + distance + ", groups: '" + groups.toString() + "', milkyWayOrigins: '" + originIdsString + "']";
        }
        return "[name=" + name + ", distance=" + distance + ", groups: '" + groups.toString() + "']";
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
