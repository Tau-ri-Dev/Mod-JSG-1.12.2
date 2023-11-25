package tauri.dev.jsg.util;

import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;
import java.util.Map;

public class DimensionsHelper {
    public static Map<Integer, DimensionType> getRegisteredDimensions() {
        Map<Integer, DimensionType> result = new HashMap<>();
        for(Map.Entry<DimensionType, IntSortedSet> entry : DimensionManager.getRegisteredDimensions().entrySet()){
            for(int i : entry.getValue()){
                result.put(i, entry.getKey());
            }
        }
        return result;
    }
}
