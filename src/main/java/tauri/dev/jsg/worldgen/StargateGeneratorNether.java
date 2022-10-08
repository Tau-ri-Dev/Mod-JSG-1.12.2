package tauri.dev.jsg.worldgen;

import tauri.dev.jsg.JSG;
import tauri.dev.jsg.config.JSGConfig;
import tauri.dev.jsg.util.main.JSGProps;
import tauri.dev.jsg.block.JSGBlocks;
import tauri.dev.jsg.config.stargate.StargateDimensionConfig;
import tauri.dev.jsg.config.stargate.StargateSizeEnum;
import tauri.dev.jsg.fluid.JSGFluids;
import tauri.dev.jsg.item.JSGItems;
import tauri.dev.jsg.stargate.merging.StargateMilkyWayMergeHelper;
import tauri.dev.jsg.stargate.network.StargateAddress;
import tauri.dev.jsg.stargate.network.SymbolTypeEnum;
import tauri.dev.jsg.tileentity.stargate.StargateMilkyWayBaseTile;
import tauri.dev.jsg.util.LinkingHelper;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Handles generating the Stargate structure in the Nether.
 * 
 * @author MrJake222
 */
public class StargateGeneratorNether {
	
	/**
	 * Directions check definitions
	 */
	private static final List<StargateGenerationHelper.Direction> DIRECTIONS = Arrays.asList(
			new StargateGenerationHelper.Direction(EnumFacing.UP, false, 0).setRequiredMinimum(12).setIgnoreInMaximum(),
			new StargateGenerationHelper.Direction(EnumFacing.NORTH, true, 2),
			new StargateGenerationHelper.Direction(EnumFacing.SOUTH, true, 2),
			new StargateGenerationHelper.Direction(EnumFacing.WEST, true, 2),
			new StargateGenerationHelper.Direction(EnumFacing.EAST, true, 2));
	
	/**
	 * Allowed blocks
	 */
	private static final List<BlockMatcher> ALLOWED_BLOCKS_BELOW = Arrays.asList(
			BlockMatcher.forBlock(Blocks.NETHERRACK),
			BlockMatcher.forBlock(Blocks.QUARTZ_ORE),
			BlockMatcher.forBlock(Blocks.SOUL_SAND));
	
	private static final ITemplateProcessor TEMPLATE_PROCESSOR = new StargateNetherTemplateProcessor();
	
	private static final int MINIMAL_FRONT_SPACE = 16;
	private static final int MINIMAL_SIDE_SPACE = 5;
	
