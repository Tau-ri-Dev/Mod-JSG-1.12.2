package tauri.dev.jsg.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tauri.dev.jsg.advancements.JSGAdvancements;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.tileentity.stargate.*;

import java.util.List;

public class JSGAdvancementsUtil {
    public enum EnumAdvancementType {
        GATE_OPEN,
        GATE_MERGE,
        GATE_FLICKER,
        IRIS_IMPACT,

        ZPM_HUB,
        ZPM_SLOT
    }

    public static void tryTriggerRangedAdvancement(TileEntity tile, EnumAdvancementType advancementType) {
        World world = tile.getWorld();
        BlockPos pos = tile.getPos();

        int radius = JSGConfig.advancementsConfig.radius;

        List<EntityPlayerMP> players = world.getEntitiesWithinAABB(EntityPlayerMP.class, new JSGAxisAlignedBB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)));
        for (EntityPlayerMP player : players) {
            switch (advancementType) {
                case GATE_OPEN:
                    if (!(tile instanceof StargateClassicBaseTile)) break;
                    int dialedSize = ((StargateAbstractBaseTile) tile).getDialedAddress().size();
                    JSGAdvancements.CHEVRON_SEVEN_LOCKED.trigger(player);
                    if (dialedSize >= 8)
                        JSGAdvancements.CHEVRON_EIGHT_LOCKED.trigger(player);
                    if (dialedSize >= 9)
                        JSGAdvancements.CHEVRON_NINE_LOCKED.trigger(player);
                    break;
                case GATE_MERGE:
                    if (!(tile instanceof StargateAbstractBaseTile)) break;
                    if (tile instanceof StargateOrlinBaseTile)
                        JSGAdvancements.MERGED_ORLIN.trigger(player);
                    if (tile instanceof StargateMilkyWayBaseTile)
                        JSGAdvancements.MERGED_MILKYWAY.trigger(player);
                    if (tile instanceof StargatePegasusBaseTile)
                        JSGAdvancements.MERGED_PEGASUS.trigger(player);
                    if (tile instanceof StargateUniverseBaseTile)
                        JSGAdvancements.MERGED_UNIVERSE.trigger(player);
                    break;
                case IRIS_IMPACT:
                    JSGAdvancements.IRIS_IMPACT.trigger(player);
                    break;
                case ZPM_HUB:
                    JSGAdvancements.THREE_ZPMS.trigger(player);
                    break;
                case ZPM_SLOT:
                    JSGAdvancements.ZPM_SLOT.trigger(player);
                    break;
                default:
                    break;
            }
        }
    }
}
