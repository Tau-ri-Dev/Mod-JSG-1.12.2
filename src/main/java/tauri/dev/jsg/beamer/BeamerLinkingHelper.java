package tauri.dev.jsg.beamer;

import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.block.stargate.StargateClassicBaseBlock;
import tauri.dev.jsg.tileentity.BeamerTile;
import tauri.dev.jsg.tileentity.stargate.StargateClassicBaseTile;
import tauri.dev.jsg.util.FacingToRotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class BeamerLinkingHelper {
	
	private static final int SIDE = 2;
	private static final int DOWN = 6;
	
	public static void findGateInFrontAndLink(World world, BlockPos pos, EnumFacing blockFacing) {
		EnumFacing left = blockFacing.rotateYCCW();
		EnumFacing right = blockFacing.rotateY();
		
		Iterable<MutableBlockPos> blocks = BlockPos.getAllInBoxMutable(pos.offset(left, SIDE).down(DOWN).offset(blockFacing, 1), pos.offset(right, SIDE).down().offset(blockFacing, JSGConfig.beamerConfig.reach));
		
		for (MutableBlockPos scanPos : blocks) {
			IBlockState state = world.getBlockState(scanPos);
			
			if (state.getBlock() instanceof StargateClassicBaseBlock && state.getValue(JSGProps.FACING_HORIZONTAL) == blockFacing.getOpposite()) {
				EnumFacing baseFacing = state.getValue(JSGProps.FACING_HORIZONTAL);
				Rotation rotation = FacingToRotation.get(baseFacing);
				
				if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90)
					rotation = rotation.add(Rotation.CLOCKWISE_180);
				
				BlockPos vec = pos.subtract(scanPos).rotate(rotation);
				
				if (Math.abs(vec.getX()) == 2 && (vec.getY() == 1 || vec.getY() == 6))
					continue;
				
				// All checked
				StargateClassicBaseTile gateTile = (StargateClassicBaseTile) world.getTileEntity(scanPos);
				
				if (!gateTile.isMerged())
					continue;
				
				((BeamerTile) world.getTileEntity(pos)).setLinkedGate(scanPos, vec);
				gateTile.addLinkedBeamer(pos);
			}
		}
	}
	
	public static void findBeamersInFront(World world, BlockPos pos, EnumFacing blockFacing) {
		EnumFacing left = blockFacing.rotateYCCW();
		EnumFacing right = blockFacing.rotateY();
						
		Iterable<MutableBlockPos> blocks = BlockPos.getAllInBoxMutable(pos.offset(left, SIDE).up(DOWN).offset(blockFacing, 1), pos.offset(right, SIDE).up().offset(blockFacing, 9));
		
		for (MutableBlockPos scanPos : blocks) {
			IBlockState state = world.getBlockState(scanPos);
			
			if (state.getBlock() == JSGBlocks.BEAMER_BLOCK && state.getValue(JSGProps.FACING_HORIZONTAL) == blockFacing.getOpposite()) {
				EnumFacing beamerFacing = state.getValue(JSGProps.FACING_HORIZONTAL);
				Rotation rotation = FacingToRotation.get(beamerFacing);
				
				if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90)
					rotation = rotation.add(Rotation.CLOCKWISE_180);
				
				BlockPos vec = scanPos.subtract(pos).rotate(rotation);
				
				if (Math.abs(vec.getX()) == 2 && (vec.getY() == 1 || vec.getY() == 6))
					continue;
				
				// All checked
				((BeamerTile) world.getTileEntity(scanPos)).setLinkedGate(pos, vec);
				((StargateClassicBaseTile) world.getTileEntity(pos)).addLinkedBeamer(scanPos);
			}
		}
	}
	
}
