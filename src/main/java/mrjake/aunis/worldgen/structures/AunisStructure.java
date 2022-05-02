package mrjake.aunis.worldgen.structures;

import mrjake.aunis.Aunis;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.config.stargate.StargateSizeEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

import static mrjake.aunis.worldgen.StargateGenerator.generateStargate;


public class AunisStructure extends WorldGenerator implements IStructure{

    public String structureName;
    public int yNegativeOffset;
    boolean isStargateStructure;
    boolean isPegasusGate;
    int dimensionToSpawn;

    public int structureSizeX;
    public int structureSizeZ;

    public AunisStructure(String structureName, int yNegativeOffset, boolean isStargateStructure, boolean isPegasusGate, int structureSizeX, int structureSizeZ, int dimensionToSpawn) {
        this.structureName = structureName;
        this.yNegativeOffset = yNegativeOffset;
        this.isStargateStructure = isStargateStructure;
        this.isPegasusGate = isPegasusGate;
        this.structureSizeX = structureSizeX;
        this.structureSizeZ = structureSizeZ;
        this.dimensionToSpawn = dimensionToSpawn;
    }

    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        generateStructure(worldIn, position);
        return true;
    }

    public void generateStructure(World world, BlockPos pos){
        pos = pos.down(yNegativeOffset);
        WorldServer worldToSpawn = world.getMinecraftServer().getWorld(dimensionToSpawn);
        // generate stargate
        if(isStargateStructure && AunisConfig.stargateGeneratorConfig.stargateRandomGeneratorEnabled){
            String size = AunisConfig.stargateSize == StargateSizeEnum.LARGE ? "_large" : "_small";
            generateStargate(world, pos, "sg_" + structureName + size, isPegasusGate, dimensionToSpawn);
        }
        else if(AunisConfig.stargateGeneratorConfig.structuresRandomGeneratorEnabled) {

            // else generate normal structure
            MinecraftServer mcServer = world.getMinecraftServer();
            TemplateManager manager = worldToSpawn.getStructureTemplateManager();
            ResourceLocation resourceLocation = new ResourceLocation(Aunis.ModID, structureName);
            Template template = manager.getTemplate(mcServer, resourceLocation);

            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            template.addBlocksToWorldChunk(world, pos, defaultSettings);
        }
    }
}
