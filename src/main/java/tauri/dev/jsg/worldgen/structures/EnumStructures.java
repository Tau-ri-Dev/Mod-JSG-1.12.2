package tauri.dev.jsg.worldgen.structures;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.worldgen.structures.stargate.processor.StargateNetherTemplateProcessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public enum EnumStructures {

    // ---------------------------------------------------------------------------
    // STARGATE STRUCTURES

    // Milkyway
    PLAINS_MW("sg_plains_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
                add(Blocks.DIRT);
                add(Blocks.STONE);
            }}, null),
    DESERT_MW("sg_desert_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
            new ArrayList<Block>() {{
                add(Blocks.SAND);
                add(Blocks.SANDSTONE);
            }},
            new ArrayList<String>() {{
                add("desert");
                add("mesa");
            }}),
    MOSSY_MW("sg_mossy_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
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
            }}),
    FROST_MW("sg_frosty_milkyway", 0, true, SymbolTypeEnum.MILKYWAY, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
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
            }}),
    // Pegasus
    PLAINS_PG("sg_plains_pegasus", -3, true, SymbolTypeEnum.PEGASUS, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
            new ArrayList<Block>() {{
        add(Blocks.GRASS);
        add(Blocks.DIRT);
        add(Blocks.STONE);
    }}, null),
    DESERT_PG("sg_desert_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
            new ArrayList<Block>() {{
        add(Blocks.SAND);
        add(Blocks.SANDSTONE);
    }},
            new ArrayList<String>() {{
        add("desert");
        add("mesa");
    }}),
    MOSSY_PG("sg_mossy_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
            new ArrayList<Block>() {{
        add(Blocks.GRASS);
        add(Blocks.DIRT);
        add(Blocks.STONE);
    }},
            new ArrayList<String>() {{
        add("taiga");
    }}),
    FROST_PG("sg_frosty_pegasus", 0, true, SymbolTypeEnum.PEGASUS, 15, 15, 0, true, JSGConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled, JSGConfig.stargateGeneratorConfig.stargateRGChanceOverworld,
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
    }}),
    // Universe
    END_UNI("sg_end_universe", 0, true, SymbolTypeEnum.PEGASUS, 15, 15, 1, true, false, 0, null, null),

    // Nether
    NETHER_MW("sg_nether", 0, true, SymbolTypeEnum.MILKYWAY, 10, 15, -1, false, false, 0, new StargateNetherTemplateProcessor(), null, null),

    // ---------------------------------------------------------------------------
    // GENERAL STRUCTURES

    NAQUADAH_MINE("naquadah_mine", 8, false, null, 15, 15, 0, false, JSGConfig.stargateGeneratorConfig.structuresRandomGeneratorEnabled, 0.08f,
            new ArrayList<Block>() {{
                add(Blocks.GRASS);
            }}, null),
    ;

    public final JSGStructure structure;
    public final boolean randomGenEnable;
    public final float chance;
    public final List<String> allowedInBiomes;
    public final List<Block> allowedOnBlocks;

    EnumStructures(String structureName, int yNegativeOffset, boolean isStargateStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int dimensionToSpawn, boolean findOptimalRotation, boolean randomGenEnable, float chanceToGenerateRandom, @Nullable List<Block> allowedOnBlocks, @Nullable List<String> allowedInBiomes) {
        this(structureName, yNegativeOffset, isStargateStructure, symbolType, structureSizeX, structureSizeZ, dimensionToSpawn, findOptimalRotation, randomGenEnable, chanceToGenerateRandom, null, allowedOnBlocks, allowedInBiomes);
    }
    EnumStructures(String structureName, int yNegativeOffset, boolean isStargateStructure, SymbolTypeEnum symbolType, int structureSizeX, int structureSizeZ, int dimensionToSpawn, boolean findOptimalRotation, boolean randomGenEnable, float chanceToGenerateRandom, ITemplateProcessor templateProcessor, @Nullable List<Block> allowedOnBlocks, @Nullable List<String> allowedInBiomes) {
        this.structure = new JSGStructure(structureName, yNegativeOffset, isStargateStructure, symbolType, structureSizeX, structureSizeZ, dimensionToSpawn, findOptimalRotation, templateProcessor);

        this.randomGenEnable = randomGenEnable;
        this.chance = chanceToGenerateRandom;

        this.allowedInBiomes = allowedInBiomes;
        this.allowedOnBlocks = allowedOnBlocks;
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
                    if (biomeName.contains(s)) return structure;
                }
            } else biomeNull.add(structure);
        }
        if (biomeNull.size() > 0) return biomeNull.get(0);
        return null;
    }
}
