package mrjake.aunis.worldgen.structures;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import mrjake.aunis.tileentity.dialhomedevice.DHDMilkyWayTile;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.Map;
import java.util.Random;


public class AunisStructure extends WorldGenerator{

    public String structureName;

    public AunisStructure(String name) {
        structureName = name;
    }


    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        Aunis.info("WTF???");
        EnumFacing facing = findOptimalRotation(worldIn, position);
        generateStructure(rand, worldIn, position, facing);
        return true;
    }

    public void generateStructure(Random rand, World world, BlockPos pos, EnumFacing facing){
        WorldServer worldServer = (WorldServer) world;
        MinecraftServer server = world.getMinecraftServer();

        MinecraftServer mcServer = world.getMinecraftServer();
        TemplateManager manager = worldServer.getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(Aunis.ModID, "sg_" + structureName);
        Template template = manager.getTemplate(mcServer, resourceLocation);

        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        Rotation rotation;
        switch (facing) {
            case SOUTH: rotation = Rotation.CLOCKWISE_90; break;
            case WEST:  rotation = Rotation.CLOCKWISE_180; break;
            case NORTH: rotation = Rotation.COUNTERCLOCKWISE_90; break;
            default:    rotation = Rotation.NONE; break;
        }
        PlacementSettings sett = (new PlacementSettings()).setRotation(rotation).setIgnoreStructureBlock(false);//.setIgnoreEntities(false);
        template.addBlocksToWorldChunk(world, pos, sett);


        // setup the stargate and make it work
        Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, sett);
        BlockPos gatePos = null;
        BlockPos dhdPos = null;
        boolean isMilkyWay;

        for (BlockPos dataPos : dataBlocks.keySet()) {
            String name = dataBlocks.get(dataPos);

            if (name.equals("base")) {
                gatePos = dataPos.add(0, -3, 0);
                TileEntity entity = world.getTileEntity(gatePos);
                if(entity != null) {
                    IItemHandler itemHandler = entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if(itemHandler != null)
                        itemHandler.insertItem(4, new ItemStack(AunisBlocks.CAPACITOR_BLOCK), false);

                    ((StargateAbstractBaseTile) entity).getMergeHelper().updateMembersBasePos(world, gatePos, facing);

                    world.setBlockToAir(dataPos);
                    world.setBlockToAir(dataPos.down()); // save block
                }
            }

            else if (name.equals("dhd")) {
                dhdPos = dataPos.down();

                if (rand.nextFloat() < AunisConfig.mysteriousConfig.despawnDhdChance) {
                    world.setBlockToAir(dhdPos);
                }

                else {
                    int fluid = AunisConfig.powerConfig.stargateEnergyStorage / AunisConfig.dhdConfig.energyPerNaquadah;

                    DHDAbstractTile dhdTile = (DHDAbstractTile) world.getTileEntity(dhdPos);
                    isMilkyWay = (dhdTile instanceof DHDMilkyWayTile);

                    ItemStack crystal = new ItemStack(isMilkyWay ? AunisItems.CRYSTAL_CONTROL_DHD : AunisItems.CRYSTAL_CONTROL_PEGASUS_DHD);
                    if(dhdTile != null) {
                        IItemHandler itemHandler = dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                        IFluidHandler fluidHandler = dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        if(itemHandler != null && fluidHandler != null) {
                            itemHandler.insertItem(0, crystal, false);
                            if (!isMilkyWay)
                                itemHandler.insertItem(1, new ItemStack(AunisItems.CRYSTAL_GLYPH_DHD), false);
                            ((FluidTank) fluidHandler).fillInternal(new FluidStack(AunisFluids.moltenNaquadahRefined, fluid), true);
                        }
                    }
                }

                world.setBlockToAir(dataPos);
            }
        }
        LinkingHelper.updateLinkedGate(world, gatePos, dhdPos);
        StargateClassicBaseTile gateTile = gatePos != null ? (StargateClassicBaseTile) world.getTileEntity(gatePos) : null;
        if(gateTile != null) {
            gateTile.refresh();
            gateTile.markDirty();
        }
    }

    private static final int MAX_CHECK = 100;
    private static EnumFacing findOptimalRotation(World world, BlockPos pos) {
        BlockPos start = pos.add(0, 5, 5);
        int max = -1;
        EnumFacing maxFacing = EnumFacing.EAST;

        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            RayTraceResult rayTraceResult = world.rayTraceBlocks(new Vec3d(start), new Vec3d(start.offset(facing, MAX_CHECK)));

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                int distance = (int) rayTraceResult.getBlockPos().distanceSq(start);
//				Aunis.info(facing.getName().toUpperCase() + ": distance: " + distance);

                if (distance > max) {
                    max = distance;
                    maxFacing = facing;
                }
            }

            else {
//				Aunis.info(facing.getName().toUpperCase() + ": null");

                max = 100000;
                maxFacing = facing;
            }
        }

//		Aunis.info("maxFacing: " + maxFacing.getName().toUpperCase());
        return maxFacing;
    }
}
