package tauri.dev.jsg.block.stargate;

import net.minecraft.block.state.BlockStateContainer;
import tauri.dev.jsg.JSG;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.gui.GuiIdEnum;
import tauri.dev.jsg.stargate.CamoPropertiesHelper;
import tauri.dev.jsg.stargate.EnumMemberVariant;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

import static tauri.dev.jsg.util.FacingHelper.getVerticalFacingFromPitch;

public abstract class StargateClassicBaseBlock extends StargateAbstractBaseBlock {
	
	public StargateClassicBaseBlock(String blockName) {
		super(blockName);
	}
	
	// --------------------------------------------------------------------------------------
	// Interactions
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		StargateClassicBaseTile gateTile = (StargateClassicBaseTile) world.getTileEntity(pos);
		EnumFacing facing = placer.getHorizontalFacing().getOpposite();
		EnumFacing verticalFacing = getVerticalFacingFromPitch(placer.rotationPitch);
		
		if (!world.isRemote) {			
			state = state.withProperty(JSGProps.FACING_HORIZONTAL, facing)
					.withProperty(JSGProps.FACING_VERTICAL, verticalFacing)
					.withProperty(JSGProps.RENDER_BLOCK, true);
		
			world.setBlockState(pos, state);
					
			gateTile.updateFacing(facing, verticalFacing, true);
			gateTile.updateMergeState(gateTile.getMergeHelper().checkBlocks(world, pos, facing), facing);
		}
	}

	@Override
	protected void showGateInfo(EntityPlayer player, EnumHand hand, World world, BlockPos pos) {
		StargateClassicBaseTile tile = (StargateClassicBaseTile) world.getTileEntity(pos);
		if(tile != null && tile.isMerged() && !tile.tryInsertUpgrade(player, hand)) {
			player.openGui(JSG.instance, GuiIdEnum.GUI_STARGATE.id, world, pos.getX(), pos.getY(), pos.getZ());
		}
	}

	// --------------------------------------------------------------------------------------
	// Block states

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, JSGProps.FACING_HORIZONTAL, JSGProps.FACING_VERTICAL, JSGProps.RENDER_BLOCK);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(JSGProps.RENDER_BLOCK) ? 0x04 : 0) | state.getValue(JSGProps.FACING_HORIZONTAL).getHorizontalIndex() | (state.getValue(JSGProps.FACING_VERTICAL).getIndex() == 0 ? 0x05 : state.getValue(JSGProps.FACING_VERTICAL).getIndex() == 1 ? 0x06 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(JSGProps.RENDER_BLOCK, (meta & 0x04) != 0).withProperty(JSGProps.FACING_HORIZONTAL, EnumFacing.getHorizontal(meta & 0x03)).withProperty(JSGProps.FACING_VERTICAL, (((meta & 0x05) != 0) ? EnumFacing.DOWN : ((meta & 0x06) != 0) ? EnumFacing.UP : EnumFacing.SOUTH));
	}

	@Override
	protected IBlockState createMemberState(IBlockState memberState, EnumFacing facing, EnumFacing facingVertical, int meta) {
		return memberState.withProperty(JSGProps.RENDER_BLOCK, true)
				.withProperty(JSGProps.FACING_HORIZONTAL, facing)
				.withProperty(JSGProps.FACING_VERTICAL, facingVertical)
				.withProperty(JSGProps.MEMBER_VARIANT, EnumMemberVariant.byId((meta >> 3) & 0x01));
	}

	// --------------------------------------------------------------------------------------
	// Rendering
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		return CamoPropertiesHelper.getStargateBlockBoundingBox(state, access, pos, false);
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess access, BlockPos pos) {
		return CamoPropertiesHelper.getStargateBlockBoundingBox(state, access, pos, true);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, isActualState);
	}
}
