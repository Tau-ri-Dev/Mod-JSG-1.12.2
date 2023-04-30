package tauri.dev.jsg.config;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.parsers.BiomeParser;
import tauri.dev.jsg.config.parsers.BlockMetaParser;
import tauri.dev.jsg.config.parsers.ItemMetaParser;
import tauri.dev.jsg.renderer.biomes.BiomeOverlayEnum;
import tauri.dev.jsg.util.ItemMetaPair;
import tauri.dev.jsg.util.JSGAxisAlignedBB;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSGConfigUtil {

    @SubscribeEvent
    public void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(JSG.MOD_ID)) {
            ConfigManager.sync(JSG.MOD_ID, Config.Type.INSTANCE);
            JSG.info("Should be saved");
            JSGConfigUtil.resetCache();
        }
    }

    @SuppressWarnings("unused")
    public static void rescaleToConfig() {
        GlStateManager.translate(JSGConfig.General.devConfig.x, JSGConfig.General.devConfig.y, JSGConfig.General.devConfig.z);
        GlStateManager.scale(JSGConfig.General.devConfig.s, JSGConfig.General.devConfig.s, JSGConfig.General.devConfig.s);
    }

    @SuppressWarnings("unused")
    public static void rotateToConfig() {
        GlStateManager.rotate(JSGConfig.General.devConfig.x2, 1, 0, 0);
        GlStateManager.rotate(JSGConfig.General.devConfig.y2, 0, 1, 0);
        GlStateManager.rotate(JSGConfig.General.devConfig.z2, 0, 0, 1);
    }

    @SuppressWarnings("unused")
    public static JSGAxisAlignedBB createHitbox() {
        return new JSGAxisAlignedBB(JSGConfig.General.devConfig.x, JSGConfig.General.devConfig.y, JSGConfig.General.devConfig.z, JSGConfig.General.devConfig.x2, JSGConfig.General.devConfig.y2, JSGConfig.General.devConfig.z2);
    }

    public static void resetCache() {
        cachedInvincibleBlocks = null;
        cachedCamoBlacklistBlocks = null;
        cachedBiomeMatchesReverse = null;
        cachedBiomeOverrideBlocks = null;
        cachedBiomeOverrideBlocksReverse = null;
    }

    // Kawoosh blocks
    private static Map<IBlockState, Boolean> cachedInvincibleBlocks = null;

    public static boolean canKawooshDestroyBlock(IBlockState state) {
        if (state.getBlock() == JSGBlocks.IRIS_BLOCK) return false;
        if (state.getBlock() == JSGBlocks.INVISIBLE_BLOCK) return false;

        if (cachedInvincibleBlocks == null) {
            cachedInvincibleBlocks = BlockMetaParser.parseConfig(JSGConfig.Stargate.eventHorizon.kawooshInvincibleBlocks);
        }
        if (cachedInvincibleBlocks.get(state.getBlock().getDefaultState()) != null && cachedInvincibleBlocks.get(state.getBlock().getDefaultState())) {
            return false;
        }
        return cachedInvincibleBlocks.get(state) == null;
    }

    // Camo blacklist
    private static Map<IBlockState, Boolean> cachedCamoBlacklistBlocks = null;

    public static boolean canBeUsedAsCamoBlock(IBlockState state) {
        if(JSGBlocks.isInBlocksArray(state.getBlock(), JSGBlocks.CAMO_BLOCKS_BLACKLIST)) return false;

        if (cachedCamoBlacklistBlocks == null) {
            cachedCamoBlacklistBlocks = BlockMetaParser.parseConfig(JSGConfig.Stargate.visual.camoBlacklist);
        }
        if (cachedCamoBlacklistBlocks.get(state.getBlock().getDefaultState()) != null && cachedCamoBlacklistBlocks.get(state.getBlock().getDefaultState())) {
            return false;
        }
        return cachedCamoBlacklistBlocks.get(state) == null;
    }


    // Biome overlays
    private static Map<Biome, BiomeOverlayEnum> cachedBiomeMatchesReverse = null;

    public static Map<Biome, BiomeOverlayEnum> getBiomeOverrideBiomes() {
        if (cachedBiomeMatchesReverse == null) {
            cachedBiomeMatchesReverse = new HashMap<>();

            for (Map.Entry<String, String[]> entry : JSGConfig.Stargate.visual.biomeMatches.entrySet()) {
                List<Biome> parsedList = BiomeParser.parseConfig(entry.getValue());
                BiomeOverlayEnum biomeOverlay = BiomeOverlayEnum.fromString(entry.getKey());

                for (Biome biome : parsedList) {
                    cachedBiomeMatchesReverse.put(biome, biomeOverlay);
                }
            }
        }

        return cachedBiomeMatchesReverse;
    }

    private static Map<BiomeOverlayEnum, List<ItemMetaPair>> cachedBiomeOverrideBlocks = null;
    private static Map<ItemMetaPair, BiomeOverlayEnum> cachedBiomeOverrideBlocksReverse = null;

    private static void genBiomeOverrideCache() {
        cachedBiomeOverrideBlocks = new HashMap<>();
        cachedBiomeOverrideBlocksReverse = new HashMap<>();

        for (Map.Entry<String, String[]> entry : JSGConfig.Stargate.visual.biomeOverrideBlocks.entrySet()) {
            List<ItemMetaPair> parsedList = ItemMetaParser.parseConfig(entry.getValue());
            BiomeOverlayEnum biomeOverlay = BiomeOverlayEnum.fromString(entry.getKey());

            cachedBiomeOverrideBlocks.put(biomeOverlay, parsedList);

            for (ItemMetaPair stack : parsedList) {
                cachedBiomeOverrideBlocksReverse.put(stack, biomeOverlay);
            }
        }
    }

    public static Map<BiomeOverlayEnum, List<ItemMetaPair>> getBiomeOverrideBlocks() {
        if (cachedBiomeOverrideBlocks == null) {
            genBiomeOverrideCache();
        }

        return cachedBiomeOverrideBlocks;
    }

    public static Map<ItemMetaPair, BiomeOverlayEnum> getBiomeOverrideItemMetaPairs() {
        if (cachedBiomeOverrideBlocksReverse == null) {
            genBiomeOverrideCache();
        }

        return cachedBiomeOverrideBlocksReverse;
    }
}
