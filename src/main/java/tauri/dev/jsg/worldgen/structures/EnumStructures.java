package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.structures.stargate.processor.NetherProcessor;
import tauri.dev.jsg.worldgen.structures.stargate.processor.OverworldProcessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public enum EnumStructures {

    // ---------------------------------------------------------------------------
    // STARGATE STRUCTURES

    // Milkyway
    PLAINS_MW("sg_plains_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
                add(Blocks.DIRT);
                add(Blocks.STONE);
            }}, null, 15, Rotation.CLOCKWISE_90),
    DESERT_MW("sg_desert_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.SAND);
                add(Blocks.SANDSTONE);
            }},
            new ArrayList<String>() {{
                add("desert");
                add("mesa");
            }}, 15, Rotation.CLOCKWISE_90),
    MOSSY_MW("sg_mossy_milkyway", 1, true, SymbolTypeEnum.MILKYWAY, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
                add(Blocks.DIRT);
                add(Blocks.STONE);
            }},
            new ArrayList<String>() {{
                add("taiga");
                add("jungle");
                add("swamp");
                add("mushroom");
            }}, 15, Rotation.CLOCKWISE_90),
    FROST_MW("sg_frosty_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.SNOW);
                add(Blocks.SNOW_LAYER);
                add(Blocks.ICE);
                add(Blocks.FROSTED_ICE);
                add(Blocks.PACKED_ICE);
            }},
            new ArrayList<String>() {{
                add("ice");
                add("frozen");
                add("cold");
            }}, 15, Rotation.CLOCKWISE_90),
    // Pegasus
    PLAINS_PG("sg_plains_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
                add(Blocks.DIRT);
                add(Blocks.STONE);
            }}, null, 15, Rotation.CLOCKWISE_90),
    DESERT_PG("sg_desert_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.SAND);
                add(Blocks.SANDSTONE);
            }},
            new ArrayList<String>() {{
                add("desert");
                add("mesa");
            }}, 15, Rotation.CLOCKWISE_90),
    MOSSY_PG("sg_mossy_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
                add(Blocks.DIRT);
                add(Blocks.STONE);
            }},
            new ArrayList<String>() {{
                add("taiga");
            }}, 15, Rotation.CLOCKWISE_90),
    FROST_PG("sg_frosty_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 8, 13, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.SNOW);
                add(Blocks.SNOW_LAYER);
                add(Blocks.ICE);
                add(Blocks.FROSTED_ICE);
                add(Blocks.PACKED_ICE);
            }},
            new ArrayList<String>() {{
                add("ice");
                add("frozen");
                add("cold");
            }}, 15, Rotation.CLOCKWISE_90),
    // Universe
    END_UNI("sg_end_universe", 0, true, SymbolTypeEnum.UNIVERSE, 10, 10, 1, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceTheEnd, new OverworldProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.END_STONE);
            }}, null, 15, Rotation.CLOCKWISE_90),

    // Nether
    NETHER_MW("sg_nether", 2, true, SymbolTypeEnum.MILKYWAY, 15, 15, -1, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceNether, new NetherProcessor(),
            new ArrayList<Block>() {{
                add(Blocks.NETHERRACK);
                add(Blocks.QUARTZ_ORE);
                add(Blocks.NETHER_BRICK);
                add(Blocks.SOUL_SAND);
            }}, null, 15, Rotation.NONE),

    // ---------------------------------------------------------------------------
    // GENERAL STRUCTURES

    NAQUADAH_MINE("naquadah_mine", 8, false, null, 15, 15, 0, false, JSGConfig.stargateGeneratorConfig.structuresRandomGeneratorEnabled, 0.0008f,
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
            }}, null, 15, Rotation.NONE),
    ;

    public final JSGStructure structure;
    public final boolean randomGenEnable;
    public final float chance;
    public final List<String> allowedInBiomes;
    public final List<Block> allowedOnBlocks;
    public final int airCountUp;

    EnumStructures(String structureName, int yNegativeOffset, boolean isStargateStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int dimensionToSpawn, boolean findOptimalRotation, boolean randomGenEnable, float chanceToGenerateRandom, @Nullable List<Block> allowedOnBlocks, @Nullable List<String> allowedInBiomes, int airCountUp, Rotation rotationToNorth) {
        this(structureName, yNegativeOffset, isStargateStructure, symbolType, structureSizeX, structureSizeZ, dimensionToSpawn, findOptimalRotation, randomGenEnable, chanceToGenerateRandom, null, allowedOnBlocks, allowedInBiomes, airCountUp, rotationToNorth);
    }

    EnumStructures(String structureName, int yNegativeOffset, boolean isStargateStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int dimensionToSpawn, boolean findOptimalRotation, boolean randomGenEnable, float chanceToGenerateRandom, ITemplateProcessor templateProcessor, @Nullable List<Block> allowedOnBlocks, @Nullable List<String> allowedInBiomes, int airCountUp, Rotation rotationToNorth) {
        this.structure = new JSGStructure(structureName, yNegativeOffset, isStargateStructure, symbolType, structureSizeX, structureSizeZ, dimensionToSpawn, findOptimalRotation, templateProcessor, rotationToNorth);

        this.randomGenEnable = randomGenEnable;
        this.chance = chanceToGenerateRandom;

        this.allowedInBiomes = allowedInBiomes;
        this.allowedOnBlocks = allowedOnBlocks;
        this.airCountUp = airCountUp;
    }

    @Nullable
    public static EnumStructures getStargateStructureByBiome(String biomeName, SymbolTypeEnum symbolType, int dimensionToSpawn) {
        ArrayList<EnumStructures> biomeNull = new ArrayList<>();
        for (EnumStructures structure : EnumStructures.values()) {
            if (!structure.structure.isStargateStructure) continue;
            if (structure.structure.symbolType != symbolType) continue;
            if (structure.structure.dimensionToSpawn != dimensionToSpawn) continue;
            if (structure.allowedInBiomes != null) {
                for (String s : structure.allowedInBiomes) {
                    if (biomeName.toLowerCase().contains(s.toLowerCase())) return structure;
                }
            } else biomeNull.add(structure);
        }
        if (biomeNull.size() > 0) return biomeNull.get(0);
        return null;
    }
}
