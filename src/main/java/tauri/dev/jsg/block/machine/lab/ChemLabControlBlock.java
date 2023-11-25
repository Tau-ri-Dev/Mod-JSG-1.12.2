package tauri.dev.jsg.block.machine.lab;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.block.machine.AbstractMachineBlock;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.item.machine.MachineItemBlock;
import tauri.dev.jsg.renderer.machine.LabRenderer;
import tauri.dev.jsg.tileentity.machine.lab.LabTile;
import tauri.dev.jsg.util.JSGAxisAlignedBB;
import tauri.dev.jsg.util.main.JSGProps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class ChemLabControlBlock extends AbstractMachineBlock {
    public static final String BLOCK_NAME = "lab_machine_block";
    public static final int MAX_ENERGY = 9_000_000;
    public static final int MAX_ENERGY_TRANSFER = 20_000;

    public ChemLabControlBlock() {
        super(BLOCK_NAME);
        setDefaultState(blockState.getBaseState().withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.NORTH).withProperty(JSGProps.RENDER_BLOCK, true));
    }


    // --------------------------------------------------------------------------------------
    // Block states

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, JSGProps.FACING_HORIZONTAL, JSGProps.RENDER_BLOCK);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(JSGProps.RENDER_BLOCK) ? 0x04 : 0) | state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalIndex();
    }

    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(JSGProps.RENDER_BLOCK, (meta & 0x04) != 0).withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta & 0x03));
    }

    // --------------------------------------------------------------------------------------

    @Override
    public ItemBlock getItemBlock() {
        return new MachineItemBlock(this, BLOCK_NAME, MAX_ENERGY);
    }

    public void showGuiHandler(EntityPlayer player, EnumHand hand, World world, BlockPos pos) {
        showGui(player, hand, world, pos);
    }

    @Override
    protected void showGui(EntityPlayer player, EnumHand hand, World world, BlockPos pos) {
        player.openGui(JSG.instance, GuiIdEnum.GUI_LAB.id, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack stack = new ItemStack(this);
        return Collections.singletonList(stack);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void onBlockPlacedBy(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, EntityLivingBase placer, @Nonnull ItemStack stack) {
        EnumFacing facing = placer.getHorizontalFacing().getOpposite();
        state = state.withProperty(JSGProps.FACING_HORIZONTAL, facing);
        world.setBlockState(pos, state);
    }

    @Override
    public void breakBlock(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if (hasTileEntity(state)) {
            world.removeTileEntity(pos);
        }
    }

    @Override
    public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
        return new LabTile();
    }

    @Override
    public Class<? extends TileEntity> getTileEntityClass() {
        return LabTile.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return new LabRenderer();
    }

    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return new JSGAxisAlignedBB(0, 0, 0, 1, 0.8, 1);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return new JSGAxisAlignedBB(0, 0, 0, 1, 0.8, 1);
    }

    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        if (state.getValue(JSGProps.RENDER_BLOCK)) return EnumBlockRenderType.MODEL;
        else return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean renderHighlight(IBlockState state) {
        return false;
    }
}
