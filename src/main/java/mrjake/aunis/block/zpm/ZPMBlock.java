package mrjake.aunis.block.zpm;

import mrjake.aunis.Aunis;
import mrjake.aunis.AunisProps;
import mrjake.aunis.gui.GuiIdEnum;
import mrjake.aunis.stargate.power.StargateAbstractEnergyStorage;
import mrjake.aunis.stargate.power.StargateItemEnergyStorage;
import mrjake.aunis.tileentity.dialhomedevice.DHDMilkyWayTile;
import mrjake.aunis.tileentity.energy.ZPMTile;
import mrjake.aunis.util.AunisAxisAlignedBB;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ZPMBlock extends Block {

    private static final String blockName = "zpm";

    public ZPMBlock() {
        super(Material.IRON);

        setRegistryName(Aunis.ModID + ":" + blockName);
        setUnlocalizedName(Aunis.ModID + "." + blockName);

        setSoundType(SoundType.GLASS);
        setCreativeTab(Aunis.aunisEnergyCreativeTab);

        setLightOpacity(0);

        setHardness(3.0f);
        setHarvestLevel("pickaxe", 3);
    }

    protected static final int ENERGY_MAX = 100000000;

    public void setEnergy(ItemStack stack, int energy) {
        NBTTagCompound nbt;
        if (stack.hasTagCompound()) {
            nbt = stack.getTagCompound();
        } else {
            nbt = new NBTTagCompound();
        }
        nbt.setInteger("energy", energy);
        stack.setTagCompound(nbt);
    }

    public int getEnergy(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt.hasKey("energy")) {
                return nbt.getInteger("energy");
            }
            else {
                nbt.setInteger("energy", ENERGY_MAX);
                stack.setTagCompound(nbt);
            }
        }

        return ENERGY_MAX;

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Energy: " + getEnergy(stack) + "/" + ENERGY_MAX + "RF");
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        StargateAbstractEnergyStorage capacitorEnergyStorage = (StargateAbstractEnergyStorage) world.getTileEntity(pos).getCapability(CapabilityEnergy.ENERGY, null);

        ItemStack stack = new ItemStack(this);
        ((StargateItemEnergyStorage) stack.getCapability(CapabilityEnergy.ENERGY, null)).setEnergyStored(capacitorEnergyStorage.getEnergyStored());

        return Arrays.asList(stack);
    }



    // ------------------------------------------------------------------------
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AunisProps.ROTATION_HORIZONTAL, AunisProps.SNOWY);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AunisProps.ROTATION_HORIZONTAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AunisProps.ROTATION_HORIZONTAL, meta);
    }

    public final static BlockMatcher SNOW_MATCHER = BlockMatcher.forBlock(Blocks.SNOW_LAYER);

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(AunisProps.SNOWY, isSnowAroundBlock(world, pos));
    }

    public static boolean isSnowAroundBlock(IBlockAccess world, BlockPos inPos) {

        // Check if 4 adjacent blocks are snow layers
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos pos = inPos.offset(facing);
            if (!SNOW_MATCHER.apply(world.getBlockState(pos))) {
                return false;
            }
        }

        return true;
    }

    // ------------------------------------------------------------------------
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        // Server side
        if (!world.isRemote) {
            int facing = MathHelper.floor((double) ((placer.rotationYaw) * 16.0F / 360.0F) + 0.5D) & 0x0F;
            world.setBlockState(pos, state.withProperty(AunisProps.ROTATION_HORIZONTAL, facing), 3);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (!world.isRemote) {
            // Server
            if (!player.isSneaking()) {
                // Not sneaking
                player.openGui(Aunis.instance, GuiIdEnum.GUI_ZPM.id, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return !player.isSneaking();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
    }

    private int getPower(IBlockAccess world, BlockPos pos) {
        // todo return power by charge
        return 0;
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getPower(world, pos);
    }

    @Override
    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getPower(world, pos);
    }

    // ------------------------------------------------------------------------
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new ZPMTile();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AunisAxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return new AunisAxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    }
}