	/**
	 * Searches for a place and spawns the Stargate structure.
	 * 
	 * @param world
	 * @param result
	 * @return
	 */
	@Nullable
	public static StargateAddress place(WorldServer world, BlockPos posIn) {
		BlockPos start = posIn;
		BlockPos current = start;
		int count = 0;
		int pass = 1;
		
		BlockPos found = null;
		Entry<EnumFacing, StargateGenerationHelper.DirectionResult> frontResult = null;
		
		while (found == null) {
			JSG.logger.debug("StargateGeneratorNether: count: " + count + ", pass: " + pass + ", current: " + current);
			
			for (MutableBlockPos pos : MutableBlockPos.getAllInBoxMutable(current, current.add(16, 16, 16))) {
				if (world.isAirBlock(pos.down()))
					continue;
				
				StargateGenerationHelper.FreeSpace freeSpace = StargateGenerationHelper.getFreeSpaceInDirections(world, pos, DIRECTIONS, 16, ALLOWED_BLOCKS_BELOW);
				
				if (freeSpace != null) {				
					if (freeSpace.getMaxDistance().getValue().distance >= MINIMAL_FRONT_SPACE) {
						int left = freeSpace.getDistance(freeSpace.getMaxDistance().getKey().rotateYCCW()).distance;
						int right = freeSpace.getDistance(freeSpace.getMaxDistance().getKey().rotateY()).distance;
						
						if (left >= MINIMAL_SIDE_SPACE && right >= MINIMAL_SIDE_SPACE) {
							found = pos.toImmutable();
							frontResult = freeSpace.getMaxDistance();
							
							break;
						}
					}
				}
			}
			
			if (count == 0)
				current = start.add(16, 0, 0);
			else if (count == 1)
				current = start.add(0, 0, 16);
			else if (count == 2)
				current = start.add(16, 0, 16);
			else if (count == 3) {
				if (start.getY() > 100) {
					start = posIn.add(32*pass, 0, 32*pass);
					pass++;
				}
				
				else {
					start = start.add(0, 16, 0);
				}
				
				current = start;
			}
			
			count++;
			
			if (count > 3)
				count = 0;
		}

		JSG.logger.debug("StargateGeneratorNether: /tp " + found.getX() + " " + found.getY() + " " + found.getZ());
		
		Rotation rotation;
		
		switch (frontResult.getKey()) {
			case SOUTH: rotation = Rotation.CLOCKWISE_180; break;
			case WEST:  rotation = Rotation.COUNTERCLOCKWISE_90; break;
			case NORTH: rotation = Rotation.NONE; break;
			case EAST:  rotation = Rotation.CLOCKWISE_90; break;
			default:    rotation = Rotation.NONE; break;
		}
		
		int y = Math.min(frontResult.getValue().ydiff, 0);
		y -= 2;
		
		BlockPos translate = new BlockPos(-7, y, -16).rotate(rotation);			
		found = found.add(translate);
		
		TemplateManager templateManager = world.getStructureTemplateManager();
		Template template = templateManager.getTemplate(world.getMinecraftServer(), new ResourceLocation(JSG.MOD_ID, "sg_nether_" + (JSGConfig.stargateSize == StargateSizeEnum.LARGE ? "large" : "small")));
		
		if (template != null) {
			PlacementSettings settings = new PlacementSettings().setIgnoreStructureBlock(false).setRotation(rotation);
			template.addBlocksToWorld(world, found, TEMPLATE_PROCESSOR, settings, 3);
			
			Map<BlockPos, String> datablocks = template.getDataBlocks(found, settings);

			BlockPos basePos = null;
			BlockPos dhdPos = null;
			
			for (Map.Entry<BlockPos, String> datablock : datablocks.entrySet()) {
				switch (datablock.getValue()) {
					case "base":
						basePos = datablock.getKey().down();
						world.setBlockState(datablock.getKey(), Blocks.NETHER_BRICK.getDefaultState());
						
						EnumFacing facing = world.getBlockState(basePos).getValue(JSGProps.FACING_HORIZONTAL);
						StargateMilkyWayMergeHelper.INSTANCE.updateMembersBasePos(world, datablock.getKey().down(), facing);
						IItemHandler itemHandler = world.getTileEntity(basePos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
						
						itemHandler.insertItem(4, new ItemStack(JSGBlocks.CAPACITOR_BLOCK), false);
						itemHandler.insertItem(0, new ItemStack(JSGItems.CRYSTAL_GLYPH_MILKYWAY), false);
						
						if (StargateDimensionConfig.netherOverworld8thSymbol())
							itemHandler.insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_STARGATE), false);
						
						break;
						
					case "dhd":
						dhdPos = datablock.getKey().down();
						world.setBlockToAir(datablock.getKey());
						
						int fluid = (int) (6000 + Math.random() * 3000);
						((FluidTank) world.getTileEntity(dhdPos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)).fillInternal(new FluidStack(JSGFluids.moltenNaquadahRefined, fluid), true);
						
						itemHandler = world.getTileEntity(dhdPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
						itemHandler.insertItem(0, new ItemStack(JSGItems.CRYSTAL_CONTROL_MILKYWAY_DHD), false);
						
						if (StargateDimensionConfig.netherOverworld8thSymbol())
							itemHandler.insertItem(1, new ItemStack(JSGItems.CRYSTAL_GLYPH_DHD), false);
						
						break;
				}
			}
						
			LinkingHelper.updateLinkedGate(world, basePos, dhdPos);
			StargateMilkyWayBaseTile gateTile = (StargateMilkyWayBaseTile) world.getTileEntity(basePos);
			
			return gateTile.getStargateAddress(SymbolTypeEnum.MILKYWAY);
		}
		
		return null;
	}
}
