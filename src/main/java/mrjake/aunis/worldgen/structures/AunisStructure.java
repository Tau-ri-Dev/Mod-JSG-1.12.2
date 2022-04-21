package mrjake.aunis.worldgen.structures;

import mrjake.aunis.Aunis;
import mrjake.aunis.block.AunisBlocks;
import mrjake.aunis.config.AunisConfig;
import mrjake.aunis.fluid.AunisFluids;
import mrjake.aunis.item.AunisItems;
import mrjake.aunis.stargate.network.StargateAddress;
import mrjake.aunis.stargate.network.StargatePos;
import mrjake.aunis.stargate.network.SymbolTypeEnum;
import mrjake.aunis.tileentity.dialhomedevice.DHDAbstractTile;
import mrjake.aunis.tileentity.stargate.StargateAbstractBaseTile;
import mrjake.aunis.tileentity.stargate.StargateClassicBaseTile;
import mrjake.aunis.util.LinkingHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.Map;
import java.util.Random;


public class AunisStructure extends WorldGenerator implements IStructure{

    public String structureName;
    public int minusY;

    public AunisStructure(String name, int yNegativeOffset) {
        structureName = name;
        minusY = yNegativeOffset;
    }


    @Override
    public boolean generate(World worldIn, Random rand, BlockPos position) {
        generateStructure(worldIn, position);
        return true;
    }


    // todo(Mine): Fix nullPointer of member block of the gate
    public void generateStructure(World world, BlockPos pos){
        pos = pos.down(minusY);
        EnumFacing facing = EnumFacing.EAST;
        Rotation rotation = Rotation.NONE;
        MinecraftServer mcServer = world.getMinecraftServer();
        TemplateManager manager = worldServer.getStructureTemplateManager();
        ResourceLocation resourceLocation = new ResourceLocation(Aunis.ModID, "sg_" + structureName);
        Template template = manager.getTemplate(mcServer, resourceLocation);

        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        PlacementSettings settings = (new PlacementSettings()).setRotation(rotation).setIgnoreStructureBlock(false).setIgnoreEntities(false);
        template.addBlocksToWorldChunk(world, pos, settings);



        Random rand = new Random();
        template.addBlocksToWorld(world, pos, settings);

        Map<BlockPos, String> datablocks = template.getDataBlocks(pos, settings);
        BlockPos gatePos = null;
        BlockPos dhdPos = null;

        for (BlockPos dataPos : datablocks.keySet()) {
            String name = datablocks.get(dataPos);

            if (name.equals("base")) {
                gatePos = dataPos.add(0, -3, 0);

                world.getTileEntity(gatePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(4, new ItemStack(AunisBlocks.CAPACITOR_BLOCK), false);

                ((StargateAbstractBaseTile) world.getTileEntity(gatePos)).getMergeHelper().updateMembersBasePos(world, gatePos, facing);

                world.setBlockToAir(dataPos);
                world.setBlockToAir(dataPos.down()); // save block
            }

            else if (name.equals("dhd")) {
                dhdPos = dataPos.down();

                if (rand.nextFloat() < AunisConfig.mysteriousConfig.despawnDhdChance) {
                    world.setBlockToAir(dhdPos);
                }

                else {
                    int fluid = AunisConfig.powerConfig.stargateEnergyStorage / AunisConfig.dhdConfig.energyPerNaquadah;

                    DHDAbstractTile dhdTile = (DHDAbstractTile) world.getTileEntity(dhdPos);

                    ItemStack crystal = new ItemStack(AunisItems.CRYSTAL_CONTROL_DHD);

                    dhdTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).insertItem(0, crystal, false);
                    ((FluidTank) dhdTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)).fillInternal(new FluidStack(AunisFluids.moltenNaquadahRefined, fluid), true);
                }

                world.setBlockToAir(dataPos);
            }
        }

        LinkingHelper.updateLinkedGate(world, gatePos, dhdPos);
        StargateClassicBaseTile gateTile = (StargateClassicBaseTile) world.getTileEntity(gatePos);
        gateTile.refresh();
        gateTile.markDirty();

        StargateAddress address = gateTile.getStargateAddress(SymbolTypeEnum.MILKYWAY);

        if(address != null && !gateTile.getNetwork().isStargateInNetwork(address))
            gateTile.getNetwork().addStargate(address, new StargatePos(world.provider.getDimensionType().getId(), gatePos, address));



        /*
        // setup the stargate and make it work
        Map<BlockPos, String> dataBlocks = template.getDataBlocks(pos, settings);
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
         */
    }
}
